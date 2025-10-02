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
 * This class provides functionality to create, load, save, and manipulate YAML configuration files.
 */
public class ConfigManager {

    private final File configFile;
    private final FileConfiguration config;
    private final String fileName;
    private final Plugin plugin = getInstance();
    private boolean isFirstLoad;

    /**
     * Creates a ConfigManager for a configuration file in the plugin's data folder.
     *
     * @param fileName the name of the configuration file
     */
    public ConfigManager(String fileName) {
        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                isFirstLoad = true;
            } catch (IOException e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
        } else {
            isFirstLoad = false;
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Checks if a configuration file exists in a specific subfolder.
     *
     * @param subFolder the subfolder to check in
     * @param fileName the name of the configuration file
     * @return true if the file exists, false otherwise
     */
    public static boolean isExists(String subFolder, String fileName) {
        return new File(getInstance().getDataFolder() + "/" + subFolder, fileName).exists();
    }

    /**
     * Creates a ConfigManager for a configuration file in a specific subfolder.
     *
     * @param subFolder the subfolder where the configuration file should be located
     * @param fileName the name of the configuration file
     */
    public ConfigManager(String subFolder, String fileName) {
        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder() + "/" + subFolder, fileName);
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                isFirstLoad = true;
            } catch (IOException e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
        } else {
            isFirstLoad = false;
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Sets a value at the specified path in the configuration.
     *
     * @param path the configuration path
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
     * This method writes all changes made to the configuration back to the file.
     */
    public void saveConfig() {
        try {
            config.save(configFile);
            config.options().copyDefaults(true);
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
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
            return null;
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