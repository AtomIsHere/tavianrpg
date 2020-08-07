package com.taviannetwork.tavianrpg.commands;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.commands.data.CommandInfo;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import org.bukkit.Bukkit;

@Singleton
public class CommandManager {
    @Getter
    private final BaseCommand base = new BaseCommand("tavianrpg");

    @Inject
    private Injector injector;
    @Inject
    private TavianRPG plugin;

    public void registerCommand(TavianCommand command) {
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        if(info == null) {
            plugin.getLogger().severe("Could not register command " + command.getClass().getSimpleName() +", CommandInfo not found!");
            return;
        }

        base.registerCommand(new CommandContainer(command, info));
    }

    public void registerCommands(Package commandPackage) {
        try(ScanResult result =
                    new ClassGraph()
                            .verbose()
                            .enableAllInfo()
                            .acceptPackages(commandPackage.getName())
                            .scan()) {
           result.getClassesImplementing(TavianCommand.class.getName()).forEach(ci -> registerCommand(injector.getInstance(ci.loadClass(TavianCommand.class))));
        }

        Bukkit.getServer().getCommandMap().register("tavianrpg", base);
    }
}
