package com.github.pinont.singularitylib.api.utils;

import com.github.pinont.singularitylib.api.manager.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

/**
 * Utility class for managing MySQL database connections.
 * Provides functionality to connect to MySQL databases with automatic configuration setup.
 */
public class MySQL {

    private Connection connection;
    private Plugin plugin;

    /**
     * Default constructor for MySQL utility class.
     */
    public MySQL(Plugin plugin) {
        this.plugin = plugin;
    }

    private void defaultConfigSetup(Plugin plugin, String configPath) {
        ConfigManager configManager = new ConfigManager(plugin, configPath);
        configManager.set("database.host", "localhost");
        configManager.set("database.port", 3306);
        configManager.set("database.databaseName", "database");
        configManager.set("database.username", "root");
        configManager.set("database.password", "password");
        configManager.set("database.timezone", "UTC");
        configManager.set("database.useSSL", "false");
        configManager.saveConfig();
        sendConsoleMessage(Component.text("[DB] Please set the database configuration in database.yml", NamedTextColor.YELLOW));
    }

    /**
     * Gets a connection to the database using the default config file.
     *
     * @return the database connection, or null if connection failed
     */
    public Connection getConnection() {
        return getConnection("config.yml");
    }

    /**
     * Gets a connection to the database using the specified config file.
     *
     * @param configPath the path to the configuration file
     * @return the database connection, or null if connection failed
     */
    public Connection getConnection(String configPath) {
        ConfigManager configManager = new ConfigManager(plugin, configPath);
        FileConfiguration config = configManager.getConfig();
        boolean database =
                config.getString("database.host") == null ||
                        config.getString("database.port") == null || config.getString("database.databaseName") == null ||
                        config.getString("database.username") == null || config.getString("database.password") == null ||
                        config.getString("database.timezone") == null || config.getString("database.useSSL") == null;
        if (database) {
            defaultConfigSetup(plugin, configPath);
            return null;
        }
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String dbName = config.getString("database.databaseName");
        String username = config.getString("database.username");
        String password = config.getString("database.password");
        String timeZone = config.getString("database.timezone", "UTC");
        String ssl = config.getString("database.useSSL", "false");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=" + ssl + "&serverTimezone=" + timeZone + "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
        sendDebugMessage(Component.text("[DB] Connecting to MySQL: host=" + host + ", port=" + port + ", dbName=" + dbName + ", user=" + username, NamedTextColor.YELLOW));
        try {
            // Check if database exists, if not, create it
            String baseUrl = "jdbc:mysql://" + host + ":" + port + "/?useSSL=" + ssl + "&serverTimezone=" + timeZone;
            try (Connection baseConn = java.sql.DriverManager.getConnection(baseUrl, username, password)) {
                sendConsoleMessage(Component.text("[DB] Connected to MySQL server for database check.", NamedTextColor.YELLOW));
                try (java.sql.Statement stmt = baseConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
                    sendDebugMessage(Component.text("[DB] Ensured database '" + dbName + "' exists.", NamedTextColor.YELLOW));
                }
            }
            // Now connect to the actual database
            if (connection == null || connection.isClosed()) {
                connection = java.sql.DriverManager.getConnection(url, username, password);
                sendDebugMessage(Component.text("[DB] Connected to database '" + dbName + "'.", NamedTextColor.GREEN));
            }
        } catch (Exception e) {
            sendConsoleMessage(Component.text("[DB] MySQL connection error:\n" + e.getMessage(), NamedTextColor.RED));
            return null;
        }
        return connection;
    }

    /**
     * Initializes the database connection using the specified config file.
     *
     * @param configPath the path to the configuration file
     */
    public void init(String configPath) {
        connection = getConnection(configPath);
        if (connection == null) {
            sendConsoleMessage(Component.text("[DB] Failed to connect to the database. Please check your configuration in database.yml", NamedTextColor.RED));
        } else {
            sendConsoleMessage(Component.text("[DB] Successfully connected to the database.", NamedTextColor.GREEN));
        }
    }

    /**
     * Closes the database connection if it exists.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
