package com.github.pinont.singularitylib.api.utils;

import com.github.pinont.singularitylib.TestPlugin;
import com.github.pinont.singularitylib.api.enums.PlayerInventorySlotType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommonTest {

    private ServerMock server;
    private PlayerMock player;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        player = server.addPlayer("TestPlayer");
        MockBukkit.load(TestPlugin.class);

    }

    @AfterEach
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Test
    void testColorizeSuccess() {
        Common common = new Common();
        String input = "<red>Test";
        Component result = common.colorize(input);
        assertEquals(MiniMessage.miniMessage().deserialize(input), result);
    }

    @Test
    void testColorizeNoItalic() {
        Common common = new Common();
        String input = "<red>Test";
        Component result = common.colorize(input, true);
        assertEquals(MiniMessage.miniMessage().deserialize("<!italic><red>Test"), result);
    }

    @Test
    void testGetAllItemsMaterialsSuccess() {
        Material[] materials = Common.getAllItemsMaterials();
        assertTrue(materials.length > 0);
        assertFalse(Arrays.asList(materials).contains(Material.AIR));
    }

    @Test
    void testIsItemIsMaterialSuccess() {
        assertTrue(Common.isItemIsMaterial(Material.DIAMOND_SWORD));
        assertFalse(Common.isItemIsMaterial(Material.AIR));
    }

    @Test
    void testIsAirSuccess() {
        assertTrue(Common.isAir(null));
        assertTrue(Common.isAir(new ItemStack(Material.AIR)));
        assertFalse(Common.isAir(new ItemStack(Material.DIAMOND_SWORD)));
    }

    @Test
    void testCheckBooleanSuccess() {
        assertDoesNotThrow(() -> Common.checkBoolean(true, "Should not fail"));
    }

    @Test
    void testCheckBooleanFail() {
        assertDoesNotThrow(() -> Common.checkBoolean(false, "Fail: %s", "reason"));
    }

    @Test
    void testGetItemSuccess() {
        ItemStack item = Common.getItem("DIAMOND_SWORD");
        assertEquals(Material.DIAMOND_SWORD, item.getType());
    }

    @Test
    void testGetItemFail() {
        assertThrows(IllegalArgumentException.class, () -> Common.getItem("NOT_A_MATERIAL"));
    }

    @Test
    void testSneakySuccess() {
        assertDoesNotThrow(() -> Common.sneaky(new Exception("Test error")));
    }

    @Test
    void testGetOnlinePlayersNamesSuccess() {
        List<String> names = Common.getOnlinePlayersNames();
        assertTrue(names.contains("TestPlayer"));
    }

    @Test
    void testNormalizeStringNameSuccess() {
        assertEquals("Diamond Sword", Common.normalizeStringName("DIAMOND_SWORD"));
        assertEquals("Test", Common.normalizeStringName("TEST"));
    }

    @Test
    void testIsMainHandEmptySuccess() {
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        assertTrue(Common.isMainHandEmpty(player));
    }

    @Test
    void testIsMainHandEmptyFail() {
        
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        assertFalse(Common.isMainHandEmpty(player));
    }

    @Test
    void testIntegerToRomanNumeralSuccess() {
        assertEquals("I", Common.IntegerToRomanNumeral(1));
        assertEquals("IV", Common.IntegerToRomanNumeral(4));
        assertEquals("MCMXCIV", Common.IntegerToRomanNumeral(1994));
    }

    @Test
    void testIntegerToRomanNumeralFail() {
        assertEquals("Invalid Roman Number Value", Common.IntegerToRomanNumeral(0));
        assertEquals("Invalid Roman Number Value", Common.IntegerToRomanNumeral(4000));
    }

    @Test
    void testNormalizeStringNameFail() {
        assertEquals("", Common.normalizeStringName(""));
    }

    @Test
    void testResetStringColorStringSuccess() {
        assertEquals("Test", Common.resetStringColor("§cTest"));
    }

    @Test
    void testResetStringColorComponentSuccess() {
        Component comp = MiniMessage.miniMessage().deserialize("<red>Test");
        assertEquals(MiniMessage.miniMessage().serialize(comp), Common.resetStringColor(comp));
    }

    @Test
    void testCheckAirInSlotMainHandSuccess() {
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        assertTrue(Common.checkAirInSlot(PlayerInventorySlotType.MAINHAND, player));
    }

    @Test
    void testCheckAirInSlotMainHandFail() {
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        assertFalse(Common.checkAirInSlot(PlayerInventorySlotType.MAINHAND, player));
    }

    @Test
    void testGetItemInSlotMainHandSuccess() {
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        assertEquals(Material.DIAMOND_SWORD, Common.getItemInSlot(PlayerInventorySlotType.MAINHAND, player).getType());
    }

    @Test
    void testGetItemInSlotMainHandFail() {
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        assertEquals(Material.AIR, Common.getItemInSlot(PlayerInventorySlotType.MAINHAND, player).getType());
    }

    @Test
    void testGetNearestPlayerNull() {
        Location location = new Location(MockBukkit.getMock().getWorlds().get(0), 0, 0, 0);
        assertNull(Common.getNearestPlayer(location, 10));
    }
}
