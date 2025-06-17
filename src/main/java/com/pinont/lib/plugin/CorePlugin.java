package com.pinont.lib.plugin;

import com.pinont.lib.api.command.SimpleCommand;
import com.pinont.lib.api.creator.EnchantmentCreator;
import com.pinont.lib.api.items.CustomItem;
import com.pinont.lib.api.manager.CommandManager;
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

    private final Boolean custom_items = false;

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

    private final List<EnchantmentCreator.Enchant> enchantments = new ArrayList<>();

    private final List<Listener> listeners = new ArrayList<>();

    private boolean custom_motd;

    private final List<CustomItem> customItems = new ArrayList<>();

    public static String getAPIVersion() {
        return "V2.1.0-BETA";
    }

    private ConfigManager pluginConfig;

    private boolean getMotdEnable() {
        if (getConfig().contains("custom_motd")) {
            return getConfig().getBoolean("custom_motd");
        } else {
            return false;
        }
    }

    private final List<String> motds = List.of(new String[]{
            "SingularityAPI for all!\nThank you for using SingularityAPI!",
            "Welcome to the server!\nMinimessage will be supported soon! <player>",
            "Enjoy your stay!\n<player>",
            "Have fun playing!\n<player>",
    });

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
        for (World world : Bukkit.getWorlds()) {
            if (world.hasMetadata("loader")) {
                new WorldManager(world.getName()).saveWorld();
            }
        }
        onPluginStop();
    }

    @Override
    public final void onEnable() {
        instance = this;
        pluginConfig = new ConfigManager("config.yml");
        if (pluginConfig.isFirstLoad()) {
            pluginConfig.set("debug", false);
            pluginConfig.saveConfig();
        }
        pluginConfig.getConfig().options().copyDefaults(true);
        custom_motd = getMotdEnable();
        WorldManager.autoLoadWorlds();
        sendConsoleMessage(ChatColor.WHITE  + "" + ChatColor.ITALIC + "Hooked " + ChatColor.YELLOW + ChatColor.ITALIC + this.getName() + ChatColor.WHITE + ChatColor.ITALIC + " into " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "SingularityAPI!");
        onPluginStart();
        registerAPIListener(this);
        new CommandManager().register(this, this.simpleCommands);
        Register register = new Register();
        register.scanAndCollect(this.getClass().getPackageName());
        register.registerAll(this);
        addAPIListener(new PlayerListener());
    }

    private void registerAPIListener(Plugin plugin) {
        sendConsoleMessage(Color.GREEN + "" + ChatColor.ITALIC + "Registering events...");
        for (Listener l : this.listeners) {
            Bukkit.getPluginManager().registerEvents(l, plugin);
            sendConsoleMessage(Color.GREEN + "" + ChatColor.ITALIC + "Registered event: " + l.getClass().getSimpleName());
        }
        this.listeners.clear();
    }

    public void registerCommand(SimpleCommand... simpleCommand) {
        this.simpleCommands.addAll(List.of(simpleCommand));
    }

    private void addAPIListener(Listener... listener) {
        this.listeners.addAll(List.of(listener));
    }

    public abstract void onPluginStart();

    public abstract void onPluginStop();
}
