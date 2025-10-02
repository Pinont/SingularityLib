package com.github.pinont.singularitylib.api.event;

import com.github.pinont.singularitylib.api.items.ItemInteraction;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

/**
 * Event that is fired when a player executes an item interaction.
 * This event allows for listening to and potentially cancelling item interactions.
 */
public class ItemExecuteEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final ItemStack item;
    private final ItemInteraction interaction;
    private final Action action;
    private boolean isCancelled;

    /**
     * Creates a new ItemExecuteEvent.
     *
     * @param event the original PlayerInteractEvent
     * @param item the ItemStack that was interacted with
     * @param interaction the ItemInteraction that will be executed
     */
    public ItemExecuteEvent(PlayerInteractEvent event, ItemStack item, ItemInteraction interaction) {
        this.player = event.getPlayer();
        this.item = item;
        this.interaction = interaction;
        this.action = event.getAction();
    }

    /**
     * Gets the handler list for this event.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        return !isCancelled();
    }

    /**
     * Executes the item interaction if the event is not cancelled.
     * Will consume the item if the player is not in creative mode.
     */
    public void execute() {
        if (callEvent()) {
            if (interaction.getAction().contains(getAction())) {
                interaction.execute(player);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    item.setAmount(item.getAmount() - interaction.removeItemAmountOnExecute());
                }
                sendDebugMessage("Executing interaction: " + interaction.getName());
            }
        }
    }

    /**
     * Gets the player who triggered this event.
     *
     * @return the player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the ItemStack that was interacted with.
     *
     * @return the ItemStack
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Gets the ItemInteraction that will be executed.
     *
     * @return the ItemInteraction
     */
    public ItemInteraction getInteraction() {
        return this.interaction;
    }

    /**
     * Gets the action that triggered this event.
     *
     * @return the Action
     */
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
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
