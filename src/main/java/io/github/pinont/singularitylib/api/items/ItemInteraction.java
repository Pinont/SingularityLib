package io.github.pinont.singularitylib.api.items;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Set;

/**
 * Interface for defining custom item interactions.
 * Items with interactions can respond to player clicks and actions.
 */
public interface ItemInteraction {

    /**
     * Gets the number of items to remove when this interaction is executed.
     * Default implementation returns 0 (no items consumed).
     *
     * @return the number of items to remove from the stack
     */
    default int removeItemAmountOnExecute() {
        return 0;
    }

    /**
     * Gets the name of this interaction.
     *
     * @return the interaction name
     */
    String getName();

    /**
     * Gets the set of actions that trigger this interaction.
     *
     * @return the set of triggering actions
     */
    Set<Action> getAction();

    /**
     * Executes this interaction for the given player.
     *
     * @param player the player who triggered the interaction
     */
    void execute(Player player);

    /**
     * Whether to cancel the original event when this interaction is executed.
     * Default implementation returns true.
     *
     * @return true to cancel the event, false to allow it to continue
     */
    default boolean cancelEvent() {
        return true;
    }

}
