package com.github.pinont.singularitylib.api.items;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Set;

import static com.github.pinont.plugin.CorePlugin.sendConsoleMessage;

/**
 * Interface for defining custom item interactions.
 * Items with interactions can respond to player clicks and actions.
 */
public abstract class ItemInteraction {

    private String name;
    private Set<Action> action;

    public ItemInteraction(String name, Set<Action> action) {
        this.name = name;
        this.action = action;
    }

    /**
     * Gets the number of items to remove when this interaction is executed.
     * Default implementation returns 0 (no items consumed).
     *
     * @return the number of items to remove from the stack
     */
    public int removeItemAmountOnExecute() {
        return 0;
    }

    /**
     * Gets the name of this interaction.
     *
     * @return the interaction name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the set of actions that trigger this interaction.
     *
     * @return the set of triggering actions
     */
    public Set<Action> getAction() {
        return action;
    }

    /**
     * Executes this interaction for the given player.
     *
     * @param player the player who triggered the interaction
     */
    public abstract void execute(Player player);

    /**
     * Whether to cancel the original event when this interaction is executed.
     * Default implementation returns true.
     *
     * @return true to cancel the event, false to allow it to continue
     */
    public boolean cancelEvent() {
        return true;
    }

    /**
     * Checks if the item has persistent data of a specific type.
     *
     * @param item the ItemStack to check
     * @param key the key of the data to check for
     * @param type the PersistentDataType of the data
     * @return true if the data exists, false otherwise
     */
    public static Boolean isItemHasPersistData(Plugin plugin, ItemStack item, String key, PersistentDataType type) {
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, key), type);
    }

    private static String getItemInteractionName(Plugin plugin, ItemStack item) {
        if (isItemHasPersistData(plugin, item, "interaction", PersistentDataType.STRING)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return Objects.requireNonNull(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "interaction"), PersistentDataType.STRING));
            }
        }
        return null;
    }

    /**
     * Gets the persistent data of an item.
     *
     * @param item the ItemStack to get data from
     * @param key the key of the data to get
     * @param type the PersistentDataType of the data
     * @return the data value, or null if not present
     */
    public static Object getItemPersistData(Plugin plugin, ItemStack item, String key, PersistentDataType type) {
        if (isItemHasPersistData(plugin, item, key, type)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return meta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), type);
            }
        }
        return null;
    }

    /**
     * Gets the interaction associated with the given ItemStack.
     *
     * @param item the ItemStack to get the interaction for
     * @return the ItemInteraction, or null if none found
     */
    public static ItemInteraction getInteraction(Plugin plugin, ItemStack item) {
        String id = getItemInteractionName(plugin, item);
        if (id == null) {
            sendConsoleMessage(ChatColor.RED + "Item interaction not found for item: " + item.getType());
            return null;
        }
        ItemInteraction interaction = ItemCreator.getInteractions().stream().filter(itemInteraction -> itemInteraction.getName().equals(id)).findFirst().orElse(null);
        if (interaction != null) {
            return interaction;
        } else {
            sendConsoleMessage(ChatColor.RED + "Item interaction not found for item: " + item.getType());
        }
        return null;
    }

}
