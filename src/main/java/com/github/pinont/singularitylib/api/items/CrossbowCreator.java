package com.github.pinont.singularitylib.api.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A specialized ItemCreator for creating crossbow items.
 * Extends ItemCreator to provide crossbow-specific functionality like adding charged projectiles.
 */
public class CrossbowCreator extends ItemCreator {

    /**
     * Creates a new CrossbowCreator with a crossbow ItemStack.
     */
    public CrossbowCreator() {
        super(new ItemStack(Material.CROSSBOW));
    }

    /**
     * Adds a charged projectile to the crossbow.
     *
     * @param arrow the ItemStack representing the projectile to add
     * @return this CrossbowCreator for method chaining
     */
    public ItemCreator addChargedProjectile(ItemStack arrow) {
        ItemMeta meta = this.getItemMeta();
        CrossbowMeta crossbowMeta = (CrossbowMeta) meta;
        crossbowMeta.addChargedProjectile(arrow);
        this.setItemMeta(meta);
        return this;
    }
}
