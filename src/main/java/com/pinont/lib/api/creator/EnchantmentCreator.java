package com.pinont.lib.api.creator;

import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;

public class EnchantmentCreator {

    public interface EnchantmentBlueprint {
        String getNamespace();

        String getName();

        String getDescription();

        int getMaxLevel();

        int getAnvilCost();

        TagKey<ItemType> getSupportItem();

        int getFoundWeight();

        EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

        EnchantmentRegistryEntry.EnchantmentCost getMaximumCost();

        EquipmentSlotGroup getActiveSlotGroup();
    }

    private static final ArrayList<EnchantmentBlueprint> enchantments = new ArrayList<>();

    public static ArrayList<EnchantmentBlueprint> getEnchantments() {
        return enchantments;
    }

    public EnchantmentCreator(EnchantmentBlueprint enchantment) {
        enchantments.add(enchantment);
    }

    public Enchantment getEnchantment() {
        return new Enchantment() {
            @Override
            public @NotNull String getTranslationKey() {
                return "";
            }

            @Override
            public @NotNull NamespacedKey getKey() {
                return null;
            }

            @Override
            public @NotNull String getName() {
                return "";
            }

            @Override
            public int getMaxLevel() {
                return 0;
            }

            @Override
            public int getStartLevel() {
                return 0;
            }

            @Override
            public @NotNull EnchantmentTarget getItemTarget() {
                return null;
            }

            @Override
            public boolean isTreasure() {
                return false;
            }

            @Override
            public boolean isCursed() {
                return false;
            }

            @Override
            public boolean conflictsWith(@NotNull Enchantment enchantment) {
                return false;
            }

            @Override
            public boolean canEnchantItem(@NotNull ItemStack itemStack) {
                return false;
            }

            @Override
            public @NotNull Component displayName(int i) {
                return null;
            }

            @Override
            public boolean isTradeable() {
                return false;
            }

            @Override
            public boolean isDiscoverable() {
                return false;
            }

            @Override
            public int getMinModifiedCost(int i) {
                return 0;
            }

            @Override
            public int getMaxModifiedCost(int i) {
                return 0;
            }

            @Override
            public int getAnvilCost() {
                return 0;
            }

            @Override
            public @NotNull EnchantmentRarity getRarity() {
                return null;
            }

            @Override
            public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
                return 0;
            }

            @Override
            public float getDamageIncrease(int i, @NotNull EntityType entityType) {
                return 0;
            }

            @Override
            public @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups() {
                return Set.of();
            }

            @Override
            public @NotNull Component description() {
                return null;
            }

            @Override
            public @NotNull RegistryKeySet<ItemType> getSupportedItems() {
                return null;
            }

            @Override
            public @Nullable RegistryKeySet<ItemType> getPrimaryItems() {
                return null;
            }

            @Override
            public int getWeight() {
                return 0;
            }

            @Override
            public @NotNull RegistryKeySet<Enchantment> getExclusiveWith() {
                return null;
            }

            @Override
            public @NotNull String translationKey() {
                return "";
            }
        };
    }
}
