package com.github.pinont.singularitylib.api.registry;

import com.github.pinont.singularitylib.TestPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

/**
 * Tests the runtime plugin auto-discovery registry.
 */
public class PluginRegistryTest {

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        PluginRegistry.clear();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        PluginRegistry.clear();
    }

    @Test
    @DisplayName("CorePlugin registers itself on load")
    public void registersOnLoad() {
        TestPlugin loaded = MockBukkit.load(TestPlugin.class);
        Assertions.assertEquals(1, PluginRegistry.count(), "plugin registered");
        Assertions.assertTrue(PluginRegistry.find("TestPlugin").isPresent(), "findable by name");
    }

    @Test
    @DisplayName("registry lists loaded plugins and handles missing lookups")
    public void listAndMiss() {
        MockBukkit.load(TestPlugin.class);
        Assertions.assertFalse(PluginRegistry.plugins().isEmpty());
        Assertions.assertTrue(PluginRegistry.find("DoesNotExist").isEmpty());
        Assertions.assertNull(PluginRegistry.get("DoesNotExist"));
    }
}