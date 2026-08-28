package com.github.pinont.singularitylib.plugin.register;

import com.github.pinont.singularitylib.api.annotation.AutoRegister;
import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.items.CustomItem;
import com.github.pinont.singularitylib.api.manager.CommandManager;
import com.github.pinont.singularitylib.api.manager.CustomItemManager;
import com.github.pinont.singularitylib.api.utils.Console;
import org.bukkit.Bukkit;
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
     * Explicitly registers annotated component CLASSES (no classpath scanning).
     * Each class is instantiated via its no-arg constructor and classified into
     * listeners / commands / custom items — the fast, reflection-free path.
     *
     * @param classes the component classes to register (must have a no-arg constructor)
     */
    public void register(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                collect(instance);
            } catch (NoSuchMethodException e) {
                Console.logError("No default constructor found for class: " + clazz.getName());
            } catch (InstantiationException e) {
                Console.logError("Failed to instantiate class: " + clazz.getName());
            } catch (IllegalAccessException e) {
                Console.logError("Illegal access while instantiating class: " + clazz.getName());
            } catch (Exception e) {
                Console.logError("Unexpected error while processing class: " + clazz.getName());
                Console.logError(e.getMessage());
            }
        }
    }

    /**
     * Explicitly registers component INSTANCES (already constructed by the caller) —
     * the fastest path, with no reflection at all.
     *
     * @param instances the component instances to register
     */
    public void register(Object... instances) {
        for (Object instance : instances) {
            collect(instance);
        }
    }

    /**
     * Classifies a single instance into the appropriate collection.
     */
    private void collect(Object instance) {
        if (instance instanceof Listener) {
            listeners.add((Listener) instance);
        }
        if (instance instanceof SimpleCommand) {
            commands.add((SimpleCommand) instance);
        }
        if (instance instanceof CustomItem) {
            customItems.add((CustomItem) instance);
        }
    }

    /**
     * Scans the specified package for annotated classes and collects them for registration.
     *
     * @param packageName the package name to scan
     * @deprecated prefer {@link #register(Class[])} / {@link #register(Object[])} —
     *             the Reflections classpath scan is slow and fragile; explicit registration
     *             is the supported path since v2.
     */
    @Deprecated
    public void scanAndCollect(String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            // Nothing to scan (e.g. under MockBukkit tests where the plugin's
            // package may resolve empty). Explicitly supported no-op.
            return;
        }
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AutoRegister.class);

        for (Class<?> clazz : annotated) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                collect(instance);
            } catch (NoSuchMethodException e) {
                Console.logError("No default constructor found for class: " + clazz.getName());
            } catch (InstantiationException e) {
                Console.logError("Failed to instantiate class: " + clazz.getName());
            } catch (IllegalAccessException e) {
                Console.logError("Illegal access while instantiating class: " + clazz.getName());
            } catch (Exception e) {
                Console.logError("Unexpected error while processing class: " + clazz.getName());
                Console.logError(e.getMessage());
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
