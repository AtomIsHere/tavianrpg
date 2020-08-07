package com.taviannetwork.tavianrpg.services;

import org.jetbrains.annotations.NotNull;

public class NoSuchServiceException extends RuntimeException {
    public NoSuchServiceException(@NotNull Class<? extends Service> clazz) {
        super("Could not find service " + clazz.getSimpleName());
    }

}
