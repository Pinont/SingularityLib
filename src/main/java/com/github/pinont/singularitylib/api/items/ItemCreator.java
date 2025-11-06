package com.github.pinont.singularitylib.api.items;

import com.github.pinont.singularitylib.api.enums.AttributeType;
import com.github.pinont.singularitylib.api.enums.PersisDataType;
import com.github.pinont.singularitylib.api.utils.Common;
import com.github.pinont.singularitylib.api.utils.Console;
import com.google.common.collect.Sets;
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

import static com.github.pinont.plugin.CorePlugin.sendConsoleMessage;

/**
 * Builder class for creating and modifying ItemStacks with enhanced functionality.
 * Provides a fluent API for setting item properties, adding enchantments, lore, tags, and interactions.
 */
public class ItemCreator {

    private final Common common = new Common();

    private ItemStack item;
    private ItemMeta meta;
    private short durability = 0;
    private PersistentDataContainer data;
    private final ArrayList<Component> lore = new ArrayList<>();
    private int amount = 1;
    private Material type;
    private final Plugin plugin;
    private static final Set<ItemInteraction> ITEM_INTERACTIONS = Sets.newHashSet();
    private String name;

    /**
     * Gets all registered item interactions.
     *
     * @return set of all item interactions
     */
    public static Set<ItemInteraction> getInteractions() {
        return ITEM_INTERACTIONS;
    }

    public ItemCreator clone() {
        return new ItemCreator(plugin, this.create());
    }

    /**
     * Creates a new ItemCreator for the specified material type.
     *
     * @param type the material type for the item
     */
    public ItemCreator(Plugin plugin, Material type) {
        this(plugin, new ItemStack(type));
    }

    /**
     * Creates a new ItemCreator for the specified material type and amount.
     *
     * @param type the material type for the item
     * @param amount the amount of items in the stack
     */
    public ItemCreator(Plugin plugin, Material type, int amount) {
        this(plugin, new ItemStack(type, amount));
    }

    /**
     * Creates a new ItemCreator from an existing ItemStack.
     *
     * @param item the ItemStack to create from
     */
    public ItemCreator(Plugin plugin, @NotNull ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.plugin = plugin;
        this.type = item.getType();
        this.amount = item.getAmount();
        this.name = item.getItemMeta().getDisplayName().isEmpty() ? Common.normalizeStringName(item.getType().name()) : item.getItemMeta().getDisplayName();
        data = meta != null ? meta.getPersistentDataContainer() : null;
    }

    /**
     * Gets the display name of the item.
     *
     * @return the display name
     */
    public String getName() {
        return this.create().getItemMeta().getDisplayName();
    }

    /**
     * Gets the ItemMeta of the item.
     *
     * @return the ItemMeta
     */
    public ItemMeta getItemMeta() {
        return this.create().getItemMeta();
    }

    /**
     * Gets the material type of the item.
     *
     * @return the material type
     */
    public Material getType() {
        return this.create().getType();
    }

    /**
     * Gets the amount of items in the stack.
     *
     * @return the amount
     */
    public int getAmount() {
        return this.create().getAmount();
    }

    /**
     * Creates the final ItemStack with all applied properties.
     *
     * @return the created ItemStack
     */
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

    /**
     * Checks if the item has a specific tag.
     *
     * @param tag the tag to check for
     * @return true if the item has the tag, false otherwise
     */
    public Boolean hasTag(String tag) {
        return data.has(new NamespacedKey(plugin, tag), PersistentDataType.STRING);
    }

    /**
     * Gets the value of a specific key from the item's tags.
     *
     * @param key the key to get the value for
     * @return the value associated with the key
     */
    public String getKey(String key) {
        return getTagValue(key);
    }

