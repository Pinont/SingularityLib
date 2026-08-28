package com.github.pinont.singularitylib.api.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tests the SQLite backend end-to-end (create table, insert, query) — async API.
 */
public class DatabaseTest {

    @TempDir
    File tmp;

    private com.github.pinont.singularitylib.TestPlugin plugin;
    private Database db;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(com.github.pinont.singularitylib.TestPlugin.class);
        plugin.getDataFolder().mkdirs();
        db = Database.sqlite(plugin, "test.db");
    }

    @AfterEach
    public void tearDown() {
        if (db != null) db.close();
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("SQLite: create table, insert, query round-trip")
    public void sqliteRoundTrip() throws Exception {
        db.update("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT)").get(10, TimeUnit.SECONDS);
        db.update("INSERT OR REPLACE INTO players (uuid, name) VALUES (?, ?)", "abc-123", "Steve").get(10, TimeUnit.SECONDS);

        List<String> names = db.<List<String>>query(conn -> {
            try (var rs = conn.createStatement().executeQuery("SELECT name FROM players WHERE uuid='abc-123'")) {
                List<String> out = new java.util.ArrayList<>();
                while (rs.next()) out.add(rs.getString(1));
                return out;
            }
        }).get(10, TimeUnit.SECONDS);

        Assertions.assertEquals(List.of("Steve"), names, "row round-tripped");
    }

    @Test
    @DisplayName("SQLite: connection opens and file is created")
    public void sqliteFileCreated() throws Exception {
        Connection c = db.getConnection();
        Assertions.assertFalse(c.isClosed(), "connection open");
        Assertions.assertTrue(new File(plugin.getDataFolder(), "test.db").exists(), "db file created");
    }
}