package com.pinont.lib.api.custom.item;

import com.pinont.lib.api.creator.items.ItemCreator;
import org.bukkit.inventory.ItemStack;

public class CustomItemCreator extends ItemCreator {

    private final CustomItem customItem;

    public CustomItemCreator(CustomItem customItem) {
        super(customItem.getOriginItem());
        this.customItem = customItem;
    }

    public ItemStack create() {
        ItemCreator item = this.setDisplayName(customItem.getName()).setLore(customItem.getLore()).setCustomModelData(customItem.getModelData()).addInteraction(customItem.onUse());
        return item.create();
    }

}
