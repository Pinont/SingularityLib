package com.pinont.lib.api.custom;

import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.utils.Common;
import org.bukkit.inventory.ItemStack;

public interface CustomItem {
    default ItemStack getItem() {
        return register().create(); // do not change this
    }

    default String getName() {
        return Common.resetStringColor(getItem().getItemMeta().getDisplayName()).replace(" ", "_").toLowerCase(); // do not change this
    }

    ItemCreator register();
}
