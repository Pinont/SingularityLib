package com.pinont.lib.plugin;

import com.pinont.lib.plugin.events.PlayerListener;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
        final LifecycleEventManager<@NotNull Plugin> lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            // Handler for the event
            event.registrar().register("ench", new BasicCommand() {
                @Override
                public void execute(CommandSourceStack commandSourceStack, String[] strings) {
                    commandSourceStack.getSender().sendMessage("Hello world!");
                }
            });
        });
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
