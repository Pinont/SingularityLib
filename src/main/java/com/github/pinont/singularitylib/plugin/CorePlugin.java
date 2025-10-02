package com.github.pinont.singularitylib.plugin;

import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.manager.ConfigManager;
import com.github.pinont.singularitylib.plugin.listener.PlayerListener;
import com.github.pinont.singularitylib.plugin.register.Register;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for plugins using the SingularityLib framework.
 * This class provides core functionality including configuration management,
 * command registration, and plugin lifecycle management.
 */
public abstract class CorePlugin extends JavaPlugin {
    private static CorePlugin instance;

    /**
     * Default constructor for CorePlugin.
     */
    public CorePlugin() {
    }

    public @NotNull FileConfiguration getConfig() {
        return new ConfigManager("config.yml").getConfig();
    }

    /**
     * Gets the configuration manager for the main config file.
     *
     * @return the ConfigManager instance for config.yml
     */
    public @NotNull ConfigManager getConfigManager() {
        return new ConfigManager("config.yml");
    }

    private static String prefix;
    private static Long startTime;

    /**
     * Flag indicating if the plugin is running in test mode.
     */
    public boolean isTest = false;

    /**
     * Gets the time when the plugin started loading.
     *
     * @return the start time in milliseconds
     */
    public static Long getStartTime() {
        return startTime;
    }

    /**
     * Gets the plugin's console message prefix.
     *
     * @return the formatted prefix string
     */
    public static String getPrefix() {
        if (prefix == null) {
            return "[" + getInstance().getName() + "]";
        }
        if (!prefix.contains("[") || !prefix.contains("]")) {
            prefix = "[" + prefix + "]";
        }
        return prefix;
    }

    /**
     * Sends a message to the console with the plugin prefix.
     *
     * @param message the message to send
     */
    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " " + message);
    }

    private final List<SimpleCommand> simpleCommands = new ArrayList<>();

    private final List<Listener> listeners = new ArrayList<>();

    /**
     * Gets the current API version.
     *
     * @return the API version string
     */
    public static String getAPIVersion() {
        String version = new ConfigManager("api-version.yml").getConfig().getString("version");
//        String version = "1.0.0";
        return "V-" + version;
    }

    private ConfigManager pluginConfig;

    /**
     * Gets the singleton instance of the plugin.
     *
     * @return the plugin instance
     * @throws IllegalStateException if the plugin instance cannot be retrieved
     */
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

    /**
     * Sends a debug message to the console if debug mode is enabled.
     *
     * @param message the debug message to send
     */
    public static void sendDebugMessage(String message) {
        if (getInstance().getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.ITALIC + "" + ChatColor.LIGHT_PURPLE + getPrefix() + ChatColor.YELLOW + " [DEV] " + ChatColor.WHITE + message);
        }
    }

    @Override
    public final void onDisable() {
        // Save all worlds that have been loaded by the plugin.
        // TODO: Move to Devtool
//        for (World world : Bukkit.getWorlds()) {
//            if (world.hasMetadata("loader")) {
//                new WorldManager(world.getName()).saveWorld();
//            }
//        }

        // Plugin Stop Process
        onPluginStop();
    }

    @Override
    public final void onEnable() {
        startTime = System.currentTimeMillis();
        // Initialize the plugin instance.
        instance = this;
        prefix = getInstance().getPluginMeta().getLoggerPrefix();
        // Initialize Plugin Config.
        pluginConfig = new ConfigManager("config.yml");
        if (pluginConfig.isFirstLoad()) {
            pluginConfig.set("debug", false);
            pluginConfig.saveConfig();
        }
        pluginConfig.getConfig().options().copyDefaults(true);

        // TODO: Move to Devtool
//        WorldManager.autoLoadWorlds();

        // Initialize API To Plugin.
        sendConsoleMessage(ChatColor.WHITE + "" + ChatColor.ITALIC + "Hooked " + ChatColor.YELLOW + ChatColor.ITALIC + this.getName() + ChatColor.WHITE + ChatColor.ITALIC + " into " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "SingularityAPI! " + getAPIVersion());
        onPluginStart();
        registerAPIListener(this, new PlayerListener());
//        new CommandManager().register(this, this.simpleCommands);

        // Register Command, CustomItem, and Listeners.
        if (!isTest) {
            Register register = new Register();
            register.scanAndCollect(this.getClass().getPackageName());
            register.registerAll(this);
        }
    }

    private void registerAPIListener(Plugin plugin, Listener... listener) {
        sendConsoleMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "Initializing API listeners for " + plugin.getName() + "...");
        for (Listener l : listener) {
            Bukkit.getPluginManager().registerEvents(l, plugin);
        }
    }

    /**
     * Registers commands with the plugin.
     *
     * @param simpleCommand the commands to register
     * @deprecated Use the automatic registration system instead
     */
    @Deprecated
    public void registerCommand(SimpleCommand... simpleCommand) {
        this.simpleCommands.addAll(List.of(simpleCommand));
    }

    /**
     * Called when the plugin starts. Implement this method to add plugin-specific startup logic.
     */
    public abstract void onPluginStart();

    /**
     * Called when the plugin stops. Implement this method to add plugin-specific shutdown logic.
     */
    public abstract void onPluginStop();
}
