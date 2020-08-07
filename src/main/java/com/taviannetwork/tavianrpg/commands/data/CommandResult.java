package com.taviannetwork.tavianrpg.commands.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommandResult {
    INVALID_USAGE(true),
    FAILURE(false),
    SUCCESS(true);
    
    private final boolean returns;

    public boolean getReturns() {
        return returns;
    }
}
