package io.github.pinont.lib.api.items;

import com.google.common.collect.Sets;
import io.github.pinont.lib.api.enums.AttributeType;
import io.github.pinont.lib.api.enums.PersisDataType;
import io.github.pinont.lib.api.utils.Common;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.pinont.lib.plugin.CorePlugin.getInstance;
import static io.github.pinont.lib.plugin.CorePlugin.sendConsoleMessage;

public class ItemCreator {

    private final Common common = new Common();

    private final ItemStack item;
    private ItemMeta meta;
    private short durability = 0;
    private final PersistentDataContainer data;
    private final ArrayList<Component> lore = new ArrayList<>();
    private int amount = 1;
    private Material type;
    private final Plugin plugin = getInstance();
    private static final Set<ItemInteraction> ITEM_INTERACTIONS = Sets.newHashSet();

    public static Set<ItemInteraction> getInteractions() {
        return ITEM_INTERACTIONS;
    }

    public ItemCreator(Material type) {
        this(new ItemStack(type));
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
            meta.lore(lore);
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
        this.lore.add(common.colorize(lore));
        return this;
    }

    public ItemCreator setItemMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    public static ItemInteraction getInteraction(ItemStack item) {
        String id = getItemInteractionName(item);
        if (id == null) {
            sendConsoleMessage(ChatColor.RED + "Item interaction not found for item: " + item.getType());
            return null;
        }
        ItemInteraction interaction = ITEM_INTERACTIONS.stream().filter(itemInteraction -> itemInteraction.getName().equals(id)).findFirst().orElse(null);
        if (interaction != null) {
            return interaction;
        } else {
            sendConsoleMessage(ChatColor.RED + "Item interaction not found for item: " + item.getType());
        }
        return null;
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

    public ItemCreator setCannotMove(boolean b) {
        if (b) {
            this.setDataContainer("cannot_move", "true", PersisDataType.STRING);
        }
        return this;
    }

    public ItemCreator setAmount(int amount) {
        this.amount = Math.max(amount, 1);
        return this;
    }

    public ItemCreator setName(String name) {
        meta.displayName(common.colorize(name));
        meta.itemName(common.colorize(name));
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
        this.lore.add(common.colorize(Arrays.toString(lore)));
        return this;
    }

    public ItemCreator addAttribute(AttributeType attributeType, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        meta.addAttributeModifier(attributeType.getAttribute(), new AttributeModifier(UUID.randomUUID(), attributeType.name(), amount, operation, slot));
        return this;
    }

    public ItemCreator setUnstackable(boolean bool) {
        setDataContainer("unstackable", (short) new Random().nextInt(), PersisDataType.SHORT);
        return this;
    }

    public ItemCreator setModelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemCreator setTag(String tag) {
        data.set(new NamespacedKey(plugin, tag), PersistentDataType.STRING, tag);
        return this;
    }

    public ItemCreator setTag(String key, String value) {
        data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        return this;
    }

    public ItemCreator setDurability(int durability) {
        this.durability = durability < 0 ? 0 : (short) durability;
        return this;
    }

    public ItemMeta getMeta() {
        return Objects.requireNonNull(item).getItemMeta();
    }

    public static Object getItemPersistData(ItemStack item, String key, PersistentDataType type) {
        if (isItemHasPersistData(item, key, type)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return meta.getPersistentDataContainer().get(new NamespacedKey(getInstance(), key), type);
            }
        }
        return null;
    }

    public ItemCreator setDataContainer(String key, Object value, PersisDataType type) {
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

    private static String getItemInteractionName(ItemStack item) {
        if (isItemHasPersistData(item, "interaction", PersistentDataType.STRING)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return Objects.requireNonNull(meta.getPersistentDataContainer().get(new NamespacedKey(getInstance(), "interaction"), PersistentDataType.STRING));
            }
        }
        return null;
    }

    public ItemCreator addInteraction(ItemInteraction itemInteraction) {
        if (itemInteraction == null) return this;
        ITEM_INTERACTIONS.add(itemInteraction);
        this.setDataContainer("interaction", itemInteraction.getName(), PersisDataType.STRING);
        return this;
    }

    public static Boolean isItemHasPersistData(ItemStack item, String key, PersistentDataType type) {
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(getInstance(), key), type);
    }
}