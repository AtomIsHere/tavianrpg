package com.taviannetwork.tavianrpg.entity.adapters;

import org.bukkit.ChatColor;

public final class AdapterUtils {
    private static final String NAME_FORMAT = ChatColor.GRAY + "[" + "Lvl " + "%o" + "]" + ChatColor.WHITE + " %s " + ChatColor.GRAY + "[" + ChatColor.RED + "%o" + ChatColor.WHITE + "/" + ChatColor.RED + "%o" + ChatColor.GRAY + "]";

    private AdapterUtils() {
        throw new AssertionError();
    }

    public static String getDisplayName(CustomEntityAdapter<?> adapter) {
        return String.format(NAME_FORMAT, adapter.getLevel(), adapter.getBaseName(), (int) adapter.get().getHealth(), (int) adapter.get().getMaxHealth());
    }
}
