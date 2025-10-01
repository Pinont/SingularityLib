package com.github.pinont.singularitylib.api.items;

import com.github.pinont.singularitylib.api.utils.Console;
import com.google.common.collect.Sets;
import com.github.pinont.singularitylib.api.enums.AttributeType;
import com.github.pinont.singularitylib.api.enums.PersisDataType;
import com.github.pinont.singularitylib.api.utils.Common;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.pinont.singularitylib.plugin.CorePlugin.getInstance;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

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
    private final String name;

    public static Set<ItemInteraction> getInteractions() {
        return ITEM_INTERACTIONS;
    }

    public ItemCreator clone() {
        return new ItemCreator(this.create());
    }

    public ItemCreator(Material type) {
        this(new ItemStack(type));
    }

    public ItemCreator(Material type, int amount) {
        this(new ItemStack(type, amount));
    }

    public ItemCreator(@NotNull ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.type = item.getType();
        this.amount = item.getAmount();
        this.name = item.getItemMeta().getDisplayName().isEmpty() ? Common.normalizeStringName(item.getType().name()) : item.getItemMeta().getDisplayName();
        data = meta != null ? meta.getPersistentDataContainer() : null;
    }

    public String getName() {
        return this.create().getItemMeta().getDisplayName();
    }

    public ItemMeta getItemMeta() {
        return this.create().getItemMeta();
    }

    public Material getType() {
        return this.create().getType();
    }

    public int getAmount() {
        return this.create().getAmount();
    }

    public ItemStack create() {
        this.item.setType(type);
        if (this.meta == null) {this.meta = this.item.getItemMeta();}
        if (this.meta != null) {
            this.meta.lore(this.lore);
        }
        this.item.setItemMeta(this.meta);
        this.item.setDurability(this.durability);
        this.item.setAmount(this.amount);
        return this.item;
    }

    public Boolean hasTag(String tag) {
        return data.has(new NamespacedKey(plugin, tag), PersistentDataType.STRING);
    }

    public String getKey(String key) {
        return getTagValue(key);
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

    public ItemCreator setName(@Nullable String name) {
        return this.setName(common.colorize(name != null ? name : ""));
    }

    public ItemCreator setName(@Nullable Component name) {
        if (name == null) name = common.colorize("");
        meta.displayName(name);
        meta.itemName(name);
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

    public ItemCreator addLore(String... lores) {
        this.lore.add(common.colorize(Arrays.toString(lores)));
        return this;
    }

    public ItemCreator addLore(Component... lores) {
        this.lore.addAll(Arrays.asList(lores));
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

    private void sendTagNamingPatternWarn(String tag) {
        Console.logWarning("Tag naming pattern warning: '" + tag + "' tag should not contain spaces and should be lowercase. It has been converted to '" + tag.replace(" ", "_").toLowerCase() + "'");
    }

    private String formatTag(String tag) {
        String tagFormatted = String.join(" ", tag.replace(" ", "_"));
        if (!tagFormatted.equals(tag)) {
            sendTagNamingPatternWarn(tag);
            tag = tagFormatted;
        }
        return tag;
    }

    public ItemCreator addTags(String... tags) {
        for (String tag : tags) {
            String formatTag = formatTag(tag);
            data.set(new NamespacedKey(plugin, formatTag), PersistentDataType.STRING, tag);
        }
        return this;
    }

    public ItemCreator addTag(String key) {
        String formatTag = formatTag(key);
        addTag(formatTag, key);
        return this;
    }

    public ItemCreator addTag(String key, String value) {
        key = formatTag(key);
        data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        return this;
    }

    public ItemCreator replaceTag(String oldKey, String newKey) {
        oldKey = formatTag(oldKey);
        newKey = formatTag(newKey);
        if (hasTag(oldKey)) {
            if (oldKey.equals(getTagValue(oldKey))) {
                removeTag(oldKey);
                data.set(new NamespacedKey(plugin, newKey), PersistentDataType.STRING, newKey);
                return this;
            }
            String value = getTagValue(oldKey);
            removeTag(oldKey);
            data.set(new NamespacedKey(plugin, newKey), PersistentDataType.STRING, value);
        } else {
            sendConsoleMessage(ChatColor.RED + "Item tag not found: " + oldKey);
        }
        return this;
    }

    public String getTagValue(String key) {
        key = formatTag(key);
        return data.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    public ItemCreator setTagValue(String key, String newValue) {
        key = formatTag(key);
        if (hasTag(key)) {
            removeTag(key);
            data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, newValue);
        } else {
            sendConsoleMessage(ChatColor.RED + "Item tag not found: " + key);
        }
        return this;
    }

    public ItemCreator removeTag(String tag) {
        tag = formatTag(tag);
        if (hasTag(tag)) {
            data.remove(new NamespacedKey(plugin, tag));
        } else {
            sendConsoleMessage(ChatColor.RED + "Item tag not found: " + tag);
        }
        return this;
    }

    public ItemCreator setDurability(int durability) {
        this.durability = durability < 0 ? 0 : (short) durability;
        return this;
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