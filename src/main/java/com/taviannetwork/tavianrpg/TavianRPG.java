package com.taviannetwork.tavianrpg;

import com.taviannetwork.tavianrpg.attributes.AttributeManager;
import com.taviannetwork.tavianrpg.attributes.CustomBukkitAttribute;
import com.taviannetwork.tavianrpg.commands.TavianCommand;
import com.taviannetwork.tavianrpg.commands.CommandManager;
import com.taviannetwork.tavianrpg.damage.DamageManager;
import com.taviannetwork.tavianrpg.entity.CustomEntities;
import com.taviannetwork.tavianrpg.items.ItemManager;
import com.taviannetwork.tavianrpg.module.ModuleManager;
import com.taviannetwork.tavianrpg.services.ServiceManager;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

@Singleton
public final class TavianRPG extends JavaPlugin {
    private Injector injector;

    @Inject
    private ModuleManager moduleManager;
    @Inject
    private ServiceManager serviceManager;
    @Inject
    private CommandManager commandManager;

    @Override
    public void onLoad() {
        try {
            CustomBukkitAttribute.injectAttributes();
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
            getPluginLoader().disablePlugin(this);
        }

        CustomEntities.register();
    }

    @Override
    public void onEnable() {
        TavianRPGModule module = new TavianRPGModule(this);
        injector = module.createInjector();
        injector.injectMembers(this);

        createDefaultServices();

        try {
            moduleManager.start();
            serviceManager.start();
            getLogger().info("Services enabled");
        } catch(Exception ex) {
            getLogger().severe("Exception encountered while enabling AtomRPG: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        commandManager.registerCommands(TavianCommand.class.getPackage());
        commandManager.getBase().buildHelpMessage();

        Bukkit.getServer().getCommandMap().register("atomrpg", commandManager.getBase());
    }

    @Override
    public void onDisable() {
        try {
            moduleManager.stop();
            serviceManager.stop();
        } catch(Exception ex) {
            getLogger().severe("Exception encountered while disabling AtomRPG: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        commandManager.getBase().clearCommands();

        injector = null;
    }

    public void createDefaultServices() {
        //serviceManager.createService(SQLConnectionManager.class);
        //serviceManager.createService(SQLManager.class);
        serviceManager.createService(ItemManager.class);
        serviceManager.createService(DamageManager.class);
        serviceManager.createService(AttributeManager.class);
        serviceManager.createService(TestService.class);
    }
}
