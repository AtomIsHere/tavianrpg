package com.taviannetwork.tavianrpg.items.datatypes;

import com.taviannetwork.tavianrpg.items.EnchantmentMap;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class EnchantmentMapDataType implements PersistentDataType<byte[], EnchantmentMap> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<EnchantmentMap> getComplexType() {
        return EnchantmentMap.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull EnchantmentMap enchantmentMap, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(enchantmentMap);
            return baos.toByteArray();
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }

    @NotNull
    @Override
    public EnchantmentMap fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (EnchantmentMap) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
