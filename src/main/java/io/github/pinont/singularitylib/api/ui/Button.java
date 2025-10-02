package io.github.pinont.singularitylib.api.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Interface representing a clickable button in a user interface.
 * Buttons can be placed in inventories and respond to player clicks.
 */
public interface Button {

    /**
     * Gets the slot position where this button should be placed.
     * Default implementation returns slot 0.
     *
     * @return the slot number (0-based index)
     */
    default int getSlot() {
        return 0;
    }

    /**
     * Gets the ItemStack that represents this button's visual appearance.
     *
     * @return the ItemStack to display for this button
     */
    ItemStack getItem();

    /**
     * Called when a player clicks on this button.
     *
     * @param player the player who clicked the button
     */
    void onClick(Player player);

}
