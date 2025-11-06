package com.github.pinont.singularitylib.api.ui;

import com.github.pinont.singularitylib.api.items.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.github.pinont.plugin.CorePlugin.getInstance;
import static com.github.pinont.plugin.CorePlugin.sendDebugMessage;

/**
 * Class for creating and managing custom inventory menus.
 * Provides functionality to create interactive GUIs with buttons and layouts.
 */
public class Menu {

    private final String title;
    private final int size;
    private final Plugin plugin;
    private final ArrayList<String> patternLayout = new ArrayList<>();
    private final ArrayList<Layout> layouts = new ArrayList<>();
    private final ArrayList<Button> buttons = new ArrayList<>();

    /**
     * Gets the list of buttons in this menu.
     *
     * @return the list of buttons
     */
    public ArrayList<Button> getButtons() {
        return buttons;
    }

    /**
     * Creates a new Menu with the specified title and automatic size.
     *
     * @param title the title of the menu
     */
    public Menu(Plugin plugin, String title) {
        this(plugin, title, -1);
    }

    /**
     * Creates a new Menu with the specified title and size.
     *
     * @param title the title of the menu
     * @param size the size of the menu inventory
     */
    public Menu(Plugin plugin, String title, int size) {
        this.plugin = plugin;
        this.title = title;
        this.size = size;
    }

    /**
     * Gets the title of this menu.
     *
     * @return the menu title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the size of this menu.
     *
     * @return the menu size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the layout pattern for this menu using string patterns.
     *
     * @param layout the layout strings defining the menu structure
     * @return this Menu for method chaining
     */
    public Menu setLayout(String... layout) {
        String[] split = String.join("", layout).split("");
        for (String s : split) {
            if (s.length() > 1) {
                throw new IllegalArgumentException("Layout must be a single character");
            }
            patternLayout.add(s);
        }
        return this;
    }

    /**
     * Gets the layout pattern of this menu.
     *
     * @return the layout pattern as a list of strings
     */
    public ArrayList<String> getLayout() {
        return patternLayout;
    }

    /**
     * Sets the layout keys that map characters to buttons.
     *
     * @param layouts the layout mappings
     * @return this Menu for method chaining
     */
    public Menu setKey(Layout... layouts) {
        this.layouts.addAll(Arrays.asList(layouts));
        return this;
    }

    /**
     * Adds a button to this menu.
     *
     * @param button the button to add
     * @return this Menu for method chaining
     */
    public Menu addButton(Button button) {
        this.buttons.add(button);
        return this;
    }

    /**
     * Shows this menu to the specified player.
     *
     * @param player the player to show the menu to
     */
    public void show(Player player) {
        Inventory inventory;
        if (size == -1 && patternLayout.isEmpty()) {
            inventory = Bukkit.createInventory(player, 9, title);
        } else if (size == -1 && patternLayout.size() >= 9) {
            inventory = Bukkit.createInventory(player, patternLayout.size(), title); // auto layout
        } else {
            inventory = Bukkit.createInventory(player, size, title);
        }

        player.removeMetadata("Menu", getInstance());
        if (!patternLayout.isEmpty()) {
            for (int i = 0; i < patternLayout.size(); i++) {
                String c = patternLayout.get(i);
                if (Objects.equals(c, " ")) continue;
                Layout layout = layouts.stream()
                        .filter(l -> c.equalsIgnoreCase(String.valueOf(l.getKey())))
                        .findFirst()
                        .orElse(null);
                if (layout == null) {
                    return;
                }
                int finalI = i;
                if (layout.getButton() == null) {
                    addButton(new Button() {
                        @Override
                        public int getSlot() {
                            return finalI;
                        }

                        @Override
                        public ItemStack getItem() {
                            return new ItemCreator(plugin, new ItemStack(Material.BARRIER)).setName(ChatColor.RED + "This feature are not implemented yet.").create();
                        }

                        @Override
                        public void onClick(Player player) {

                        }
                    });
                    continue;
                } else if (layout.getButton().getItem() == null || layout.getButton().getItem().getType().equals(Material.AIR)) {continue;}
                addButton(new Button() {
                    @Override
                    public int getSlot() {
                        return finalI;
                    }

                    @Override
                    public ItemStack getItem() {
                        return layout.getButton().getItem();
                    }

                    @Override
                    public void onClick(Player player) {
                        layout.getButton().onClick(player);
                    }
                });
            }
        }
        if (!buttons.isEmpty()) {
            for (Button button : buttons) {
                inventory.setItem(button.getSlot(), button.getItem());
            }
        }
        if (player.hasMetadata("Menu")) {
            player.removeMetadata("Menu", getInstance());
            player.closeInventory();
        }
        // Set metadata to prevent multiple instances
        sendDebugMessage("Opening menu... " + getTitle());
        player.openInventory(inventory);
        player.setMetadata("Menu", new FixedMetadataValue(getInstance(), this));
    }

}
