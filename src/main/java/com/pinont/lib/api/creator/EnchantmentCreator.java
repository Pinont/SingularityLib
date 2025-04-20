package com.pinont.lib.api.creator;

import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import org.bukkit.inventory.EquipmentSlotGroup;

public interface EnchantmentCreator {
    String getNamespace();

    String getName();

    String getDescription();

    int getMaxLevel();

    int getAnvilCost();

    ItemTypeTagKeys getSupportItems();

    int getFoundWeight();

    EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

    EnchantmentRegistryEntry.EnchantmentCost getMaximumCost();

    EquipmentSlotGroup getSlotGroup();
}
