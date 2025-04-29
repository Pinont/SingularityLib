package com.pinont.lib.api.creator.items;

import com.google.common.collect.Sets;
import com.pinont.lib.api.ui.ItemInteraction;
import com.pinont.lib.api.utils.Common;
import com.pinont.lib.enums.AttributeType;
import com.pinont.lib.enums.PersisDataType;
import com.pinont.lib.enums.WorldEnvironment;
import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.*;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.pinont.lib.plugin.CorePlugin.sendConsoleMessage;

public class ItemCreator {

    private final ItemStack item;
    private ItemMeta meta;
    private short durability = 0;
    private final PersistentDataContainer data;
    private final ArrayList<String> lore = new ArrayList<>();
    private int amount = 1;
    private Material type;
    private final Plugin plugin = CorePlugin.getInstance();
    private static final Set<ItemInteraction> ITEM_INTERACTIONS = Sets.newHashSet();

    public static Set<ItemInteraction> getInteractions() {
        return ITEM_INTERACTIONS;
    }

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

    public ItemCreator addLore(String lore) {
        this.lore.add(ChatColor.RESET + Common.colorize(lore));
        return this;
    }

    public ItemCreator setItemMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    public ItemCreator setCannotMove(boolean b) {
        if (b) {
            this.setPersisDataContainer("cannot_move", "true", PersisDataType.STRING);
        }
        return this;
    }

    public ItemCreator setType(Material type) {
        this.type = type;
        return this;
    }

    public ItemCreator addItemFlag(ItemFlag... flags) {
        for (ItemFlag flag : flags) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    public ItemCreator setDisplayName(String name) {
        meta.setDisplayName(ChatColor.RESET + Common.colorize(name));
        return this;
    }

    public ItemCreator setAmount(int amount) {
        this.amount = Math.max(amount, 1);
        return this;
    }

    public ItemCreator setLore(List<String> lore) {
        for (String s : lore) {
            this.lore.add(ChatColor.RESET + Common.colorize(s));
        }
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

    public ItemCreator addLore(String... lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    public ItemCreator addAttribute(AttributeType attributeType, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        meta.addAttributeModifier(attributeType.getAttribute(), new AttributeModifier(UUID.randomUUID(), attributeType.name(), amount, operation, slot));
        return this;
    }

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

    public static Object getItemPersistData(ItemStack item, String key, PersistentDataType type) {
        if (isItemHasPersistData(item, key, type)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return meta.getPersistentDataContainer().get(new NamespacedKey(CorePlugin.getInstance(), key), type);
            }
        }
        return null;
    }

    public ItemCreator addInteraction(ItemInteraction itemInteraction) {
        ITEM_INTERACTIONS.add(itemInteraction);
        List<World> allowedWorlds = new ArrayList<>();
        if (itemInteraction.getExecuteInWorlds().contains("*")) {
            allowedWorlds = Bukkit.getWorlds();
        } else {
            for (String worldName : itemInteraction.getExecuteInWorlds()) {
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    allowedWorlds.add(world);
                    continue;
                }
                sendConsoleMessage("World " + worldName + " not found, please check if the world is loaded or exist.");
            }
        }
        allowedWorlds = allowedWorlds.stream()
                .filter(world -> itemInteraction.getExecuteWorldEnvironment().stream()
                .anyMatch(env -> env.getWorldEnvironment().contains(world.getEnvironment()))).toList();
        for (World world : allowedWorlds) {
            if (itemInteraction.getExecuteWorldEnvironment().contains(WorldEnvironment.fromWorldEnvironment(world.getEnvironment()))) {
                world.setMetadata("interaction_" + itemInteraction.getName(), new FixedMetadataValue(plugin, itemInteraction));
            }
        }
        this.setPersisDataContainer("interaction", itemInteraction.getName(), PersisDataType.STRING);
        return this;
    }

    private static String getItemInteractionName(ItemStack item) {
        if (isItemHasPersistData(item, "interaction", PersistentDataType.STRING)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return Objects.requireNonNull(meta.getPersistentDataContainer().get(new NamespacedKey(CorePlugin.getInstance(), "interaction"), PersistentDataType.STRING));
            }
        }
        return null;
    }

    public static ItemInteraction getInteraction(Player holder, ItemStack item) {
        String id = getItemInteractionName(item);
        World playerWorld = holder.getWorld();
        if (playerWorld.hasMetadata("interaction_" + id)) {
            return (ItemInteraction) playerWorld.getMetadata("interaction_" + id);
        }
        return null;
    }

    public static Boolean isItemHasPersistData(ItemStack item, String key, PersistentDataType type) {
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(CorePlugin.getInstance(), key), type);
    }
}