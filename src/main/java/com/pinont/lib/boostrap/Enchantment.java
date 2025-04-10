package com.pinont.lib.boostrap;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Setter
public class Enchantment extends org.bukkit.enchantments.Enchantment {

    private final String key;
    private final String name;
    public static int maxLevel;
    public static int startLevel;
    public static EnchantmentTarget enchantmentTarget;
    public static boolean isTreasure;
    public static boolean isCursed;
    public static ArrayList<org.bukkit.enchantments.Enchantment> conflictsWith = new ArrayList<>();
    public static ArrayList<ItemStack> canEnchantItem = new ArrayList<>();
    public static boolean isRegistered;
    @Getter
    public static int tableLevel;
    @Getter
    public static boolean foundOnTable;
    @Getter
    public static Lootable lootable;

    public Enchantment(String key, String name) {
        this.key = key;
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getStartLevel() {
        return startLevel;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return enchantmentTarget;
    }

    @Override
    public boolean isTreasure() {
        return isTreasure;
    }

    @Override
    public boolean isCursed() {
        return isCursed;
    }

    @Override
    public boolean conflictsWith(@NotNull org.bukkit.enchantments.Enchantment enchantment) {
        return conflictsWith.contains(enchantment);
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return canEnchantItem.contains(itemStack);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey(key, name);
    }

    @Override
    public @NotNull String getTranslationKey() {
        return key + "." + name;
    }

    @Override
    public @NotNull NamespacedKey getKeyOrThrow() {
        return getKey();
    }

    @Override
    public @Nullable NamespacedKey getKeyOrNull() {
        return getKey();
    }

    @Override
    public boolean isRegistered() {
        return isRegistered;
    }
}
