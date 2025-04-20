package com.pinont.lib.api.ui;

import com.pinont.lib.SingularityLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private final String name;
    private int size = 0; // for dynamic buttons
    private final List<Button> buttons = new ArrayList<>();

    public List<Button> getButtons() {
        return buttons;
    }

    public Menu(String title) {
        this.name = title;
    }

    public Menu addButton(Button button) {
        buttons.add(button);
        return this;
    }

    public Menu setSize(int size) {
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
        player.setMetadata("Menu", new FixedMetadataValue(SingularityLib.getInstance(), this));
        player.openInventory(inventory);
    }

}
