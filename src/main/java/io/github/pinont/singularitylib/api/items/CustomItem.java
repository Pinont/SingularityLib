package io.github.pinont.singularitylib.api.items;

import io.github.pinont.singularitylib.api.utils.Common;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract base class for creating custom items with interactions.
 * Custom items combine an ItemCreator with an ItemInteraction to provide
 * both visual representation and functionality.
 */
public abstract class CustomItem {

    /**
     * Default constructor for CustomItem.
     */
    public CustomItem() {
    }

    /**
     * Gets the final ItemStack for this custom item.
     * This method combines the item creation and interaction registration.
     *
     * @return the complete ItemStack with interaction capabilities
     */
    public ItemStack getItem() {
        return register().addInteraction(getInteraction()).create();
    }

    /**
     * Gets the normalized name of this custom item.
     * The name is derived from the item's display name, with colors removed
     * and formatted as lowercase with underscores.
     *
     * @return the normalized item name
     */
    public String getName() {
        return Common.resetStringColor(getItem().getItemMeta().getDisplayName()).replace(" ", "_").toLowerCase();
    }

    /**
     * Gets the interaction behavior for this custom item.
     * Implement this method to define what happens when players interact with the item.
     *
     * @return the ItemInteraction that defines the item's behavior
     */
    public abstract ItemInteraction getInteraction();

    /**
     * Registers and configures the ItemCreator for this custom item.
     * Implement this method to define the item's appearance and properties.
     *
     * @return the ItemCreator that defines the item's visual properties
     */
    public abstract ItemCreator register();
}
