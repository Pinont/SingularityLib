package com.pinont.lib.plugin;

import com.pinont.lib.plugin.events.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public abstract class CorePlugin extends JavaPlugin {
    /**
     * The plugin instance.
     */
    private static JavaPlugin plugin;

    private static CorePlugin instance;

    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static CorePlugin getInstance() {
        if (instance == null) {
            try {
                instance = JavaPlugin.getPlugin(CorePlugin.class);

            } catch (final IllegalStateException ex) {
                if (Bukkit.getPluginManager().getPlugin("PlugManX") != null)
                    sendConsoleMessage("Failed to get instance of the plugin, if you reloaded using PlugManX you need to do a clean restart instead.");
                throw ex;
            }
            Objects.requireNonNull(instance, "Cannot get a new instance! Have you reloaded?");
        }

        return instance;
    }

    @Override
    public final void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        onPluginStart();
        sendConsoleMessage(this.getName() + " has been enabled!");
    }

    @Override
    public final void onDisable() {
        onPluginStop();
        sendConsoleMessage(this.getName() + " has been disabled!");
    }

    public abstract void onPluginStart();

    public abstract void onPluginStop();
}
