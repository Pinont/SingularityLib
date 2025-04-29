package com.pinont.lib.plugin;

import com.pinont.lib.Singularity;
import com.pinont.lib.api.manager.ConfigManager;
import com.pinont.lib.api.ui.Button;
import com.pinont.lib.plugin.events.PlayerListener;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class CorePlugin extends JavaPlugin {
    private static JavaPlugin plugin;
    private static CorePlugin instance;

    private static List<String> blockedCommands = new ArrayList<>();
    private static Boolean obfuscatedCommand = false;

    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static JavaPlugin getInstance() {
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
        onPluginStart();
        if (this.getName().equalsIgnoreCase("SingularityLib")) {
            sendConsoleMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "SingularityAPI" + ChatColor.WHITE + ChatColor.ITALIC + " has been loaded!");
        } else {
            sendConsoleMessage(ChatColor.WHITE  + "" + ChatColor.ITALIC + "Hooked " + ChatColor.YELLOW + ChatColor.ITALIC + this.getName() + ChatColor.WHITE + ChatColor.ITALIC + " into " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "Singularity!");
        }
    }

    @Override
    public final void onDisable() {
        onPluginStop();
    }

    public void registerEvents(Plugin plugin, Listener... listener) {
        for (Listener l : listener) {
            Bukkit.getPluginManager().registerEvents(l, plugin);
        }
    }

    public static void sendDebugMessage(String message) {
        if (Singularity.getConfigManager().getConfig().getBoolean("debug")) {
            sendConsoleMessage(ChatColor.ITALIC + "" + ChatColor.YELLOW + "SingularDebugger: [DEV] " + message);
        }
    }

    public abstract void onPluginStart();

    public abstract void onPluginStop();
}
