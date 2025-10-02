package com.github.pinont.singularitylib.api.items;

import com.github.pinont.singularitylib.TestPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemCreatorMetaTest {
    private TestPlugin plugin;

    @BeforeEach
    public void setup() {
        MockBukkit.mock();
        this.plugin = MockBukkit.load(TestPlugin.class);
    }

    @AfterEach
    public void teardown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Pass: setName should match ItemStack meta display name")
    public void testSetNamePass() {
        String name = "Test Sword";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setName(name);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getDisplayName(), creator.getItemMeta().getDisplayName());
    }

    @Test
    @DisplayName("Fail: setName should not match different name")
    public void testSetNameFail() {
        String name = "Test Sword";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setName(name);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Wrong Name");
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getDisplayName(), creator.getItemMeta().getDisplayName());
    }

    @Test
    @DisplayName("Pass: setUnbreakable should match ItemStack meta unbreakable")
    public void testSetUnbreakablePass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setUnbreakable(true);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(true);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().isUnbreakable(), creator.getItemMeta().isUnbreakable());
    }

    @Test
    @DisplayName("Fail: setUnbreakable should not match different unbreakable value")
    public void testSetUnbreakableFail() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setUnbreakable(true);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(false);
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().isUnbreakable(), creator.getItemMeta().isUnbreakable());
    }

    @Test
    @DisplayName("Pass: setModelData should match ItemStack meta model data")
    public void testSetModelDataPass() {
        int modelData = 123;
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setModelData(modelData);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(modelData);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getCustomModelData(), creator.getItemMeta().getCustomModelData());
    }

    @Test
    @DisplayName("Fail: setModelData should not match different model data")
    public void testSetModelDataFail() {
        int modelData = 123;
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setModelData(modelData);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(456);
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getCustomModelData(), creator.getItemMeta().getCustomModelData());
    }

    @Test
    @DisplayName("Pass: addEnchant should match ItemStack meta enchantments")
    public void testAddEnchantPass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addEnchant(Enchantment.SHARPNESS, 2, true);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 2, true);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getEnchants(), creator.getItemMeta().getEnchants());
    }

    @Test
    @DisplayName("Fail: addEnchant should not match different enchantments")
    public void testAddEnchantFail() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addEnchant(Enchantment.SHARPNESS, 2, true);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 1, true);
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getEnchants(), creator.getItemMeta().getEnchants());
    }

    @Test
    @DisplayName("Pass: addLore should match ItemStack meta lore")
    public void testAddLorePass() {
        String lore = "Legendary Sword";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addLore(lore);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(java.util.Collections.singletonList(lore));
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().lore(), creator.getItemMeta().lore());
    }

    @Test
    @DisplayName("Fail: addLore should not match different lore")
    public void testAddLoreFail() {
        String lore = "Legendary Sword";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addLore(lore);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(java.util.Collections.singletonList("Ordinary Sword"));
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getLore(), creator.getItemMeta().getLore());
    }

    @Test
    @DisplayName("Pass: setType should match ItemStack type")
    public void testSetTypePass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setType(Material.IRON_SWORD);
        ItemStack stack = new ItemStack(Material.IRON_SWORD);
        assertEquals(stack.getType(), creator.getType());
    }

    @Test
    @DisplayName("Fail: setType should not match different type")
    public void testSetTypeFail() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setType(Material.IRON_SWORD);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        assertNotEquals(stack.getType(), creator.getType());
    }

    @Test
    @DisplayName("Pass: addItemFlag should match ItemStack meta flags")
    public void testAddItemFlagPass() {
        ItemFlag flag = ItemFlag.HIDE_ATTRIBUTES;
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addItemFlag(flag);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(flag);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getItemFlags(), creator.getItemMeta().getItemFlags());
    }

    @Test
    @DisplayName("Fail: addItemFlag should not match different flags")
    public void testAddItemFlagFail() {
        ItemFlag flag = ItemFlag.HIDE_ATTRIBUTES;
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addItemFlag(flag);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getItemFlags(), creator.getItemMeta().getItemFlags());
    }

    @Test
    @DisplayName("Pass: setItemMeta should match ItemStack meta")
    public void testSetItemMetaPass() {
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Meta Sword");
        stack.setItemMeta(meta);
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setItemMeta(meta);
        assertEquals(stack.getItemMeta(), creator.getItemMeta());
    }

    @Test
    @DisplayName("Fail: setItemMeta should not match different meta")
    public void testSetItemMetaFail() {
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Meta Sword");
        stack.setItemMeta(meta);
        ItemMeta meta2 = stack.getItemMeta();
        meta2.setDisplayName("Other Sword");
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setItemMeta(meta2);
        assertNotEquals(stack.getItemMeta(), creator.getItemMeta());
    }

    @Test
    @DisplayName("Pass: setAmount via constructor should match ItemStack amount")
    public void testSetAmountPass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD, 5);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 5);
        assertEquals(stack.getAmount(), creator.getAmount());
    }

    @Test
    @DisplayName("Fail: setAmount via constructor should not match different amount")
    public void testSetAmountFail() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD, 5);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 1);
        assertNotEquals(stack.getAmount(), creator.getAmount());
    }

    @Test
    @DisplayName("Pass: addTag should match ItemStack persistent data")
    public void testAddTagPass() {
        String tag = "test_tag";
        String value = "test_value";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addTag(tag, value);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        String formattedKey = String.join("_", tag.split(" ")).toLowerCase();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING, value);
        stack.setItemMeta(meta);
        assertEquals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING),
                     creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
    }

    @Test
    @DisplayName("Fail: addTag should not match different persistent data")
    public void testAddTagFail() {
        String tag = "test_tag";
        String value = "test_value";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addTag(tag, value);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        String formattedKey = String.join("_", tag.split(" ")).toLowerCase();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING, "wrong_value");
        stack.setItemMeta(meta);
        assertNotEquals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING),
                        creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
    }

    @Test
    @DisplayName("Pass: add multiple lore lines should match ItemStack meta lore")
    public void testAddMultipleLorePass() {
        java.util.List<String> loreLines = java.util.Arrays.asList("Line 1", "Line 2", "Line 3");
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD);
        for (String line : loreLines) creator.addLore(line);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(loreLines);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getLore(), creator.getItemMeta().getLore());
    }

    @Test
    @DisplayName("Fail: add multiple lore lines should not match different lore")
    public void testAddMultipleLoreFail() {
        java.util.List<String> loreLines = java.util.Arrays.asList("Line 1", "Line 2", "Line 3");
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD);
        for (String line : loreLines) creator.addLore(line);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(java.util.Arrays.asList("Wrong Line 1", "Wrong Line 2"));
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getLore(), creator.getItemMeta().getLore());
    }

    @Test
    @DisplayName("Pass: add multiple enchantments should match ItemStack meta enchantments")
    public void testAddMultipleEnchantsPass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.SHARPNESS, 2, true)
            .addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 2, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getEnchants(), creator.getItemMeta().getEnchants());
    }

    @Test
    @DisplayName("Fail: add multiple enchantments should not match different enchantments")
    public void testAddMultipleEnchantsFail() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD)
            .addEnchant(Enchantment.SHARPNESS, 2, true)
            .addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 1, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getEnchants(), creator.getItemMeta().getEnchants());
    }

    @Test
    @DisplayName("Pass: add multiple item flags should match ItemStack meta flags")
    public void testAddMultipleItemFlagsPass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD)
            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
            .addItemFlag(ItemFlag.HIDE_ENCHANTS);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getItemFlags(), creator.getItemMeta().getItemFlags());
    }

    @Test
    @DisplayName("Fail: add multiple item flags should not match different flags")
    public void testAddMultipleItemFlagsFail() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD)
            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
            .addItemFlag(ItemFlag.HIDE_ENCHANTS);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(meta);
        assertNotEquals(stack.getItemMeta().getItemFlags(), creator.getItemMeta().getItemFlags());
    }

    @Test
    @DisplayName("Pass: setTagValue should overwrite persistent data")
    public void testSetTagValuePass() {
        String tag = "test tag";
        String value1 = "value1";
        String value2 = "value2";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addTag(tag, value1).setTagValue(tag, value2);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        String formattedKey = String.join("_", tag.split(" ")).toLowerCase();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING, value2);
        stack.setItemMeta(meta);
        assertEquals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING),
                     creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
    }

    @Test
    @DisplayName("Fail: setTagValue should not match wrong persistent data")
    public void testSetTagValueFail() {
        String tag = "test tag";
        String value1 = "value1";
        String value2 = "value2";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addTag(tag, value1).setTagValue(tag, value2);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        String formattedKey = String.join("_", tag.split(" ")).toLowerCase();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING, "wrong_value");
        stack.setItemMeta(meta);
        assertEquals(value2, creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
        assertNotEquals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING),
                        creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
    }

    @Test
    @DisplayName("Pass: replaceTag should overwrite persistent data")
    public void testReplaceTagPass() {
        String tag1 = "test tag1";
        String tag2 = "test tag2";
        String value1 = "value1";
        String value2 = "value2";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addTag(tag1, value1).replaceTag(tag1, tag2).setTagValue(tag2, value2);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        String formattedKey = String.join("_", tag2.split(" ")).toLowerCase();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING, value2);
        stack.setItemMeta(meta);
        assertEquals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING),
                creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
    }

    @Test
    @DisplayName("Fail: replaceTag should overwrite persistent data")
    public void testReplaceTagFail() {
        String tag1 = "test tag1";
        String tag2 = "test tag2";
        String value1 = "value1";
        String value2 = "value2";
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addTag(tag1, value1).replaceTag(tag1, tag2).setTagValue(tag2, value2);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        String formattedKey = String.join("_", tag2.split(" ")).toLowerCase();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING, "wrong_value");
        stack.setItemMeta(meta);
        assertEquals(value2, creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
        assertNotEquals(meta.getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING),
                creator.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, formattedKey), PersistentDataType.STRING));
    }

    @Test
    @DisplayName("Pass: setName with null should match ItemStack meta display name")
    public void testSetNameNullPass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).setName((String) null);
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(null);
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getDisplayName(), creator.getItemMeta().getDisplayName());
    }

    @Test
    @DisplayName("Pass: addLore with empty string should match ItemStack meta lore")
    public void testAddLoreEmptyPass() {
        ItemCreator creator = new ItemCreator(Material.DIAMOND_SWORD).addLore("");
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(java.util.Collections.singletonList(""));
        stack.setItemMeta(meta);
        assertEquals(stack.getItemMeta().getLore(), creator.getItemMeta().getLore());
    }
}
