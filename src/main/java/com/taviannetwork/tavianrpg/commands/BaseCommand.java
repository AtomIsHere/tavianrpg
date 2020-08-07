package com.taviannetwork.tavianrpg.commands;

import com.taviannetwork.tavianrpg.commands.data.CommandResult;
import com.taviannetwork.tavianrpg.commands.data.CommandSource;
import com.taviannetwork.tavianrpg.utils.ChatUtils;
import com.google.inject.Singleton;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Singleton
public class BaseCommand extends Command {
    public static final int MAX_HELP_LINES = 5;

    private final Map<String, CommandContainer> commands = new HashMap<>();
    private final List<List<String>> helpPages = new ArrayList<>();

    public BaseCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 0) {
            displayHelpMessage(commandSender, 0);
            return true;
        } else if(args[0].equalsIgnoreCase("help")) {
            if(args.length == 1) {
                displayHelpMessage(commandSender, 0);
            } else {
                int pageNumber;
                try {
                    pageNumber = Integer.parseInt(args[1]) - 1;
                } catch(NumberFormatException nfe) {
                    commandSender.sendMessage(ChatColor.RED + "Not a number!");
                    return true;
                }

                displayHelpMessage(commandSender, pageNumber);
            }

            return true;
        }

        CommandContainer command = commands.get(args[0]);
        if(command == null) {
            displayHelpMessage(commandSender, 0);
            return true;
        }

        if(!hasPermission(commandSender, command)) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
        }

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        CommandResult result = command.getExecutor().execute(commandSender, commandArgs);
        if(result.equals(CommandResult.INVALID_USAGE)) {
            commandSender.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", command.getCommandName()));
        }

        return result.getReturns();
    }

    private boolean hasPermission(CommandSender sender, CommandContainer command) {
        switch(command.getSource()) {
            case PLAYER:
                if(!(sender instanceof Player)) {
                    return false;
                }

                return sender.hasPermission(command.getPermission()) || sender.isOp();
            case CONSOLE:
                return !(sender instanceof Player);
            case BOTH:
                if(!(sender instanceof Player)) {
                    return true;
                }

                return sender.hasPermission(command.getPermission()) || sender.isOp();
            default:
                return false;
        }
    }

    public void clearCommands() {
        this.commands.clear();
    }

    public void registerCommand(CommandContainer container) {
        if(container.getCommandName().equalsIgnoreCase("help")) {
            throw new IllegalArgumentException("Can not register command with name \"help\"");
        }

        commands.put(container.getCommandName(), container);
    }

    public void buildHelpMessage() {
        helpPages.clear();

        List<String> currentPage = new ArrayList<>();
        for(CommandContainer command : commands.values()) {
            if(currentPage.size() >= MAX_HELP_LINES) {
                helpPages.add(currentPage);
                currentPage = new ArrayList<>();
            }

            currentPage.add(ChatColor.BLUE + command.getCommandName() + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + command.getDescription());
        }
    }

    public void displayHelpMessage(CommandSender sender, int pageIndex) {
        List<String> page;
        try {
            page = helpPages.get(pageIndex);
        } catch(ArrayIndexOutOfBoundsException aiooe) {
            sender.sendMessage(ChatColor.RED + "Page does not exist!");
            return;
        }

        ChatUtils.sendCenteredMessage(sender, ChatColor.AQUA + "TavianRPG Help " + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + pageIndex + 1 + "/" + helpPages.size() + ChatColor.DARK_GRAY + ")");
        page.forEach(sender ::sendMessage);
    }
}
