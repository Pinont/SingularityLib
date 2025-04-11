package com.pinont.lib.api.creator;

import com.pinont.lib.SingularityLib;
import com.pinont.lib.api.utils.Common;
import com.pinont.lib.api.utils.enums.AttributeType;
import com.pinont.lib.api.utils.enums.PersisDataType;
import com.pinont.lib.boostrap.AttributeModifier;
import com.pinont.lib.plugin.CorePlugin;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ItemCreator {

    private final ItemStack item;
    private ItemMeta meta;
    private short durability = 0;
    @Getter
    private final PersistentDataContainer data;
    private final ArrayList<String> lore = new ArrayList<>();
    private int amount = 1;
    private Material type;
    private final Plugin plugin = SingularityLib.getInstance();

    public ItemCreator(@NotNull ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.type = item.getType();
        data = meta != null ? meta.getPersistentDataContainer() : null;
    }

    public ItemStack create() {
        item.setType(type);
        if (meta == null) {meta = item.getItemMeta();}
        if (meta != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        item.setDurability(durability);
        item.setAmount(amount);
        return item;
    }

    public Boolean hasTag(String tag) {
        return Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(new NamespacedKey(plugin, tag), PersistentDataType.STRING);
    }

    public String getKey(String key) {
        return data.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    public ItemCreator setItemMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    public ItemCreator setType(Material type) {
        this.type = type;
        return this;
    }

    public ItemCreator addChargedProjectile(ItemStack arrow) {
        CrossbowMeta crossbowMeta = (CrossbowMeta) meta;
        crossbowMeta.addChargedProjectile(arrow);
        meta = crossbowMeta;
        return this;
    }

    public ItemCreator setDisplayName(String name) {
        meta.setDisplayName(Common.colorize(name));
        return this;
    }

    public ItemCreator setAmount(int amount) {
        this.amount = Math.max(amount, 1);
        return this;
    }

    public ItemCreator setDurability(short durability) {
        this.durability = durability;
        return this;
    }

    public ItemCreator setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemCreator addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    public ItemCreator setLore(String... lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

//    public ItemCreator addAttribute(AttributeType attributeType, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
//        meta.addAttributeModifier(attributeType.getAttribute(), new AttributeModifier(UUID.randomUUID(), attributeType.name(), amount, operation, slot).getAttributeModifier());
//        return this;
//    }

    public ItemCreator setCustomModelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemCreator setCustomTag(String tag) {
        data.set(new NamespacedKey(plugin, tag), PersistentDataType.STRING, tag);
        return this;
    }

    public ItemCreator setCustomTag(String key, String value) {
        data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        return this;
    }

    public ItemCreator setPersisDataContainer(String key, Object value, PersisDataType type) {
        switch (type) {
            case STRING:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value.toString());
                break;
            case INT:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, Integer.parseInt(value.toString()));
                break;
            case DOUBLE:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.DOUBLE, Double.parseDouble(value.toString()));
                break;
            case FLOAT:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.FLOAT, Float.parseFloat(value.toString()));
                break;
            case LONG:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.LONG, Long.parseLong(value.toString()));
                break;
            case BYTE:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.BYTE, Byte.parseByte(value.toString()));
                break;
            case SHORT:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.SHORT, Short.parseShort(value.toString()));
                break;
            case BYTE_ARRAY:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY, value.toString().getBytes());
                break;
            case INT_ARRAY:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER_ARRAY, Arrays.stream(value.toString().split(",")).mapToInt(Integer::parseInt).toArray());
                break;
            case LONG_ARRAY:
                data.set(new NamespacedKey(plugin, key), PersistentDataType.LONG_ARRAY, Arrays.stream(value.toString().split(",")).mapToLong(Long::parseLong).toArray());
                break;
        }
        return this;
    }

    public ItemCreator setDurability(int durability) {
        this.durability = durability < 0 ? 0 : (short) durability;
        return this;
    }

    public ItemMeta getMeta() {
        return Objects.requireNonNull(item).getItemMeta();
    }

    public String getCustomTag(String key) {
        return data.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    public Object getPersistentDataContainer(String key, PersistentDataType type) {
        return data.get(new NamespacedKey(plugin, key), type);
    }
}