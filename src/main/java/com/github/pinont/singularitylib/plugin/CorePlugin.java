package com.github.pinont.singularitylib.plugin;

import com.github.pinont.singularitylib.api.utils.Common;
import com.github.pinont.singularitylib.plugin.listener.PlayerListener;
import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.manager.ConfigManager;
import com.github.pinont.singularitylib.api.registry.PluginRegistry;
import com.github.pinont.singularitylib.plugin.register.Register;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
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
        // Return the cached config (created once in onEnable) rather than building
        // a fresh ConfigManager per call — behavior fix from the v1 audit.
        return pluginConfig == null ? new ConfigManager(this, "config.yml").getConfig()
                : pluginConfig.getConfig();
    }

    /**
     * Gets the configuration manager for the main config file.
     *
     * @return the ConfigManager instance for config.yml
     */
    public @NotNull ConfigManager getConfigManager() {
        return new ConfigManager(this, "config.yml");
    }

    private static volatile String prefix;
    private static volatile Long startTime;
    private static volatile boolean isFolia;

    /**
     * Flag indicating if the plugin is running in test mode.
     */
    public boolean isTest = false;

    public static boolean isFolia() {
        return isFolia;
    }

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
        if (prefix != null) {
            return prefix;
        }
        // Safe publication: compute once and publish to the volatile — never mutate
        // a shared reference lazily (avoids a benign-but-real read race on two threads).
        final String raw = prefix;
        final String formatted = raw == null
                ? "[" + getInstance().getName() + "]"
                : raw.contains("[") && raw.contains("]") ? raw : "[" + raw + "]";
        prefix = formatted;
        return formatted;
    }

    /**
     * Sends a message to the console with the plugin prefix.
     *
     * @param component the message component to send
     */
    public static void sendConsoleMessage(Component component) {
        Bukkit.getConsoleSender().sendMessage(Component.text(getPrefix() + " ").append(component));
    }

    /**
     * Sends a message to the console with the plugin prefix.
     *
     * @param message the message to send
     */
    public static void sendConsoleMessage(String message) {
        sendConsoleMessage(Component.text(message));
    }

    private final List<SimpleCommand> simpleCommands = new ArrayList<>();

    private final List<Listener> listeners = new ArrayList<>();

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
        sendDebugMessage(Component.text(message, NamedTextColor.WHITE, TextDecoration.ITALIC));
    }

    /**
     * Sends a debug message to the console if debug mode is enabled.
     * The prefix, DEV tag, and the message are rendered italic (matching the
     * legacy colour-code behaviour where the trailing ITALIC code never reset);
     * the message colour is whatever the supplied component carries.
     *
     * @param component the debug message component to send
     */
    public static void sendDebugMessage(Component component) {
        if (getInstance().getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage(
                    Component.text(getPrefix(), NamedTextColor.LIGHT_PURPLE, TextDecoration.ITALIC)
                            .append(Component.text(" [DEV] ", NamedTextColor.YELLOW, TextDecoration.ITALIC))
                            .append(component.decorate(TextDecoration.ITALIC))
            );
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
        PluginRegistry.unregister(this);
        onPluginStop();
    }

    @Override
    public final void onEnable() {
        startTime = System.currentTimeMillis();
        // Initialize the plugin instance.
        instance = this;
        PluginRegistry.register(this);
        prefix = getInstance().getPluginMeta().getLoggerPrefix();
        // Initialize Plugin Config.
        pluginConfig = new ConfigManager(this, "config.yml");
        if (pluginConfig.isFirstLoad()) {
            pluginConfig.set("debug", false);
            pluginConfig.saveConfig();
        }
        pluginConfig.getConfig().options().copyDefaults(true);

        // TODO: Move to Devtool
//        WorldManager.autoLoadWorlds();

        isFolia = foliaCheck();
        if (isFolia) {
            sendConsoleMessage(Component.text("Folia environment detected, enabling Folia compatibility mode...", NamedTextColor.GREEN, TextDecoration.ITALIC));
        }

        // Initialize API To Plugin.
        sendConsoleMessage(
                Component.text("Hooked ", NamedTextColor.WHITE, TextDecoration.ITALIC)
                        .append(Component.text(this.getName(), NamedTextColor.YELLOW, TextDecoration.ITALIC))
                        .append(Component.text(" into ", NamedTextColor.WHITE, TextDecoration.ITALIC))
                        .append(Component.text("SingularityAPI! ", NamedTextColor.LIGHT_PURPLE, TextDecoration.ITALIC))
                        .append(Component.text(Common.getAPIVersion(), NamedTextColor.LIGHT_PURPLE, TextDecoration.ITALIC))
        );
        onPluginStart();
        registerAPIListener(this, new PlayerListener());
//        new CommandManager().register(this, this.simpleCommands);

        // Register Command, CustomItem, and Listeners.
        if (!isTest) {
            if (!explicitRegistration) {
                Register register = new Register();
                // Primary: compile-time @AutoRegister index (fast, no classpath scan).
                // Fallback: legacy Reflections scan when no index resource exists.
                if (!register.loadFromIndex()) {
                    register.scanAndCollect(this.getClass().getPackageName());
                }
                register.registerAll(this);
                exposeComponents(register);
            }
        }
    }

    /**
     * Publishes this plugin's registered components into {@link PluginRegistry}
     * so DevTool-style tooling can inspect them per-plugin.
     */
    private void exposeComponents(Register register) {
        PluginRegistry.registerComponents(this,
                register.getCommands(),
                register.getListeners(),
                register.getCustomItems());
    }

    /**
     * Set when the consumer opts into explicit registration via
     * {@link #registerComponents(Object...)} / {@link #registerComponentClasses(Class...)}.
     * Suppresses the legacy {@code @AutoRegister} classpath scan to avoid double-registration.
     */
    private boolean explicitRegistration;

    /**
     * Registers component instances directly (listeners, commands, custom items) with
     * this plugin — the fast, explicit path (no classpath scanning). Call from
     * {@link #onPluginStart()}:
     * <pre>{@code
     * registerComponents(new MyListener(), new MyCommand(), new MyItem());
     * }</pre>
     *
     * @param components the component instances to register
     */
    protected final void registerComponents(Object... components) {
        explicitRegistration = true;
        Register register = new Register();
        register.register(components);
        register.registerAll(this);
        exposeComponents(register);
    }

    /**
     * Registers component classes (instantiated reflectively via no-arg ctor) with this
     * plugin. Preferred over old {@code @AutoRegister} scanning when you want class-level
     * registration without classpath scanning:
     * <pre>{@code
     * registerComponentClasses(MyListener.class, MyCommand.class);
     * }</pre>
     *
     * @param classes the component classes to register
     */
    protected final void registerComponentClasses(Class<?>... classes) {
        explicitRegistration = true;
        Register register = new Register();
        register.register(classes);
        register.registerAll(this);
        exposeComponents(register);
    }

    private boolean foliaCheck() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void registerAPIListener(Plugin plugin, Listener... listener) {
        sendConsoleMessage(Component.text("Initializing API listeners for " + plugin.getName() + "...", NamedTextColor.GREEN, TextDecoration.ITALIC));
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
