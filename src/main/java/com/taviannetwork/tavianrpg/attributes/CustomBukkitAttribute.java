package com.taviannetwork.tavianrpg.attributes;

import com.taviannetwork.tavianrpg.attributes.nms.CustomNMSAttribute;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.attribute.Attribute;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CustomBukkitAttribute {
    GENERIC_RPG_DAMAGE("generic.rpg_damage", CustomNMSAttribute.RPG_DAMAGE),
    GENERIC_RPG_STRENGTH("generic.rpg_strength", CustomNMSAttribute.RPG_STRENGTH),
    GENERIC_RPG_CRITICAL_DAMAGE("generic.rpg_critical_damage", CustomNMSAttribute.RPG_CRITICAL_DAMAGE),
    GENERIC_RPG_CRITICAL_CHANCE("generic.rpg_critical_chance", CustomNMSAttribute.RPG_CRITICAL_CHANCE),
    GENERIC_RPG_DEFENCE("generic.rpg_defence", CustomNMSAttribute.RPG_DEFENCE),
    GENERIC_RPG_INTELLIGENCE("generic.rpg_intelligence", CustomNMSAttribute.RPG_INTELLIGENCE);

    private static boolean injected = false;

    @Getter
    private final String key;
    @Getter
    private final AttributeBase handle;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Attribute bukkitAttribute;

    //Nooo you can't just create a dynamic instance of an enum
    //Haha internal classes go brrrr.
    public static void injectAttributes() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        if(injected) {
            throw new IllegalStateException("Attributes already injected!");
        }

        Class<Attribute> attributeClass = Attribute.class;
        Constructor<?> con = attributeClass.getDeclaredConstructors()[0];
        con.setAccessible(true);

        Field constructorAccessorField = Constructor.class.getDeclaredField("constructorAccessor");
        constructorAccessorField.setAccessible(true);

        Object ca = constructorAccessorField.get(con);
        if(ca == null) {
            Method acquireConstructorAccessorMethod = Constructor.class.getDeclaredMethod("acquireConstructorAccessor");
            acquireConstructorAccessorMethod.setAccessible(true);
            ca = acquireConstructorAccessorMethod.invoke(con);
        }

        Method newInstance = ca.getClass().getDeclaredMethod("newInstance", Class.forName("[L" + Object.class.getCanonicalName() + ";"));
        newInstance.setAccessible(true);

        Field valuesField = Attribute.class.getDeclaredField("$VALUES");
        makeAccessible(valuesField);

        Attribute[] oldValues = (Attribute[]) valuesField.get(null);
        Attribute[] newValues = new Attribute[oldValues.length + values().length];
        System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);

        int offset = oldValues.length;
        for(CustomBukkitAttribute customAttribute : values()) {
            Attribute enumValue = (Attribute) newInstance.invoke(ca, new Object[]{new Object[]{customAttribute.name(), offset + customAttribute.ordinal(), customAttribute.getKey()}}); // Ignore the array within array, it's to fix some weird reflections issue.
            newValues[offset + customAttribute.ordinal()] = enumValue;
            customAttribute.setBukkitAttribute(enumValue);
        }

        valuesField.set(null, newValues);

        injectNmsAttributes();

        injected = true;
    }

    private static void makeAccessible(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~ Modifier.FINAL);
    }

    @SuppressWarnings("unchecked")
    private static void injectNmsAttributes() throws NoSuchFieldException, IllegalAccessException {
        Field defaultEntityAttributesField = AttributeDefaults.class.getDeclaredField("b");
        defaultEntityAttributesField.setAccessible(true);
        Map<EntityTypes<? extends EntityLiving>, AttributeProvider> defaultEntityAttributes = (Map<EntityTypes<? extends EntityLiving>, AttributeProvider>) defaultEntityAttributesField.get(null);

        for(AttributeProvider target : defaultEntityAttributes.values()) {
            Field attributeMapField = AttributeProvider.class.getDeclaredField("a");
            makeAccessible(attributeMapField);

            ImmutableMap.Builder<AttributeBase, AttributeModifiable> mapBuilder = ImmutableMap.builder();
            mapBuilder.putAll((Map<AttributeBase, AttributeModifiable>) attributeMapField.get(target));

            Arrays.stream(values()).forEach(cba -> mapBuilder.put(cba.getHandle(), new AttributeModifiable(cba.getHandle(), def -> def.setValue(cba.getHandle().getDefault()))));

            attributeMapField.set(target, mapBuilder.build());
        }
    }
}
