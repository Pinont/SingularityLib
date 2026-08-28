package com.github.pinont.singularitylib.api.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.github.pinont.singularitylib.api.items.ItemCreator;
import org.bukkit.plugin.Plugin;

/**
 * Factory for common menu buttons (back, close, filler, confirm, cancel).
 * Removes the need to hand-roll identical buttons across every plugin menu.
 */
public final class StandardButtons {

    private StandardButtons() {
    }

    /**
     * A button that re-opens the given parent menu when clicked.
     */
    public static Button back(Plugin plugin, Menu parent, String label) {
        return new Button() {
            @Override
            public int getSlot() {
                return 0;
            }

            @Override
            public ItemStack getItem() {
                return new ItemCreator(plugin, Material.ARROW)
                        .setName(Component.text(label == null ? "Back" : label, NamedTextColor.YELLOW))
                        .create();
            }

            @Override
            public void onClick(org.bukkit.entity.Player player) {
                if (parent != null) {
                    parent.show(player);
                }
            }
        };
    }

    /**
     * A button that closes the open inventory.
     */
    public static Button close(Plugin plugin) {
        return new Button() {
            @Override
            public int getSlot() {
                return 8;
            }

            @Override
            public ItemStack getItem() {
                return new ItemCreator(plugin, Material.BARRIER)
                        .setName(Component.text("Close", NamedTextColor.RED))
                        .create();
            }

            @Override
            public void onClick(org.bukkit.entity.Player player) {
                player.closeInventory();
            }
        };
    }

    /**
     * A non-interactive filler item (e.g. stained glass pane) for decorative slots.
     */
    public static Button filler(Plugin plugin, Material material) {
        return new Button() {
            @Override
            public int getSlot() {
                return -1; // placed by layout, never via slot
            }

            @Override
            public ItemStack getItem() {
                return new ItemCreator(plugin, material == null ? Material.GRAY_STAINED_GLASS_PANE : material)
                        .setName(Component.text(" "))
                        .create();
            }

            @Override
            public void onClick(org.bukkit.entity.Player player) {
                // no-op
            }
        };
    }

    /**
     * A confirmation-style button that runs the given action on click.
     */
    public static Button confirm(Plugin plugin, Material material, String label, Runnable action) {
        return new Button() {
            @Override
            public int getSlot() {
                return 0;
            }

            @Override
            public ItemStack getItem() {
                return new ItemCreator(plugin, material == null ? Material.GREEN_STAINED_GLASS : material)
                        .setName(Component.text(label == null ? "Confirm" : label, NamedTextColor.GREEN))
                        .create();
            }

            @Override
            public void onClick(org.bukkit.entity.Player player) {
                if (action != null) {
                    action.run();
                }
            }
        };
    }

    /**
     * A cancel-style button that runs the given action on click.
     */
    public static Button cancel(Plugin plugin, Material material, String label, Runnable action) {
        return new Button() {
            @Override
            public int getSlot() {
                return 0;
            }

            @Override
            public ItemStack getItem() {
                return new ItemCreator(plugin, material == null ? Material.RED_STAINED_GLASS : material)
                        .setName(Component.text(label == null ? "Cancel" : label, NamedTextColor.RED))
                        .create();
            }

            @Override
            public void onClick(org.bukkit.entity.Player player) {
                if (action != null) {
                    action.run();
                }
            }
        };
    }
}