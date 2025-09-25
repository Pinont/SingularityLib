package com.pinont.lib.api.utils;

import com.pinont.lib.api.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;

import static com.pinont.lib.plugin.CorePlugin.sendConsoleMessage;
import static com.pinont.lib.plugin.CorePlugin.sendDebugMessage;

public class MYSQL {

    private Connection connection;

    private void defaultConfigSetup(String configPath) {
        ConfigManager configManager = new ConfigManager(configPath);
        configManager.set("database.host", "localhost");
        configManager.set("database.port", 3306);
        configManager.set("database.databaseName", "database");
        configManager.set("database.username", "root");
        configManager.set("database.password", "password");
        configManager.set("database.timezone", "UTC");
        configManager.set("database.useSSL", "false");
        configManager.saveConfig();
        sendConsoleMessage(ChatColor.YELLOW + "[DB] Please set the database configuration in database.yml");
    }

    public Connection getConnection() {
        return getConnection("config.yml");
    }

    public Connection getConnection(String configPath) {
        ConfigManager configManager = new ConfigManager(configPath);
        FileConfiguration config = configManager.getConfig();
        boolean database =
                config.getString("database.host") == null ||
                        config.getString("database.port") == null || config.getString("database.databaseName") == null ||
                        config.getString("database.username") == null || config.getString("database.password") == null ||
                        config.getString("database.timezone") == null || config.getString("database.useSSL") == null;
        if (database) {
            defaultConfigSetup(configPath);
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
        sendDebugMessage(ChatColor.YELLOW + "[DB] Connecting to MySQL: host=" + host + ", port=" + port + ", dbName=" + dbName + ", user=" + username);
        try {
            // Check if database exists, if not, create it
            String baseUrl = "jdbc:mysql://" + host + ":" + port + "/?useSSL=" + ssl + "&serverTimezone=" + timeZone;
            try (Connection baseConn = java.sql.DriverManager.getConnection(baseUrl, username, password)) {
                sendConsoleMessage(ChatColor.YELLOW + "[DB] Connected to MySQL server for database check.");
                try (java.sql.Statement stmt = baseConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
                    sendDebugMessage(ChatColor.YELLOW + "[DB] Ensured database '" + dbName + "' exists.");
                }
            }
            // Now connect to the actual database
            if (connection == null || connection.isClosed()) {
                connection = java.sql.DriverManager.getConnection(url, username, password);
                sendDebugMessage(ChatColor.GREEN + "[DB] Connected to database '" + dbName + "'.");
            }
        } catch (Exception e) {
            sendConsoleMessage(ChatColor.RED + "[DB] MySQL connection error:\n" + e.getMessage());
            return null;
        }
        return connection;
    }

    public void init(String configPath) {
        connection = getConnection(configPath);
        if (connection == null) {
            sendConsoleMessage(ChatColor.RED + "[DB] Failed to connect to the database. Please check your configuration in database.yml");
        } else {
            sendConsoleMessage(ChatColor.GREEN + "[DB] Successfully connected to the database.");
        }
    }

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
