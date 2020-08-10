package com.taviannetwork.tavianrpg.damage;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.attributes.CustomBukkitAttribute;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

@Singleton
@ServiceInfo(serviceName = "DamageManager", serviceAuthor = "AtomIsHere", serviceVersion = "1.0.0")
public class DamageManager implements Service, Listener {
    private final List<DamageModifier> defaultDamageModifiers = new ArrayList<>();

    @Inject
    private TavianRPG plugin;

    @Override
    public void start() {
        createDefaultModifiers();
    }

    @Override
    public void stop() {
        defaultDamageModifiers.clear();
    }

    private void registerDefaultModifier(DamageModifier modifier) {
        defaultDamageModifiers.add(modifier);
    }

    private void createDefaultModifiers() {
        //Base Damage
        registerDefaultModifier((dm) -> {
            double strength = dm.getDamager().getAttribute(CustomBukkitAttribute.GENERIC_RPG_STRENGTH.getBukkitAttribute()).getValue();
            double damage = dm.getDamager().getAttribute(CustomBukkitAttribute.GENERIC_RPG_DAMAGE.getBukkitAttribute()).getValue();

            dm.setCurrentDamage((5.0D + damage + Math.floor(strength/5.0D)) * (1 + (strength/100)));
        });

        //Critical Damage
        registerDefaultModifier((dm) -> {
            double rand = Math.random();
            double critChance = dm.getDamager().getAttribute(CustomBukkitAttribute.GENERIC_RPG_CRITICAL_CHANCE.getBukkitAttribute()).getValue() / 100.0D;

            if(critChance >= rand) {
                dm.setCurrentDamage(dm.getCurrentDamage() * (1 + dm.getDamager().getAttribute(CustomBukkitAttribute.GENERIC_RPG_CRITICAL_DAMAGE.getBukkitAttribute()).getValue()));
                dm.setCritical(true);
            }
        });

        //Defence
        registerDefaultModifier((dm) -> {
            double defence = dm.getDamager().getAttribute(CustomBukkitAttribute.GENERIC_RPG_DEFENCE.getBukkitAttribute()).getValue();
            if(defence == 0.0D) {
                return;
            }

            double damageReduction = defence / (defence + 100);

            dm.setCurrentDamage(dm.getCurrentDamage() - (dm.getCurrentDamage() * damageReduction));
        });
    }

    private void createDamageDisplay(DamageSource source) {
        Location armorStandLoc = new Location(source.getDamaged().getWorld(),
                source.getDamaged().getLocation().getX() + 0.5D,
                source.getDamaged().getLocation().getY() + source.getDamaged().getHeight(),
                source.getDamaged().getLocation().getZ() + 0.5D,
                source.getDamager().getLocation().getYaw(),
                source.getDamager().getLocation().getPitch());

        ArmorStand stand = source.getDamaged().getWorld().spawn(armorStandLoc, ArmorStand.class, s -> setStandProperties(s, source.getCurrentDamage(), source.isCritical()));

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, stand::remove, 20L);
    }

    private void setStandProperties(ArmorStand stand, double damage, boolean crit) {
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setSmall(true);
        stand.setInvulnerable(true);
        stand.setCollidable(false);
        stand.setRemoveWhenFarAway(true);

        if(crit) {
            stand.setCustomName(ChatColor.GOLD + "✪ " + Math.round(damage) + " ✪");
        } else {
            stand.setCustomName(ChatColor.GRAY.toString() + Math.round(damage));
        }
        stand.setCustomNameVisible(true);
    }

    @EventHandler
    public void onEntityDamageEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof LivingEntity && event.getEntity() instanceof LivingEntity) {
            DamageSource source = new DamageSource((LivingEntity) event.getDamager(), (LivingEntity) event.getEntity());
            defaultDamageModifiers.forEach(dm -> dm.accept(source));

            RPGDamageEvent damageEvent = new RPGDamageEvent(source);
            Bukkit.getPluginManager().callEvent(damageEvent);
            if(damageEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }

            createDamageDisplay(source);

            event.setDamage(source.getCurrentDamage());
        }
    }
}
