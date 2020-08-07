package com.taviannetwork.tavianrpg.commands;

import com.taviannetwork.tavianrpg.commands.data.CommandResult;

import org.bukkit.command.CommandSender;

public interface TavianCommand {
    CommandResult execute(CommandSender sender, String[] args);
}
