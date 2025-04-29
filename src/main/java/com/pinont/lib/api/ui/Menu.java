package com.pinont.lib.api.ui;

import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Menu {

    private final String title;
    private final int size;
    private final ArrayList<String> patternLayout = new ArrayList<>();
    private final ArrayList<Layout> layouts = new ArrayList<>();
    private final ArrayList<Button> buttons = new ArrayList<>();

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public Menu(String title) {
        this(title, 9);
    }

    public Menu(String title, int size) {
        this.title = title;
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

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

    public ArrayList<String> getLayout() {
        return patternLayout;
    }

    public Menu setKey(Layout... layouts) {
        this.layouts.addAll(Arrays.asList(layouts));
        return this;
    }

    public Menu addButton(Button button) {
        this.buttons.add(button);
        return this;
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, title);

        player.removeMetadata("Menu", CorePlugin.getInstance());
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
                            return new ItemCreator(new ItemStack(Material.BARRIER)).setDisplayName(ChatColor.RED + "This feature are not implemented yet.").create();
                        }

                        @Override
                        public void onClick(Player player) {

                        }
                    });
                    continue;
                }
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
            player.removeMetadata("Menu", CorePlugin.getInstance());
            player.closeInventory();
        }
        // Set metadata to prevent multiple instances
        CorePlugin.sendDebugMessage("Opening menu... " + getTitle());
        player.openInventory(inventory);
        player.setMetadata("Menu", new FixedMetadataValue(CorePlugin.getInstance(), this));
    }

}
