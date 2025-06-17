package com.pinont.lib.plugin;

import com.pinont.lib.api.creator.EnchantmentCreator;
import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.custom.CustomItem;
import com.pinont.lib.api.custom.CustomItemManager;
import com.pinont.lib.api.manager.ConfigManager;
import com.pinont.lib.api.manager.WorldManager;
import com.pinont.lib.plugin.essentialCommand.FlySpeed;
import com.pinont.lib.plugin.events.PlayerListener;
import com.pinont.lib.plugin.events.ServerListPingListener;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class CorePlugin extends JavaPlugin {
    private static CorePlugin instance;

    private boolean custom_enchants = false;

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

    private boolean custom_items = false;

    private boolean custom_motd;

    private final List<CustomItem> customItems = new ArrayList<>();

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

    private void reload() {
        registerEvents(new PlayerListener());
        if (pluginConfig.isFirstLoad()) {
            pluginConfig.set("debug", false);
            pluginConfig.set("dev-tool", false);
            pluginConfig.saveConfig();
        }
        pluginConfig.getConfig().options().copyDefaults(true);
        configureServerMotd();
        onReload();
    }

    @Override
    public final void onEnable() {
        instance = this;
        pluginConfig = new ConfigManager("config.yml");
        registerEvents(new PlayerListener());
        if (pluginConfig.isFirstLoad()) {
            pluginConfig.set("debug", false);
            pluginConfig.set("dev-tool", false);
            pluginConfig.saveConfig();
        }
        pluginConfig.getConfig().options().copyDefaults(true);
        custom_motd = getMotdEnable();
        WorldManager.autoLoadWorlds();
        sendConsoleMessage(ChatColor.WHITE  + "" + ChatColor.ITALIC + "Hooked " + ChatColor.YELLOW + ChatColor.ITALIC + this.getName() + ChatColor.WHITE + ChatColor.ITALIC + " into " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "SingularityAPI!");
//        registerCustomItem(new DevTool());
        onPluginStart();
        reload();
        if (custom_motd) {
            registerEvents(new ServerListPingListener());
        }
        registerCommand(this);
        registerEvents(this);
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

    private void registerEvents(Plugin plugin) {
        for (Listener l : this.listeners) {
            Bukkit.getPluginManager().registerEvents(l, plugin);
        }
        this.listeners.clear();
    }

    public void registerEvents(Listener... listener) {
        this.listeners.addAll(List.of(listener));
    }

    private void registerCommand(Plugin plugin) {
        final LifecycleEventManager<@NotNull Plugin> lifecycleManager = plugin.getLifecycleManager();
        if (custom_enchants) registerCommand(new EnchantmentCreator());
        if (custom_items) registerCommand(new CustomItemManager().register(customItems));
        sendConsoleMessage(Color.WHITE + "Registering Commands: " + Arrays.toString(simpleCommands.stream().map(SimpleCommand::getName).toArray()));
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            for (SimpleCommand simpleCommand : simpleCommands) {
                sendConsoleMessage(Color.WHITE + "Registered command + " + simpleCommand.getName());
                String[] aliases = simpleCommand.getName().split(":");
                if (aliases.length > 1) {
                    for (String alias : aliases) {
                        event.registrar().register(alias, simpleCommand);
                    }
                } else {
                    event.registrar().register(simpleCommand.getName(), simpleCommand);
                }
            }
        });
        this.simpleCommands.clear();
    }

    public void registerCustomItem(CustomItem... customItem) {
        custom_items = true;
        customItems.addAll(List.of(customItem));
        for (CustomItem item : customItems) {
            if (item.getItem() != null) {
                item.register();
            }
        }
    }

    public void registerCommand(SimpleCommand... simpleCommand) {
        this.simpleCommands.addAll(List.of(simpleCommand));
    }

    private void configureServerMotd() {
        ConfigManager motdConfig = getConfigManager();
//        if (!motdConfig.getConfig().contains("custom-motd")) {
//            URL defaultImageURL = getClass().getResource("/SingularityAPI.png");
//            Image defaultImage = new ImageIcon(defaultImageURL != null ? defaultImageURL : null).getImage();
//            BufferedImage bufferedImage = defaultImage;
//            motdConfig.set("custom_motd", true);
//        }
        if (!motdConfig.getConfig().contains("motds")) {
            motdConfig.set("motds", motds);
        }
        motdConfig.saveConfig();
        motdConfig.getConfig().options().copyDefaults(true);
    }

    public void customMotd(boolean enable) {
        custom_motd = enable;
        getConfig().set("custom_motd", enable);
        if (!getConfig().contains("motds")) {
            getConfig().set("motds", motds);
        }
        getConfigManager().saveConfig();
    }

    private void registerEnchantment(EnchantmentCreator.Enchant... enchant) {
        custom_enchants = true;
        enchantments.addAll(List.of(enchant));
        new EnchantmentCreator().getEnchants().addAll(enchantments);
    }

    public static void sendDebugMessage(String message) {
        if (CorePlugin.getInstance().getConfig().getBoolean("debug")) {
            sendConsoleMessage(ChatColor.ITALIC + "" + ChatColor.LIGHT_PURPLE + "Singularity Debugger:" + ChatColor.YELLOW + " [DEV] " + ChatColor.WHITE + message);
        }
    }

    // this method is not working for me, will use it later when it is fixed
//    @Override
//    public void bootstrap(@NotNull BootstrapContext context) {
//        sendConsoleMessage(ChatColor.WHITE + "----------------Bootstrap started----------------");
//        // Register a new handler for the freeze lifecycle event on the enchantment registry
//        if (custom_enchants)
//            context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
//                    for (EnchantmentCreator.Enchant enchantment : enchantments) {
//                        event.registry().register(
//                                // The key of the registry
//                                // Plugins should use their own namespace instead of minecraft or papermc
//                                EnchantmentKeys.create(Key.key(enchantment.getNamespace() + ":" + enchantment.getName())),
//                                b -> b.description(Component.text(enchantment.getDescription()))
//                                        .supportedItems(event.getOrCreateTag(enchantment.getSupportItem()))
//                                        .anvilCost(enchantment.getAnvilCost())
//                                        .maxLevel(enchantment.getMaxLevel())
//                                        .weight(enchantment.getFoundRate())
//                                        .minimumCost(enchantment.getMinimumCost())
//                                        .maximumCost(enchantment.getMaximumCost())
//                                        .activeSlots(enchantment.getActiveSlotGroup())
//                        );
//                        sendDebugMessage("Registered enchantment " + enchantment.getName() + " with key " + enchantment.getNamespace() + ":" + enchantment.getName());
//                    }
//            }));
//    }

    public abstract void onReload();

    public abstract void onPluginStart();

    public abstract void onPluginStop();
}
