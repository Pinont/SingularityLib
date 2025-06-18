package com.pinont.lib.plugin;

import com.pinont.lib.api.command.SimpleCommand;
import com.pinont.lib.api.manager.ConfigManager;
import com.pinont.lib.api.manager.WorldManager;
import com.pinont.lib.plugin.listener.PlayerListener;
import com.pinont.lib.plugin.register.Register;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class CorePlugin extends JavaPlugin {
    private static CorePlugin instance;

    public @NotNull FileConfiguration getConfig() {
        return new ConfigManager("config.yml").getConfig();
    }

    public @NotNull ConfigManager getConfigManager() {
        return new ConfigManager("config.yml");
    }

    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    private final List<SimpleCommand> simpleCommands = new ArrayList<>();

    private final List<Listener> listeners = new ArrayList<>();

    public static String getAPIVersion() {
        // Plugin version, can be updated as needed.
        String version = new ConfigManager("SingularityVersion.yml").getConfig().getString("version");
        return "V-" + version;
    }

    private ConfigManager pluginConfig;

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

    public static void sendDebugMessage(String message) {
        if (getInstance().getConfig().getBoolean("debug")) {
            sendConsoleMessage(ChatColor.ITALIC + "" + ChatColor.LIGHT_PURPLE + "Singularity Debugger:" + ChatColor.YELLOW + " [DEV] " + ChatColor.WHITE + message);
        }
    }

    @Override
    public final void onDisable() {
        // Save all worlds that have been loaded by the plugin.
        // TODO: Move to Devtool
        for (World world : Bukkit.getWorlds()) {
            if (world.hasMetadata("loader")) {
                new WorldManager(world.getName()).saveWorld();
            }
        }

        // Plugin Stop Process
        onPluginStop();
    }

    @Override
    public final void onEnable() {
        // Initialize the plugin instance.
        instance = this;

        // Initialize Plugin Config.
        pluginConfig = new ConfigManager("config.yml");
        if (pluginConfig.isFirstLoad()) {
            pluginConfig.set("debug", false);
            pluginConfig.saveConfig();
        }
        pluginConfig.getConfig().options().copyDefaults(true);

        // TODO: Move to Devtool
        WorldManager.autoLoadWorlds();

        // Initialize API To Plugin.
        sendConsoleMessage(ChatColor.WHITE  + "" + ChatColor.ITALIC + "Hooked " + ChatColor.YELLOW + ChatColor.ITALIC + this.getName() + ChatColor.WHITE + ChatColor.ITALIC + " into " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "SingularityAPI!");
        onPluginStart();
        registerAPIListener(this, new PlayerListener());
//        new CommandManager().register(this, this.simpleCommands);

        // Register Command, CustomItem, and Listeners.
        Register register = new Register();
        register.scanAndCollect(this.getClass().getPackageName());
        register.registerAll(this);
    }

    private void registerAPIListener(Plugin plugin, Listener... listener) {
        sendConsoleMessage(Color.GREEN + "" + ChatColor.ITALIC + "Initializing API listeners for " + plugin.getName() + "...");
        for (Listener l : listener) {
            Bukkit.getPluginManager().registerEvents(l, plugin);
        }
    }

    @Deprecated
    public void registerCommand(SimpleCommand... simpleCommand) {
        this.simpleCommands.addAll(List.of(simpleCommand));
    }

    public abstract void onPluginStart();

    public abstract void onPluginStop();
}
