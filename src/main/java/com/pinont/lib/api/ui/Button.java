package com.pinont.lib.api.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Button {

    int getSlot();

    ItemStack getItem();

    void onClick(Player player);

}
