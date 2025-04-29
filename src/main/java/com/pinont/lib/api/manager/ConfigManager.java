package com.pinont.lib.api.manager;

import com.pinont.lib.Singularity;
import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final File configFile;
    private final FileConfiguration config;
    private final String fileName;
    public boolean isFirstLoad;

    public ConfigManager(Plugin plugin, String fileName) {
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

    public ConfigManager(Plugin plugin, String subFolder, String fileName) {
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

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
            config.options().copyDefaults(true);
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            Bukkit.getLogger().warning("An error occurred while loading the config file: " + fileName);
            return null;
        }
        return config;
    }

    public boolean isFirstLoad() {
        return isFirstLoad;
    }
}