package com.taviannetwork.tavianrpg.attributes;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.atomic.AtomicLong;

@Singleton
@ServiceInfo(serviceName = "AttributeManager", serviceVersion = "1.0.0", serviceAuthor = "AtomIsHere")
//Custom Attributes are already injected into the Bukkit API through the black magic of internal jdk classes. This is primarily for handling mana and player attribute displays.
public class AttributeManager implements Service, Listener {
    private static final String ATTRIBUTE_DISPLAY_FORMAT = ChatColor.RED + "%o/%o❤    " + ChatColor.DARK_GREEN + "%o✤ Defence    " + ChatColor.AQUA + "%o/%o✎ Mana";

    @Inject
    private TavianRPG plugin;

    private NamespacedKey manaKey;

    private int tickCount = 0;

    @Override
    public void start() {
        manaKey = new NamespacedKey(plugin, "mana");

        AttributeDefaults.addDefaults(EntityTypes.PLAYER, EntityHuman.eo()
                .a(GenericAttributes.MAX_HEALTH, 100)
                .a(CustomBukkitAttribute.GENERIC_RPG_STRENGTH.getHandle(), 20)
                .a(CustomBukkitAttribute.GENERIC_RPG_INTELLIGENCE.getHandle(), 100)
                .a(CustomBukkitAttribute.GENERIC_RPG_CRITICAL_CHANCE.getHandle(), 10)
                .a(CustomBukkitAttribute.GENERIC_RPG_CRITICAL_DAMAGE.getHandle(), 10)
                .a(CustomBukkitAttribute.GENERIC_RPG_DEFENCE.getHandle(), 10)
                .a(CustomBukkitAttribute.GENERIC_RPG_DAMAGE.getHandle()));
    }

    @Override
    public void tick() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            if((tickCount % 20) == 0) {
                setMana(Math.min(getManaPool(player), getMana(player) + getManaRegen(player)), player);
            }

            long maxHealth = Math.round(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            long defence = Math.round(player.getAttribute(CustomBukkitAttribute.GENERIC_RPG_DEFENCE.getBukkitAttribute()).getValue());
            long maxMana = getManaPool(player);

            player.sendActionBar(TextComponent.fromLegacyText(String.format(ATTRIBUTE_DISPLAY_FORMAT, Math.round(player.getHealth()), maxHealth, defence, getMana(player), maxMana)));
        }

        tickCount++;
    }

    public Long getManaPool(Player player) {
        return Math.round(player.getAttribute(CustomBukkitAttribute.GENERIC_RPG_INTELLIGENCE.getBukkitAttribute()).getValue() + 100.0D);
    }

    public Long getManaRegen(Player player) {
        return Math.max(1, getManaPool(player) / 50);
    }

    public Long getMana(Player player) {
        if(!player.hasMetadata(manaKey.toString())) {
            setMana(0L, player);
            return 0L;
        }

        AtomicLong mana = new AtomicLong(0);
        player.getMetadata(manaKey.toString()).stream().findFirst().ifPresent(m -> mana.set(m.asLong()));
        return mana.get();
    }

    public void setMana(Long mana, Player player) {
        player.removeMetadata(manaKey.toString(), plugin);
        player.setMetadata(manaKey.toString(), new FixedMetadataValue(plugin, mana));
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.getPlayer().setHealthScale(40.0D);
        event.getPlayer().setHealthScaled(true);
    }
}
