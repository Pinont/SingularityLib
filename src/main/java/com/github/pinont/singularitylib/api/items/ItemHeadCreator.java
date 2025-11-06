package com.github.pinont.singularitylib.api.items;

import com.github.pinont.singularitylib.api.utils.Common;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Specialized ItemCreator for creating player head items.
 * Extends ItemCreator to provide skull-specific functionality like setting the owner.
 */
public class ItemHeadCreator extends ItemCreator {

    private final SkullMeta skullMeta;

    /**
     * Creates a new ItemHeadCreator from an existing ItemStack.
     *
     * @param item the ItemStack to create from (should be a player head)
     */
    public ItemHeadCreator(Plugin plugin, @NotNull ItemStack item) {
        super(plugin, item);
        skullMeta = (SkullMeta) item.getItemMeta();
    }

    @Override
    public ItemCreator setName(String displayName) {
        if (skullMeta != null) {
            skullMeta.displayName(new Common().colorize(displayName));
        }
        return this;
    }

    /**
     * Sets the owner of the skull, which determines the skin texture.
     *
     * @param owner the username of the player whose skin to use
     * @return this ItemHeadCreator for method chaining
     */
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
