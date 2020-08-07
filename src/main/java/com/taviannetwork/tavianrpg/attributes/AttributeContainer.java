package com.taviannetwork.tavianrpg.attributes;

import lombok.Getter;
import org.bukkit.attribute.Attribute;

public class AttributeContainer {
    private final Attribute bukkitAttribute;
    private final CustomBukkitAttribute customAttribute;

    @Getter
    private final double modifier;

    private final boolean custom;

    public AttributeContainer(Attribute bukkitAttribute, double modifier) {
        this.bukkitAttribute = bukkitAttribute;
        this.customAttribute = null;

        this.modifier = modifier;

        this.custom = false;
    }

    public AttributeContainer(CustomBukkitAttribute customAttribute, double modifier) {
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
