package io.github.pinont.lib.api.ui;

import io.github.pinont.lib.api.items.ItemCreator;
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

import static io.github.pinont.lib.plugin.CorePlugin.getInstance;
import static io.github.pinont.lib.plugin.CorePlugin.sendDebugMessage;

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
        this(title, -1);
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
                            return new ItemCreator(new ItemStack(Material.BARRIER)).setName(ChatColor.RED + "This feature are not implemented yet.").create();
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
