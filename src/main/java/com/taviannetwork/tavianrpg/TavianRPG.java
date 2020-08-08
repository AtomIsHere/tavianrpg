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

@Singleton
public final class TavianRPG extends JavaPlugin {
    private boolean loadingFailed = false;

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
            CustomEntities.register();
        } catch (Exception e) {
            e.printStackTrace();
            loadingFailed = true;
        }

    }

    @Override
    public void onEnable() {
        if(loadingFailed) {
            getLogger().severe("Loading failed, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
        if(loadingFailed) {
            return;
        }

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
