package com.taviannetwork.tavianrpg.worlds.wrapper;

public class WorldNotFoundException extends Exception {
    public WorldNotFoundException(String name) {
        super("Could not find world, " + name + "!");
    }
}
