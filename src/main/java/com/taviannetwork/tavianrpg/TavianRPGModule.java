package com.taviannetwork.tavianrpg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.*;
import com.taviannetwork.tavianrpg.module.ModuleManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;


@AllArgsConstructor
public class TavianRPGModule extends AbstractModule {
    private final TavianRPG plugin;

    @NonNull
    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(TavianRPG.class).toInstance(plugin);

        ModuleManager.getOfferedModules().forEach((k, v) -> bind(k).toInstance(v));
    }

    @Provides
    @Singleton
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
