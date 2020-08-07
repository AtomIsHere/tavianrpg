package com.taviannetwork.tavianrpg.commands;

import com.taviannetwork.tavianrpg.commands.data.CommandInfo;

import com.taviannetwork.tavianrpg.commands.data.CommandSource;
import lombok.Getter;

public class CommandContainer {
    public static final String PERMISSION_PREFIX = "atomrpg.command.";

    @Getter
    private final TavianCommand executor;

    @Getter
    private final String commandName;
    @Getter
    private final String description;
    @Getter
    private final String usage;

    @Getter
    private final String permission;
    @Getter
    private final CommandSource source;

    public CommandContainer(TavianCommand executor, CommandInfo info) {
        this.executor = executor;

        this.commandName = info.commandName();
        this.description = info.description();
        this.usage = info.usage();

        this.permission = PERMISSION_PREFIX + info.permission();
        this.source = info.source();
    }
}