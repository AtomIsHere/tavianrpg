package com.taviannetwork.tavianrpg.items.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ItemRarity {
    COMMON(ChatColor.WHITE, "Common", 1.0D),
    UNCOMMON(ChatColor.GREEN, "Uncommon", 1.25D),
    RARE(ChatColor.BLUE, "Rare", 1.5D),
    EPIC(ChatColor.DARK_PURPLE, "Epic",  1.75D),
    LEGENDARY(ChatColor.GOLD, "Legendary", 2.0D),
    MYTHIC(ChatColor.LIGHT_PURPLE, "Mythic", 3.0D);

    @Getter
    private final ChatColor rarityColor;
    @Getter
    private final String display;
    @Getter
    private final double reforgeMultiplier;
}
