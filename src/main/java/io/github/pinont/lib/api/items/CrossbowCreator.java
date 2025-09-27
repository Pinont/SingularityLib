package io.github.pinont.lib.api.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class CrossbowCreator extends ItemCreator {

    public CrossbowCreator() {
        super(new ItemStack(Material.CROSSBOW));
    }

    public ItemCreator addChargedProjectile(ItemStack arrow) {
        ItemMeta meta = this.getMeta();
        CrossbowMeta crossbowMeta = (CrossbowMeta) meta;
        crossbowMeta.addChargedProjectile(arrow);
        this.setItemMeta(meta);
        return this;
    }
}
