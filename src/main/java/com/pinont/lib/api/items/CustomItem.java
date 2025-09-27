package com.pinont.lib.api.items;

import com.pinont.lib.api.utils.Common;
import org.bukkit.inventory.ItemStack;

public abstract class CustomItem {

    public ItemStack getItem() {
        return register().addInteraction(getInteraction()).create();
    }

    public String getName() {
        return Common.resetStringColor(getItem().getItemMeta().getDisplayName()).replace(" ", "_").toLowerCase();
    }

    public abstract ItemInteraction getInteraction();

    public abstract ItemCreator register();
}
