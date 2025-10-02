package io.github.pinont.singularitylib.plugins;

import io.github.pinont.singularitylib.TestPlugin;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;

public class CorePluginTest {

    public TestPlugin plugin;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        this.plugin = MockBukkit.load(TestPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test CorePlugin Initialization")
    public void load() {
        // Check if the plugin is loaded
        Assertions.assertEquals(TestPlugin.getInstance(), this.plugin);
        // Check if the plugin is enabled
        assert this.plugin.isEnabled();
        // Check if the plugin name is correct
        assert this.plugin.getName().equals("TestPlugin");
        // Check if the plugin is marked as test
        assert this.plugin.isTest;
    }

    @Test
    @DisplayName("Test MySQL Dependency Absence")
    public void noMySQL() {
        // Check if the MySQL dependency is absent
        Assertions.assertNull(this.plugin.getConfig().getString("mysql.host"));
        Assertions.assertNull(this.plugin.getConfig().getString("mysql.database"));
        Assertions.assertNull(this.plugin.getConfig().getString("mysql.username"));
        Assertions.assertNull(this.plugin.getConfig().getString("mysql.password"));
    }
}
