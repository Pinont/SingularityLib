package com.pinont.lib.api.creator;

import com.pinont.lib.boostrap.Enchantment;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;

@Getter
public class EnchantCreator extends Enchantment {

    public EnchantCreator(String key, String name) {
        super(key, name);
    }

    public EnchantCreator setMaxLevel(int maxLevel) {
        Enchantment.maxLevel = maxLevel;
        return this;
    }

    public EnchantCreator setStartLevel(int startLevel) {
        Enchantment.startLevel = startLevel;
        return this;
    }

    public EnchantCreator getItemTarget(EnchantmentTarget target) {
        Enchantment.enchantmentTarget = target;
        return this;
    }

    public EnchantCreator isTreasure(boolean isTreasure, Lootable lootable) {
        Enchantment.isTreasure = isTreasure;
        return this;
    }

    public EnchantCreator isCurse(boolean isCurse) {
        Enchantment.isCursed = isCurse;
        return this;
    }

    public EnchantCreator conflictWith(org.bukkit.enchantments.Enchantment other) {
        Enchantment.conflictsWith.add(other);
        return this;
    }

    public EnchantCreator foundOnTable(boolean foundOnTable, int tabelLevel) {
        Enchantment.foundOnTable = foundOnTable;
        return this;
    }

    public EnchantCreator canEnchantItem(Material material) {
        Enchantment.canEnchantItem.add(new ItemStack(Material.valueOf(material.name())));
        return this;
    }
}
