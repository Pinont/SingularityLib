package com.pinont.lib.plugin.register;

import com.pinont.lib.api.command.SimpleCommand;
import com.pinont.lib.api.items.CustomItem;
import com.pinont.lib.api.manager.CommandManager;
import com.pinont.lib.api.manager.CustomItemManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Register {

    private final Set<Listener> listeners = new HashSet<>();
    private final List<SimpleCommand> commands = new ArrayList<>();
    private final List<CustomItem> customItems = new ArrayList<>();

    public void scanAndCollect(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AutoRegister.class);

        for (Class<?> clazz : annotated) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (Listener.class.isAssignableFrom(clazz)) {
                    listeners.add((Listener) instance);
                } else if (SimpleCommand.class.isAssignableFrom(clazz)) {
                    commands.add((SimpleCommand) instance);
                } else if (CustomItem.class.isAssignableFrom(clazz)) {
                    customItems.add((CustomItem) instance);
                }
            } catch (NoSuchMethodException e) {
                Bukkit.getLogger().severe("No default constructor found for class: " + clazz.getName());
            } catch (InstantiationException e) {
                Bukkit.getLogger().severe("Failed to instantiate class: " + clazz.getName());
            } catch (IllegalAccessException e) {
                Bukkit.getLogger().severe("Illegal access while instantiating class: " + clazz.getName());
            } catch (Exception e) {
                Bukkit.getLogger().severe("Unexpected error while processing class: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

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

    public Set<Listener> getListeners() {
        return listeners;
    }

    public List<SimpleCommand> getCommands() {
        return commands;
    }

    public List<CustomItem> getCustomItems() {
        return customItems;
    }
}
