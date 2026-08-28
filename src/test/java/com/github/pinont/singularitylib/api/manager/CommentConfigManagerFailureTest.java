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
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Failure-path coverage for CommentConfigManager: merge conflicts, missing files,
 * invalid content, save round-trips under adversity.
 */
public class CommentConfigManagerFailureTest {

    @TempDir
    File tmp;

    private JavaPlugin plugin;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(com.github.pinont.singularitylib.TestPlugin.class);
        plugin.getDataFolder().mkdirs();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("mergeDefaults does NOT overwrite an existing user value (type conflict)")
    public void mergeDoesNotClobberUserValue() {
        CommentConfigManager mgr = new CommentConfigManager(plugin, "conflict.yml");
        mgr.set("level", 5); // user set int
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("level", "ten"); // default is string — must NOT clobber
        defaults.put("newKey", 1);
        mgr.mergeDefaults(defaults);

        Assertions.assertEquals(5, mgr.get("level"), "user int kept, default ignored");
        Assertions.assertEquals(1, mgr.get("newKey"), "missing key added");
    }

    @Test
    @DisplayName("mergeDefaults returns false when nothing was added")
    public void mergeNoChange() {
        CommentConfigManager mgr = new CommentConfigManager(plugin, "nochange.yml");
        mgr.set("a", 1);
        mgr.set("b", 2);
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("a", 99);
        defaults.put("b", 99);
        Assertions.assertFalse(mgr.mergeDefaults(defaults), "all keys exist -> no change");
    }

    @Test
    @DisplayName("saving with a corrupted/absent file does not throw")
    public void missingFileSave() throws IOException {
        CommentConfigManager mgr = new CommentConfigManager(plugin, "missing.yml");
        File f = new File(plugin.getDataFolder(), "missing.yml");
        if (f.exists()) f.delete();
        Assertions.assertDoesNotThrow(mgr::save, "save without existing file is safe");
        Assertions.assertTrue(f.exists(), "save creates the file");
        // Round-trip: reload what we saved (empty config) — must not throw
        Assertions.assertDoesNotThrow(() -> new CommentConfigManager(plugin, "missing.yml"));
    }

    @Test
    @DisplayName("getBoolean/getInt/getString return defaults for absent paths")
    public void absentGettersDefault() {
        CommentConfigManager mgr = new CommentConfigManager(plugin, "absent.yml");
        Assertions.assertEquals(true, mgr.getBoolean("nope.flag", true));
        Assertions.assertEquals(42, mgr.getInt("nope.count", 42));
        Assertions.assertEquals("fallback", mgr.getString("nope.str", "fallback"));
    }
}