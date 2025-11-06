package com.github.pinont.singularitylib.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageByPlayerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;
    private final Player player;
    private final Player damager;

    public PlayerDamageByPlayerEvent(Player player, Player hitter) {
        this.player = player;
        this.damager = hitter;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Player getDamager() {
        return this.damager;
    }

    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        return this.isCancelled();
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
