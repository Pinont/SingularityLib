package com.github.pinont.singularitylib.api.manager;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests CommentConfigManager: default-merge, comment rendering, save round-trip.
 */
public class CommentConfigManagerTest {

    @TempDir
    File tmp;

    private JavaPlugin plugin;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        // Use a fake plugin with a dataFolder pointing at tmp
        plugin = MockBukkit.load(com.github.pinont.singularitylib.TestPlugin.class);
        plugin.getDataFolder().mkdirs();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("mergeDefaults adds missing keys but preserves existing values")
    public void mergeDefaults() {
        File cfgFile = new File(plugin.getDataFolder(), "test.yml");
        CommentConfigManager mgr = new CommentConfigManager(plugin, "test.yml");
        mgr.set("debug", true); // user value
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("debug", false);
        defaults.put("prefix", "[MyPlugin]");
        boolean changed = mgr.mergeDefaults(defaults);
        Assertions.assertTrue(changed, "new key added");
        Assertions.assertEquals(true, mgr.getBoolean("debug", false), "user value wins");
        Assertions.assertEquals("[MyPlugin]", mgr.getString("prefix", ""), "default added");
        mgr.save();
        Assertions.assertTrue(cfgFile.exists(), "file written");
    }

    @Test
    @DisplayName("save renders comments above their paths")
    public void commentsRender() throws Exception {
        CommentConfigManager mgr = new CommentConfigManager(plugin, "commented.yml");
        mgr.comment("settings.debug", "Enable debug logging");
        mgr.set("settings.debug", false);
        mgr.set("settings.verbose", true);
        mgr.save();

        String content = Files.readString(new File(plugin.getDataFolder(), "commented.yml").toPath());
        Assertions.assertTrue(content.contains("# Enable debug logging"), "comment present, got:\n" + content);
        Assertions.assertTrue(content.contains("debug: false"), "value saved");
        Assertions.assertTrue(content.contains("verbose: true"), "other key saved");
    }
}