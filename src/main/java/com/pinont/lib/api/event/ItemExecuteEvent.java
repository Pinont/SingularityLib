package com.pinont.lib.api.event;

import com.pinont.lib.api.items.ItemInteraction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.pinont.lib.plugin.CorePlugin.sendDebugMessage;

public class ItemExecuteEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final ItemStack item;
    private final ItemInteraction interaction;
    private final Action action;
    private boolean isCancelled;

    public ItemExecuteEvent(PlayerInteractEvent event, ItemStack item, ItemInteraction interaction) {
        this.player = event.getPlayer();
        this.item = item;
        this.interaction = interaction;
        this.action = event.getAction();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        return !isCancelled();
    }

    public void execute() {
        if (callEvent()) {
            if (interaction.getAction().contains(getAction())) {
                item.setAmount(item.getAmount() - interaction.removeItemAmountOnExecute());
                sendDebugMessage("Executing interaction: " + interaction.getName());
                interaction.execute(player);
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ItemInteraction getInteraction() {
        return this.interaction;
    }

    public Action getAction() {
        return this.action;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
