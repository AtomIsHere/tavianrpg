package com.taviannetwork.tavianrpg.entity;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import lombok.Getter;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

@Getter
public class CustomEntityType<T extends EntityLiving> {
    @Nullable private static Field REGISTRY_MAT_MAP;

    static {
        try {
            REGISTRY_MAT_MAP = RegistryMaterials.class.getDeclaredField("c");
        } catch(ReflectiveOperationException roe) {
            roe.printStackTrace();
            REGISTRY_MAT_MAP = null;
        }
    }

    private final MinecraftKey key;
    private final Class<T> clazz;
    private final EntityTypes.b<T> maker;
    private final EntityTypes<? super T> parentType;
    private final int networkId;
    private EntityTypes<T> entityType;
    private boolean registered;

    public CustomEntityType(String name, Class<T> customEntityClass, EntityTypes<? super T> parentType, EntityTypes.b<T> maker, int networkId) {
        this.key = MinecraftKey.a(name);
        this.clazz = customEntityClass;
        this.parentType = parentType;
        this.maker = maker;
        this.networkId = networkId;
    }

    public CustomEntityType(String name, Class<T> customEntityClass, EntityTypes<? super T> parentType, EntityTypes.b<T> maker) {
        this(name, customEntityClass, parentType, maker, IRegistry.ENTITY_TYPE.a(parentType));
    }

    public org.bukkit.entity.Entity spawn(Location loc) {
        Entity entity = entityType.spawnCreature(((CraftWorld) loc.getWorld()).getHandle(),
                null,
                null,
                null,
                new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                EnumMobSpawn.EVENT,
                true,
                false);

        return entity == null ? null : entity.getBukkitEntity();
    }

    @SuppressWarnings("unchecked")
    public void register() {
        if(registered || IRegistry.ENTITY_TYPE.getOptional(key).isPresent()) {
            throw new IllegalStateException(String.format("Unable to register entity with key %s as it is already registered.", key));
        }

        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY_TREE)
                .types();
        dataTypes.put(key.toString(), dataTypes.get(parentType.i().toString().replace("entities/", "")));

        EntityTypes.Builder<T> builder = EntityTypes.Builder.a(maker, EnumCreatureType.CREATURE);
        entityType = builder.a(key.getKey());
        IRegistry.a(IRegistry.ENTITY_TYPE, networkId, key.getKey(), entityType);
        registered = true;
    }

    @SuppressWarnings("unchecked")
    public void unregister() {
        if(!registered) {
            throw new IllegalArgumentException(String.format("Entity with key '%s' could not be unregistered, as it is not in the registry", key));
        }

        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY_TREE)
                .types();
        dataTypes.remove(key.getKey());
        try {
            if(REGISTRY_MAT_MAP == null) {
                throw new ReflectiveOperationException("Field not initially found");
            }

            REGISTRY_MAT_MAP.setAccessible(true);
            Object o = REGISTRY_MAT_MAP.get(IRegistry.ENTITY_TYPE);
            ((BiMap<MinecraftKey, ?>) o).remove(key);
            REGISTRY_MAT_MAP.set(IRegistry.ENTITY_TYPE, o);
            REGISTRY_MAT_MAP.setAccessible(false);
            registered = false;
        } catch(ReflectiveOperationException roe) {
            roe.printStackTrace();
        }
    }
}
