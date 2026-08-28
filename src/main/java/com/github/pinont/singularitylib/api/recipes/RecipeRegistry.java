package com.github.pinont.singularitylib.api.recipes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

/**
 * Registry helpers for custom crafting recipes (Phase 2).
 *
 * <p>Registers shaped, shapeless and furnace recipes under namespaced keys
 * derived from {@code plugin.getName()}. Duplicate registrations are caught
 * and logged rather than thrown.
 */
public final class RecipeRegistry {

    private RecipeRegistry() {
    }

    /**
     * Registers a shaped (grid) recipe.
     *
     * @param shape       rows of characters, e.g. {"AAA","ABA","AAA"}
     * @param ingredients char -> material mapping
     */
    public static void registerShaped(Plugin plugin, String key, ItemStack result, String[] shape, Map<Character, Material> ingredients) {
        try {
            NamespacedKey nk = new NamespacedKey(plugin, key);
            ShapedRecipe recipe = new ShapedRecipe(nk, result);
            recipe.shape(shape);
            for (Map.Entry<Character, Material> e : ingredients.entrySet()) {
                recipe.setIngredient(e.getKey(), e.getValue());
            }
            Bukkit.addRecipe(recipe);
            sendConsoleMessage(Component.text("Registered shaped recipe: " + key, NamedTextColor.GREEN));
        } catch (IllegalArgumentException ex) {
            sendConsoleMessage(Component.text("Recipe already exists or invalid: " + key + " — " + ex.getMessage(), NamedTextColor.YELLOW));
        }
    }

    /**
     * Registers a shapeless recipe.
     */
    public static void registerShapeless(Plugin plugin, String key, ItemStack result, List<Material> ingredients) {
        try {
            NamespacedKey nk = new NamespacedKey(plugin, key);
            ShapelessRecipe recipe = new ShapelessRecipe(nk, result);
            for (Material m : ingredients) {
                recipe.addIngredient(m);
            }
            Bukkit.addRecipe(recipe);
            sendConsoleMessage(Component.text("Registered shapeless recipe: " + key, NamedTextColor.GREEN));
        } catch (IllegalArgumentException ex) {
            sendConsoleMessage(Component.text("Recipe already exists or invalid: " + key + " — " + ex.getMessage(), NamedTextColor.YELLOW));
        }
    }

    /**
     * Registers a furnace (smelting) recipe.
     */
    public static void registerFurnace(Plugin plugin, String key, ItemStack input, ItemStack result, float exp) {
        try {
            NamespacedKey nk = new NamespacedKey(plugin, key);
            FurnaceRecipe recipe = new FurnaceRecipe(nk, result, input.getType(), exp, 200);
            Bukkit.addRecipe(recipe);
            sendConsoleMessage(Component.text("Registered furnace recipe: " + key, NamedTextColor.GREEN));
        } catch (IllegalArgumentException ex) {
            sendConsoleMessage(Component.text("Recipe already exists or invalid: " + key + " — " + ex.getMessage(), NamedTextColor.YELLOW));
        }
    }
}