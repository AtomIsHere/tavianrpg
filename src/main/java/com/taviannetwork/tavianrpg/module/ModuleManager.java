package com.taviannetwork.tavianrpg.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class ModuleManager {
    private static boolean acceptingOffers = false;

    private static final Logger log = Logger.getLogger(ModuleManager.class.getName());
    private static final Map<Class<Module>, Module> offeredModules = new HashMap<>();

    @NotNull
    private final List<Module> modules = new ArrayList<>();

    @Inject
    private Injector injector;

    public static Map<Class<Module>, Module> getOfferedModules() {
        return offeredModules;
    }

    public void start() {
        log.info("Loading Modules!");
        loadModules();

        modules.forEach(Module::start);
    }

    public void stop() {
        modules.forEach(Module::stop);
        modules.clear();
    }

    public static void offerModule(Module module) {
        if(acceptingOffers) {
            offeredModules.put((Class<Module>) module.getClass(), module);
        } else {
            throw new IllegalArgumentException("No longer accepting modules.");
        }
    }

    private void loadModules() {
        acceptingOffers = false;
        for(Class<? extends Module> clazz : offeredModules.keySet()) {
            ModuleInfo info = clazz.getAnnotation(ModuleInfo.class);
            if(info == null) {
                log.severe(clazz.getSimpleName() + " has no module info!");
                continue;
            }

            log.info("Loading Module " + info + " v" + info.version() + " by " + info.version());
            if(Module.class.isAssignableFrom(clazz)) {
                Module module = offeredModules.get(clazz);
                injector.injectMembers(module);
                this.modules.add(module);
            } else {
                log.warning(clazz.getSimpleName() + " does not implement Module.");
            }

            offeredModules.clear();
            log.info("Loaded " + this.modules.size() + " modules!");
        }
    }
}
