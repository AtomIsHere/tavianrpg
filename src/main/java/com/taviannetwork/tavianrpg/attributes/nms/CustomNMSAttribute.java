package com.taviannetwork.tavianrpg.attributes.nms;

import net.minecraft.server.v1_16_R1.AttributeBase;
import net.minecraft.server.v1_16_R1.AttributeRanged;
import net.minecraft.server.v1_16_R1.IRegistry;

public final class CustomNMSAttribute {
    public static final AttributeBase RPG_DAMAGE = registerCustomAttribute("generic.rpg_damage", new AttributeRanged("attribute.name.generic.rpg_damage", 0.0D, 0.0D, 10000.0D));
    public static final AttributeBase RPG_STRENGTH = registerCustomAttribute("generic.rpg_strength", new AttributeRanged("attribute.name.generic.rpg_strength", 0.0D, 0.0D, 10000.0D));
    public static final AttributeBase RPG_CRITICAL_DAMAGE = registerCustomAttribute("generic.rpg_critical_damage", new AttributeRanged("attribute.name.generic.rpg_critical_damage", 0.0D, 0.0D, 10000.0D));
    public static final AttributeBase RPG_CRITICAL_CHANCE = registerCustomAttribute("generic.rpg_critical_chance", new AttributeRanged("attribute.name.generic.rpg_critical_chance", 0.0D, 0.0D, 100.0D));
    public static final AttributeBase RPG_DEFENCE = registerCustomAttribute("generic.rpg_defence", new AttributeRanged("attribute.name.generic.rpg_defence", 0.0D, 0.0D, 10000.0D));
    public static final AttributeBase RPG_INTELLIGENCE = registerCustomAttribute("generic.rpg_intelligence", new AttributeRanged("attribute.name.generic.rpg_intelligence", 100.0D, 0.0D, 10000.0D));

    private CustomNMSAttribute() {
        throw new AssertionError();
    }

    private static AttributeBase registerCustomAttribute(String id, AttributeBase base) {
        return IRegistry.a(IRegistry.ATTRIBUTE, id, base);
    }
}
