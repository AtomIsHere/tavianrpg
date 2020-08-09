package com.taviannetwork.tavianrpg.commands;

import com.taviannetwork.tavianrpg.commands.data.CommandInfo;
import com.taviannetwork.tavianrpg.commands.data.CommandResult;
import com.taviannetwork.tavianrpg.commands.data.CommandSource;
import com.taviannetwork.tavianrpg.entity.CustomEntities;
import com.taviannetwork.tavianrpg.entity.CustomEntityType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(commandName = "spawn", description = "Spawn a custom entity.", usage = "/<command> <entity name> <x> <y> <z>", permission = "entity.spawn", source = CommandSource.PLAYER)
public class EntitySpawnCommand implements TavianCommand {
    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if(args.length != 4) {
            return CommandResult.INVALID_USAGE;
        }

        CustomEntityType<?> entity = CustomEntities.getByName(args[0]);
        if(entity == null) {
            sender.sendMessage(ChatColor.RED + "Invalid Entity");
            return CommandResult.SUCCESS;
        }

        int x;
        int y;
        int z;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch(NumberFormatException nfe) {
            sender.sendMessage(ChatColor.RED + nfe.getMessage());
            return CommandResult.FAILURE;
        }

        Player player = (Player) sender;

        entity.spawn(new Location(player.getWorld(), x, y, z));
        return CommandResult.SUCCESS;
    }
}
