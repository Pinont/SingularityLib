package com.pinont.lib.plugin;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class CorePlugin extends JavaPlugin {

    @Getter
    private static JavaPlugin plugin;

    private static CorePlugin instance;

    public static CorePlugin getInstance() {
        if (instance == null) {
            try {
                instance = JavaPlugin.getPlugin(CorePlugin.class);

            } catch (final IllegalStateException ex) {
                if (Bukkit.getPluginManager().getPlugin("PlugManX") != null)
                    Bukkit.getLogger().severe("Failed to get instance of the plugin, if you reloaded using PlugManX you need to do a clean restart instead.");
                throw ex;
            }
            Objects.requireNonNull(instance, "Cannot get a new instance! Have you reloaded?");
        }

        return instance;
    }

    @Override
    public final void onEnable() {
        plugin = this;
        getLogger().info("Plugin enabled");
    }

    @Override
    public final void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin disabled");
    }
}
