package com.pinont.lib.api.creator.items;

import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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
