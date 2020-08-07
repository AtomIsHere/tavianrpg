package com.taviannetwork.tavianrpg.attributes;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.atomic.AtomicLong;

@Singleton
@ServiceInfo(serviceName = "AttributeManager", serviceVersion = "1.0.0", serviceAuthor = "AtomIsHere")
//Custom Attributes are already injected into the Bukkit API through the black magic of internal jdk classes. This is primarily for handling mana and player attribute displays.
public class AttributeManager implements Service, Listener {
    private static final String ENTITY_NAME_FORMAT = ChatColor.WHITE + "%s " + ChatColor.GRAY + "[%o/%o]";

    @Inject
    private TavianRPG plugin;

    private NamespacedKey manaKey;

    @Override
    public void start() {
        manaKey = new NamespacedKey(plugin, "mana");
    }

    public Long getManaPool(Player player) {
        return Math.round(player.getAttribute(CustomBukkitAttribute.GENERIC_RPG_INTELLIGENCE.getBukkitAttribute()).getBaseValue() + 100.0D);
    }

    public Long getManaRegen(Player player) {
        return Math.max(1, getManaPool(player) / 50);
    }

    public Long getMana(Player player) {
        AtomicLong mana = new AtomicLong(0);
        player.getMetadata(manaKey.toString()).stream().findFirst().ifPresent(m -> mana.set(m.asLong()));
        return mana.get();
    }

    public void setMana(Long mana, Player player) {
        player.removeMetadata(manaKey.toString(), plugin);
        player.setMetadata(manaKey.toString(), new FixedMetadataValue(plugin, mana));
    }

    /* TODO: Fix Entity Display (Will probably just include this in CustomEntities)
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player) && !(event.getEntity() instanceof ArmorStand)) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            Long maxHealth = Math.round(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());

            entity.setCustomName(String.format(ENTITY_NAME_FORMAT, entity.getName(), Math.round(entity.getHealth()), maxHealth));
            entity.setCustomNameVisible(true);
        }
    }


    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player) && !(event.getEntity() instanceof ArmorStand)) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            Long maxHealth = Math.round(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());

            String name = ChatColor.stripColor(entity.getName()).split(Pattern.quote(" ["))[0];

            entity.setCustomName(String.format(ENTITY_NAME_FORMAT, name, Math.round(entity.getHealth() - event.getDamage()), maxHealth));
        }
    }
     */
}
