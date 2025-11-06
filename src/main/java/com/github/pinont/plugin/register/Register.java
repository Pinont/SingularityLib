package com.github.pinont.plugin.register;

import com.github.pinont.singularitylib.api.annotation.AutoRegister;
import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.items.CustomItem;
import com.github.pinont.singularitylib.api.manager.CommandManager;
import com.github.pinont.singularitylib.api.manager.CustomItemManager;
import com.github.pinont.singularitylib.api.utils.Console;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for automatically registering annotated components.
 * Scans packages for classes annotated with @AutoRegister and registers them appropriately.
 */
public class Register {

    private final Set<Listener> listeners = new HashSet<>();
    private final List<SimpleCommand> commands = new ArrayList<>();
    private final List<CustomItem> customItems = new ArrayList<>();

    /**
     * Default constructor for Register.
     */
    public Register() {
    }

    /**
     * Scans the specified package for annotated classes and collects them for registration.
     *
     * @param packageName the package name to scan
     */
    public void scanAndCollect(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AutoRegister.class);

        for (Class<?> clazz : annotated) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (Listener.class.isAssignableFrom(clazz)) {
                    listeners.add((Listener) instance);
                }
                if (SimpleCommand.class.isAssignableFrom(clazz)) {
                    commands.add((SimpleCommand) instance);
                }
                if (CustomItem.class.isAssignableFrom(clazz)) {
                    customItems.add((CustomItem) instance);
                }
            } catch (NoSuchMethodException e) {
                Console.logError(ChatColor.RED + "No default constructor found for class: " + clazz.getName());
            } catch (InstantiationException e) {
                Console.logError(ChatColor.RED + "Failed to instantiate class: " + clazz.getName());
            } catch (IllegalAccessException e) {
                Console.logError(ChatColor.RED + "Illegal access while instantiating class: " + clazz.getName());
            } catch (Exception e) {
                Console.logError(ChatColor.RED + "Unexpected error while processing class: " + clazz.getName());
                Console.logError(ChatColor.RED + e.getMessage());
            }
        }
    }

    /**
     * Registers all collected components with the specified plugin.
     *
     * @param plugin the plugin to register components with
     */
    public void registerAll(Plugin plugin) {
        // Register listeners
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
        new CommandManager().register(plugin, commands);
        // Register custom items
        for (CustomItem item : customItems) {
            if (item.getItem() != null) {
                item.register();
            }
        }
        new CustomItemManager().register(customItems);
    }

    /**
     * Gets the collected listeners.
     *
     * @return set of listeners
     */
    public Set<Listener> getListeners() {
        return listeners;
    }

    /**
     * Gets the collected commands.
     *
     * @return list of commands
     */
    public List<SimpleCommand> getCommands() {
        return commands;
    }

    /**
     * Gets the collected custom items.
     *
     * @return list of custom items
     */
    public List<CustomItem> getCustomItems() {
        return customItems;
    }
}
