package com.github.pinont.singularitylib.api.items;

import com.github.pinont.singularitylib.api.enums.AttributeType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Attribute helpers for ItemStacks (Phase 2).
 *
 * <p>Uses Bukkit's {@link AttributeModifier} API on the item's item-meta.
 * On Paper 26.2, {@code ItemMeta.addAttributeModifier(Attribute, AttributeModifier)}
 * and {@code getAttributeModifiers(Attribute)} are the modern calls.
 */
public final class Attributes {

    private Attributes() {
    }

    /**
     * Adds an attribute modifier to the item.
     *
     * @return a NEW ItemStack with the modifier applied (original untouched)
     */
    public static ItemStack setAttribute(ItemStack item, AttributeType type, double amount,
                                         AttributeModifier.Operation operation) {
        ItemStack copy = item.clone();
        Attribute attribute = type.getAttribute();
        AttributeModifier modifier = new AttributeModifier(
                UUID.randomUUID(),
                "singularity:" + type.name().toLowerCase(),
                amount,
                operation,
                EquipmentSlotGroup.ANY
        );
        var meta = copy.getItemMeta();
        if (meta == null) {
            return copy;
        }
        meta.addAttributeModifier(attribute, modifier);
        copy.setItemMeta(meta);
        return copy;
    }

    /**
     * Sums the attribute modifier amounts currently applied to the item for the type.
     */
    public static double getAttribute(ItemStack item, AttributeType type) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return 0.0;
        }
        double total = 0.0;
        var modifiers = meta.getAttributeModifiers(type.getAttribute());
        if (modifiers == null) {
            return 0.0;
        }
        for (AttributeModifier modifier : modifiers) {
            total += modifier.getAmount();
        }
        return total;
    }
}