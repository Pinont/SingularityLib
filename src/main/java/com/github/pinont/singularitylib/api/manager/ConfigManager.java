package com.github.pinont.singularitylib.api.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

import static com.github.pinont.singularitylib.plugin.CorePlugin.getInstance;

/**
 * Manages configuration files for the plugin.
 * <p>
 * This class provides functionality to create, load, save, reload, and manipulate YAML configuration files
 * in the plugin's data folder or subfolders.
 */
public class ConfigManager {

    /** The configuration file on disk */
    private final File configFile;

    /** The in-memory representation of the YAML configuration */
    private FileConfiguration config;

    /** The name of the configuration file */
    private final String fileName;

    /** Reference to the plugin instance */
    private final Plugin plugin = getInstance();

    /** True if the config file was just created (first load) */
    private boolean isFirstLoad;

    /**
     * Creates a ConfigManager for a configuration file in the plugin's data folder.
     *
     * @param fileName the name of the configuration file
     */
    public ConfigManager(String fileName) {
        this.fileName = fileName;
        this.configFile = new File(plugin.getDataFolder(), fileName);
        initializeFile();
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Creates a ConfigManager for a configuration file in a specific subfolder.
     *
     * @param subFolder the subfolder where the configuration file should be located
     * @param fileName  the name of the configuration file
     */
    public ConfigManager(String subFolder, String fileName) {
        this.fileName = fileName;
        this.configFile = new File(plugin.getDataFolder() + "/" + subFolder, fileName);
        initializeFile();
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Checks if a configuration file exists in a specific subfolder.
     *
     * @param subFolder the subfolder to check in
     * @param fileName  the name of the configuration file
     * @return true if the file exists, false otherwise
     */
    public static boolean isExists(String subFolder, String fileName) {
        return new File(getInstance().getDataFolder() + "/" + subFolder, fileName).exists();
    }

    /**
     * Initializes the configuration file by creating it if it does not exist.
     */
    private void initializeFile() {
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                isFirstLoad = true;
            } catch (IOException e) {
                Bukkit.getLogger().warning("Failed to create config file: " + e.getMessage());
            }
        } else {
            isFirstLoad = false;
        }
    }

    /**
     * Sets a value at the specified path in the configuration.
     *
     * @param path  the configuration path
     * @param value the value to set
     */
    public void set(String path, Object value) {
        config.set(path, value);
    }

    /**
     * Gets a value from the specified path in the configuration.
     *
     * @param path the configuration path
     * @return the value at the specified path, or null if not found
     */
    public Object get(String path) {
        return config.get(path);
    }

    /**
     * Saves the configuration to the file.
     * <p>
     * Writes all changes made to the configuration back to the disk.
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to save config file: " + e.getMessage());
        }
    }

    /**
     * Reloads the configuration from disk.
     * <p>
     * Useful when the file is manually modified while the server is running.
     */
    public void reloadConfig() {
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            Bukkit.getLogger().warning("Config file does not exist: " + fileName);
        }
    }

    /**
     * Gets the FileConfiguration instance.
     *
     * @return the FileConfiguration instance, or null if there was an error loading
     */
    public FileConfiguration getConfig() {
        if (config == null) {
            Bukkit.getLogger().warning("An error occurred while loading the config file: " + fileName);
        }
        return config;
    }

    /**
     * Checks if this is the first time the configuration file is being loaded.
     *
     * @return true if this is the first load (file was just created), false otherwise
     */
    public boolean isFirstLoad() {
        return isFirstLoad;
    }
}
