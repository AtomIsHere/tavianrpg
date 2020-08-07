package com.taviannetwork.tavianrpg.items.enums;

import com.taviannetwork.tavianrpg.attributes.AttributeContainer;
import com.taviannetwork.tavianrpg.attributes.CustomBukkitAttribute;
import com.taviannetwork.tavianrpg.items.datatypes.EnumDataType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CustomItemRegistry {
    VANILLA_ITEM(null, null, ItemRarity.COMMON, ItemType.VANILLA, false, false, new AttributeContainer[]{}), //Place Holder for vanilla items.
    TEST_SWORD("Test Sword", Material.WOODEN_SWORD, ItemRarity.RARE, ItemType.SWORD, true, true, new AttributeContainer[]{new AttributeContainer(CustomBukkitAttribute.GENERIC_RPG_DAMAGE, 50.0D), new AttributeContainer(Attribute.GENERIC_MAX_HEALTH, 10.0D)});

    public static final EnumDataType<CustomItemRegistry> DATA_TYPE = new EnumDataType<>(CustomItemRegistry.class);

    @Getter
    private final String displayName;
    @Getter
    private final Material base;
    @Getter
    private final ItemRarity rarity;
    @Getter
    private final ItemType type;
    @Getter
    private final boolean reforgeable;
    @Getter
    private final boolean enchantable;
    @Getter
    private final AttributeContainer[] attributes;
}
