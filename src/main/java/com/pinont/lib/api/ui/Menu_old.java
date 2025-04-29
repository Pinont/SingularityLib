package com.pinont.lib.api.ui;

import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class Menu_old {

    private final String name;
    private int size = 0; // for dynamic buttons
    private final List<Button> buttons = new ArrayList<>();
    private final List<String> stringPattern = new ArrayList<>();
    private final List<Layout> patterns = new ArrayList<>();

    public List<Layout> getPatterns() {
        return patterns;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public Menu_old(String title) {
        this.name = title;
    }

    public Menu_old setLayout(String... pattern) {
        for (String s : pattern) {
            if (s.length() != 9) {
                throw new IllegalArgumentException("Pattern must be 9 characters long");
            }
            stringPattern.add(s);
        }
        return this;
    }

    public Menu_old setKey(Layout... pattern) {
        patterns.addAll(List.of(pattern));
        return this;
    }

    public Menu_old addButton(Button button) {
        buttons.add(button);
        return this;
    }

    public Menu_old setSize(int size) {
        this.size = size;
        return this;
    }

    public String getName() {
        return name;
    }

    public final void displayTo(Player player) {
        if (size == 0) { // dynamic sizing
            int rows = (int) Math.ceil(buttons.size() / 9.0);
            size = Math.min(rows, 6) * 9; // Max 6 rows
        }

        final Inventory inventory = Bukkit.createInventory(player, size, name);

        for (final Button button : buttons) {
            inventory.setItem(button.getSlot(), button.getItem());
        }

        if (player.hasMetadata("Menu")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Something went wrong, please try again.");
            return;
        }
        // Set metadata to prevent multiple instances
        player.setMetadata("Menu", new FixedMetadataValue(CorePlugin.getInstance(), this));
        player.openInventory(inventory);
    }

}
