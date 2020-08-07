package com.taviannetwork.tavianrpg;

import com.taviannetwork.tavianrpg.module.ModuleManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
}
