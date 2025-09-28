package com.github.pinont.singularitylib.api.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Button {

    default int getSlot() {
        return 0;
    }

    ItemStack getItem();

    void onClick(Player player);

}
