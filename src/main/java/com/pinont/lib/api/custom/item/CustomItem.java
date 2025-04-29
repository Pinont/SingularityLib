package com.pinont.lib.api.custom.item;

import com.pinont.lib.api.ui.ItemInteraction;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface CustomItem {

    String getName();

    List<String> getLore();

    ItemStack getOriginItem();

    int getModelData();

    ItemInteraction onUse();

}