    /**
     * Adds lore to the item.
     *
     * @param lore the lore text to add
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addLore(String lore) {
        this.lore.add(common.colorize(lore));
        return this;
    }

    /**
     * Sets the ItemMeta for the item.
     *
     * @param meta the ItemMeta to set
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setItemMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    /**
     * Sets the material type of the item.
     *
     * @param type the material type to set
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setType(Material type) {
        this.type = type;
        return this;
    }

    /**
     * Adds item flags to hide certain item properties.
     *
     * @param flags the item flags to add
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addItemFlag(ItemFlag... flags) {
        for (ItemFlag flag : flags) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    /**
     * Sets whether the item can be moved in inventories.
     *
     * @param b true if the item cannot be moved, false otherwise
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setCannotMove(boolean b) {
        if (b) {
            this.setDataContainer("cannot_move", "true", PersisDataType.STRING);
        }
        return this;
    }

    /**
     * Sets the amount of items in the stack.
     *
     * @param amount the amount to set
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setAmount(int amount) {
        this.amount = Math.max(amount, 1);
        return this;
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name to set
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setName(@Nullable String name) {
        return this.setName(common.colorize(name != null ? name : ""));
    }

    /**
     * Sets the display name of the item using a Component.
     *
     * @param name the Component display name to set
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setName(@Nullable Component name) {
        if (name == null) name = common.colorize("");
        meta.displayName(name);
        meta.itemName(name);
        return this;
    }

    /**
     * Sets the durability of the item.
     *
     * @param durability the durability value
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setDurability(short durability) {
        this.durability = durability;
        return this;
    }

    /**
     * Sets whether the item is unbreakable.
     *
     * @param unbreakable true if the item should be unbreakable, false otherwise
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment the enchantment to add
     * @param level the level of the enchantment
     * @param ignoreLevelRestriction whether to ignore level restrictions
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    /**
     * Adds lore to the item.
     *
     * @param lores the lore texts to add
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addLore(String... lores) {
        this.lore.add(common.colorize(Arrays.toString(lores)));
        return this;
    }

    /**
     * Adds lore to the item.
     *
     * @param lores the lore Components to add
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addLore(Component... lores) {
        this.lore.addAll(Arrays.asList(lores));
        return this;
    }

    /**
     * Adds an attribute modifier to the item.
     *
     * @param attributeType the type of the attribute to modify
     * @param amount the amount to modify the attribute by
     * @param operation the operation to perform (addition, multiplication, etc.)
     * @param slot the equipment slot this modifier applies to
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addAttribute(AttributeType attributeType, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        meta.addAttributeModifier(attributeType.getAttribute(), new AttributeModifier(UUID.randomUUID(), attributeType.name(), amount, operation, slot));
        return this;
    }

    /**
     * Sets the item as unstackable.
     *
     * @param bool true to make the item unstackable, false otherwise
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setUnstackable(boolean bool) {
        setDataContainer("unstackable", (short) new Random().nextInt(), PersisDataType.SHORT);
        return this;
    }

    /**
     * Sets the custom model data for the item.
     *
     * @param data the custom model data value
     * @return this ItemCreator for method chaining
     */
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

    /**
     * Adds tags to the item.
     *
     * @param tags the tags to add
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addTags(String... tags) {
        for (String tag : tags) {
            String formatTag = formatTag(tag);
            data.set(new NamespacedKey(plugin, formatTag), PersistentDataType.STRING, tag);
        }
        return this;
    }

    /**
     * Adds a single tag to the item.
     *
     * @param key the key of the tag to add
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addTag(String key) {
        String formatTag = formatTag(key);
        addTag(formatTag, key);
        return this;
    }

    /**
     * Adds a tag with a specific value to the item.
     *
     * @param key the key of the tag to add
     * @param value the value of the tag
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addTag(String key, String value) {
        key = formatTag(key);
        data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        return this;
    }

    /**
     * Replaces an existing tag with a new key.
     *
     * @param oldKey the current key of the tag
     * @param newKey the new key to replace with
     * @return this ItemCreator for method chaining
     */
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

    /**
     * Gets the value of a specific tag.
     *
     * @param key the key of the tag to get the value from
     * @return the value of the tag, or null if not present
     */
    public String getTagValue(String key) {
        key = formatTag(key);
        return data.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    /**
     * Sets the value of a specific tag.
     *
     * @param key the key of the tag to set the value for
     * @param newValue the new value to set
     * @return this ItemCreator for method chaining
     */
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

    /**
     * Removes a tag from the item.
     *
     * @param tag the tag to remove
     * @return this ItemCreator for method chaining
     */
    public ItemCreator removeTag(String tag) {
        tag = formatTag(tag);
        if (hasTag(tag)) {
            data.remove(new NamespacedKey(plugin, tag));
        } else {
            sendConsoleMessage(ChatColor.RED + "Item tag not found: " + tag);
        }
        return this;
    }

    /**
     * Sets the durability of the item.
     *
     * @param durability the durability value
     * @return this ItemCreator for method chaining
     */
    public ItemCreator setDurability(int durability) {
        this.durability = durability < 0 ? 0 : (short) durability;
        return this;
    }

    /**
     * Sets data in the item's PersistentDataContainer.
     *
     * @param key the key of the data to set
     * @param value the value to set
     * @param type the type of the data
     * @return this ItemCreator for method chaining
     */
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

    /**
     * Adds an interaction to the item.
     *
     * @param itemInteraction the ItemInteraction to add
     * @return this ItemCreator for method chaining
     */
    public ItemCreator addInteraction(ItemInteraction itemInteraction) {
        if (itemInteraction == null) return this;
        ITEM_INTERACTIONS.add(itemInteraction);
        this.setDataContainer("interaction", itemInteraction.getName(), PersisDataType.STRING);
        return this;
    }
}

