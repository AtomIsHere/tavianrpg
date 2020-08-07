package com.taviannetwork.tavianrpg.items.datatypes;

import lombok.RequiredArgsConstructor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class EnumDataType<T extends Enum<T>> implements PersistentDataType<byte[], T> {
    private final Class<T> enumClass;

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<T> getComplexType() {
        return enumClass;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull T enumType, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return enumType.name().getBytes(StandardCharsets.UTF_8);
    }

    @NotNull
    @Override
    public T fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return Enum.valueOf(enumClass, new String(bytes, StandardCharsets.UTF_8));
    }
}
