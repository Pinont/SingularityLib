package com.github.pinont.singularitylib.api.nms;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

/**
 * Tests the NmsBridge soft-loading behavior. In the lib's own test environment
 * SingularityNMS is NOT on the classpath, so the bridge must degrade gracefully
 * (available=false, no crashes, original stack returned).
 */
public class NmsBridgeTest {

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("bridge reports unavailable without SingularityNMS on classpath")
    public void unavailable() {
        boolean available = NmsBridge.isAvailable();
        System.out.println("NmsBridge.isAvailable() = " + available);
        Assertions.assertFalse(available, "lib test env has no singularitynms jar");
    }

    @Test
    @DisplayName("readItemNbt returns null (no NMS) — no crash")
    public void readReturnsNull() {
        Assertions.assertNull(NmsBridge.readItemNbt(null));
    }

    @Test
    @DisplayName("applyItemNbt returns original when NMS absent")
    public void applyDegrades() {
        ItemStack item = new ItemStack(Material.STONE);
        Assertions.assertSame(item, NmsBridge.applyItemNbt(item, null),
                "no NMS -> original stack returned");
    }

    @Test
    @DisplayName("hasVanillaNbt false without NMS")
    public void hasNbtFalse() {
        Assertions.assertFalse(NmsBridge.hasVanillaNbt(new ItemStack(Material.STONE)));
    }
}