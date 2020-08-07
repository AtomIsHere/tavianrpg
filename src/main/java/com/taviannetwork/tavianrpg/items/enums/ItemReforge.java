package com.taviannetwork.tavianrpg.items.enums;

import com.taviannetwork.tavianrpg.attributes.AttributeContainer;
import com.taviannetwork.tavianrpg.attributes.CustomBukkitAttribute;
import com.taviannetwork.tavianrpg.items.datatypes.EnumDataType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.attribute.Attribute;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ItemReforge {
    TEST("Test", ItemType.SWORD, 10, new AttributeContainer[]{new AttributeContainer(CustomBukkitAttribute.GENERIC_RPG_DAMAGE, 20.0D), new AttributeContainer(Attribute.GENERIC_MAX_HEALTH, 10.0D)});

    public static final EnumDataType<ItemReforge> DATA_TYPE = new EnumDataType<>(ItemReforge.class);

    @Getter
    private final String displayName;
    @Getter
    private final ItemType targetType;
    @Getter
    private final int weight;

    @Getter
    private final AttributeContainer[] attributes;
}
