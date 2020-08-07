package com.taviannetwork.tavianrpg.damage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class RPGDamageEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final DamageSource source;
    private boolean cancelled = false;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void applyModifier(DamageModifier modifier) {
        modifier.accept(source);
    }
}
