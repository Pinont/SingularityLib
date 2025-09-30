package com.github.pinont.singularitylib.items;

import com.github.pinont.singularitylib.TestPlugin;
import com.github.pinont.singularitylib.api.items.ItemCreator;
import com.github.pinont.singularitylib.api.utils.Common;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;

public class ItemCreatorTest {

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

    private ItemStack defaultItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Test Sword");
        item.setItemMeta(meta);
        return item;
    }

    @Test
    @DisplayName("Check item meta equality")
    public void checkMeta() {
        Assertions.assertEquals(new ItemCreator(Material.DIAMOND_SWORD, 1).setName("Test Sword").getItemMeta(), defaultItem().getItemMeta());
        Assertions.assertNotEquals(new ItemCreator(Material.DIAMOND_SWORD, 1).setName("Test Swosd").getMeta(), defaultItem().getItemMeta());
    }


    // Problem with persistent data container equality check in mockbukkit I'm checking rn.
//    @Test
//    @DisplayName("Check item tags equality")
//    public void checkTags() {
//        String testTag1 = "Test Tag1";
//        String replacingTestTag1 = String.join("_", testTag1.split(" ")).toLowerCase();
//        String testTag2 = "Test Tag2";
//        String replacingTestTag2 = String.join("_", testTag2.split(" ")).toLowerCase();
//        String testValue1 = "Value1";
//        String testValue2 = "Value2";
//        ItemCreator itemCreator = new ItemCreator(defaultItem()).addTag(testTag1);
//
//        Assertions.assertEquals(itemCreator.create(), setItemMeta(defaultItem(), addPersistentData(defaultItem().getItemMeta(), replacingTestTag1, replacingTestTag1)));
//        Assertions.assertNotEquals(itemCreator.replaceTag(testTag1, "something1").create(), setItemMeta(defaultItem(), addPersistentData(defaultItem().getItemMeta(), replacingTestTag1, replacingTestTag1)));
//        Assertions.assertNotEquals(itemCreator.replaceTag(testTag1, "something1").create(), setItemMeta(defaultItem(), addPersistentData(addPersistentData(defaultItem().getItemMeta(), replacingTestTag1, replacingTestTag1), replacingTestTag2, replacingTestTag2)));
//        Assertions.assertEquals(itemCreator.setTagValue(testTag1, testValue1).create(), setItemMeta(defaultItem(), addPersistentData(defaultItem().getItemMeta(), replacingTestTag1, testValue1)));
//        Assertions.assertNotEquals(itemCreator.setTagValue(testTag2, testValue2).create(), setItemMeta(defaultItem(), addPersistentData(defaultItem().getItemMeta(), replacingTestTag2, testValue2)));
//
//        ItemCreator itemCreator2 = itemCreator.addTag(testTag1).addTag(testTag2);
//        ItemStack item2 = itemCreator2.create();
//        Assertions.assertEquals(itemCreator.getTagValue(testTag1), getValue(item2.getItemMeta(), replacingTestTag1));
//    }

    private ItemMeta addPersistentData(ItemMeta meta, String key, String value) {
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        return meta;
    }

    private ItemStack setItemMeta(ItemStack item, ItemMeta meta) {
        item.setItemMeta(meta);
        return item;
    }

    private String getValue(ItemMeta itemMeta ,String key) {
        return itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    @Test
    @DisplayName("Check name of all item with default name")
    public void checkName() {
        for (Material material : Common.getAllItemsMaterials()) {
            ItemCreator itemCreator = new ItemCreator(material);
            ItemStack item = itemCreator.create();
            Assertions.assertEquals(Common.normalizeStringName(item.getType().name().replace("_", " ")), itemCreator.getName());
            Assertions.assertNotEquals(item.getType().name().replace("_", " "), itemCreator.getName());
        }
    }
}
