package com.github.pinont.singularitylib.api.items;

import com.github.pinont.singularitylib.api.utils.Common;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class ItemHeadCreator extends ItemCreator {

    private final SkullMeta skullMeta;

    public ItemHeadCreator(@NotNull ItemStack item) {
        super(item);
        skullMeta = (SkullMeta) item.getItemMeta();
    }

    @Override
    public ItemCreator setName(String displayName) {
        if (skullMeta != null) {
            skullMeta.displayName(new Common().colorize(displayName));
        }
        return this;
    }

    public ItemHeadCreator setOwner(@NotNull String owner) {
        skullMeta.setOwner(owner);
        return this;
    }

    @Override
    public ItemStack create() {
        this.setItemMeta(skullMeta);
        return super.create();
    }
}
