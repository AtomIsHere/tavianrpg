package com.taviannetwork.tavianrpg.items.enums;

import com.taviannetwork.tavianrpg.attributes.CustomBukkitAttribute;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.attribute.Attribute;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CustomEnchant {
    TEST_ENCHANT("Test", new ItemType[]{ItemType.SWORD, ItemType.BOW}, 5, new EnchantAttributeContainer[]{new EnchantAttributeContainer(CustomBukkitAttribute.GENERIC_RPG_CRITICAL_DAMAGE, level -> level * 2), new EnchantAttributeContainer(Attribute.GENERIC_MAX_HEALTH, level -> level * 3)});

    @Getter
    private final String displayName;
    @Getter
    private final ItemType[] targetTypes;
    @Getter
    private final int maxLevel;
    @Getter
    private final EnchantAttributeContainer[] attributes;

    public static class EnchantAttributeContainer {
        private final Attribute bukkitAttribute;
        private final CustomBukkitAttribute customAttribute;

        @Getter
        private final Function<Integer, Integer> modifier;

        private final boolean custom;

        public EnchantAttributeContainer(Attribute bukkitAttribute, Function<Integer, Integer> modifier) {
            this.bukkitAttribute = bukkitAttribute;
            this.customAttribute = null;

            this.modifier = modifier;

            this.custom = false;
        }

        public EnchantAttributeContainer(CustomBukkitAttribute customAttribute, Function<Integer, Integer> modifier) {
            this.bukkitAttribute = null;
            this.customAttribute = customAttribute;

            this.modifier = modifier;

            this.custom = true;
        }

        @SuppressWarnings("ConstantConditions")
        public Attribute getAttribute() {
            if(custom) {
                return customAttribute.getBukkitAttribute();
            } else {
                return bukkitAttribute;
            }
        }
    }
}
