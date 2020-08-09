package com.taviannetwork.tavianrpg.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ReflectionUtils {
    private ReflectionUtils() {
        throw new AssertionError();
    }

    public static void makeAccessible(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~ Modifier.FINAL);
    }
}
