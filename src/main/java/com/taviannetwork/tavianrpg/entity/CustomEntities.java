package com.taviannetwork.tavianrpg.entity;

import net.minecraft.server.v1_16_R1.AttributeDefaults;
import net.minecraft.server.v1_16_R1.AttributeProvider;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.EntityTypes;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class CustomEntities {
    private static boolean registered = false;

    public static final CustomEntityType<DummyZombie> DUMMY_ZOMBIE = new CustomEntityType<>("dummy_zombie", DummyZombie.class, EntityTypes.ZOMBIE, DummyZombie::new);

    private CustomEntities() {
        throw new AssertionError();
    }

    @Nullable
    public static CustomEntityType<?> getByName(String name) {
        Field foundField;
        try {
            foundField = CustomEntities.class.getField(name);
        } catch (NoSuchFieldException nsfe) {
            return null;
        }

        Object obj;
        try {
            obj = foundField.get(null);
        } catch (IllegalAccessException iae) {
            return null;
        }

        if(!(obj instanceof CustomEntityType)) {
            return null;
        }

        return (CustomEntityType<?>) obj;
    }

    @SuppressWarnings("unchecked")
    public static void register() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(registered) {
            throw new IllegalStateException("Custom entities are already registered");
        }

        for(Field field : CustomEntities.class.getDeclaredFields()) {
            if(Modifier.isStatic(field.getModifiers())
                    && Modifier.isPublic(field.getModifiers())
                    && field.getType().equals(CustomEntityType.class)) {
                Object obj = field.get(null);

                if(!(obj instanceof CustomEntityType)) {
                    return;
                }

                CustomEntityType<? extends EntityLiving> entityType = (CustomEntityType<? extends LivingEntity>) obj;
                entityType.register();

                Method attributeBuilderMethod = entityType.getClazz().getDeclaredMethod("getDefaultAttributes");
                AttributeProvider.Builder builder = (AttributeProvider.Builder) attributeBuilderMethod.invoke(null);

                AttributeDefaults.addDefaults(entityType.getEntityType(), builder);
            }
        }

        registered = true;
    }

    public static void unregister() {
        if(!registered) {
            throw new IllegalStateException("Custom entities are already registered");
        }

        Arrays.stream(CustomEntities.class.getFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> Modifier.isPublic(f.getModifiers()))
                .filter(f -> f.getType().equals(CustomEntityType.class))
                .forEach(f -> {
                    Object obj;
                    try {
                        obj = f.get(null);
                    } catch (IllegalAccessException iae) {
                        iae.printStackTrace();
                        return;
                    }

                    if(!(obj instanceof CustomEntityType)) {
                        return;
                    }

                    ((CustomEntityType<?>) obj).unregister();
                });
    }
}
