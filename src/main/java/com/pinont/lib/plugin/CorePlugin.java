package com.pinont.lib.plugin;

import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.manager.ConfigManager;
import com.pinont.lib.api.manager.WorldManager;
import com.pinont.lib.plugin.events.PlayerListener;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    private static ConfigManager configManager;

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    private final List<SimpleCommand> simpleCommands = new ArrayList<>();

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
        instance = this;
        configManager = new ConfigManager("config.yml");
        FileConfiguration config = getConfigManager().getConfig();
        registerEvents(this, new PlayerListener());
        ConfigManager configManager = getConfigManager();
        if (configManager.isFirstLoad()) {
            config.set("debug", false);
            config.set("dev-tool", false);
            configManager.saveConfig();
            config.options().copyDefaults(true);
            return;
        }
        config.options().copyDefaults(true);
        WorldManager.autoLoadWorlds();
        sendConsoleMessage(ChatColor.WHITE  + "" + ChatColor.ITALIC + "Hooked " + ChatColor.YELLOW + ChatColor.ITALIC + this.getName() + ChatColor.WHITE + ChatColor.ITALIC + " into " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "SingularityAPI!");
        onPluginStart();
        final LifecycleEventManager<@NotNull Plugin> lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            for (SimpleCommand simpleCommand : simpleCommands) {
                event.registrar().register(simpleCommand.getName(), simpleCommand);
            }
            if (config.getBoolean("dev-tool")) {
                DevTool devTool = new DevTool();
                event.registrar().register(devTool.getName(), devTool);
            }
        });
        new ItemCreator(Material.STICK).addInteraction(new DevTool().devToolItemInteraction).create(); // register devTool interaction with dummy item
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

    public void registerEvents(Plugin plugin, Listener... listener) {
        for (Listener l : listener) {
            Bukkit.getPluginManager().registerEvents(l, plugin);
        }
    }

    public void registerCommand(SimpleCommand simpleCommand) {
        simpleCommands.add(simpleCommand);
    }

    public static void sendDebugMessage(String message) {
        if (getConfigManager().getConfig().getBoolean("debug")) {
            sendConsoleMessage(ChatColor.ITALIC + "" + ChatColor.LIGHT_PURPLE + "Singularity Debugger:" + ChatColor.YELLOW + " [DEV] " + ChatColor.WHITE + message);
        }
    }

    public abstract void onPluginStart();

    public abstract void onPluginStop();
}
