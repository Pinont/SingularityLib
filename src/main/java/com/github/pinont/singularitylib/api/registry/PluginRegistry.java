package com.github.pinont.singularitylib.api.registry;

import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.items.CustomItem;
import com.github.pinont.singularitylib.plugin.CorePlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Runtime registry of every loaded Singularity plugin and its components.
 *
 * <p>Each {@link CorePlugin} registers itself (and, when available, its component
 * lists) during {@code onEnable}. This is the discovery surface consumed by
 * Singularity-DevTool's plugin auto-discovery: enumerate plugins, then inspect a
 * plugin's commands, listeners and custom items.
 */
public final class PluginRegistry {

    private static final Map<String, CorePlugin> PLUGINS = new LinkedHashMap<>();
    private static final Map<String, RegisteredComponents> COMPONENTS = new LinkedHashMap<>();

    private PluginRegistry() {
    }

    /** Immutable snapshot of a plugin's registered components. */
    public record RegisteredComponents(
            @NotNull List<SimpleCommand> commands,
            @NotNull List<Listener> listeners,
            @NotNull List<CustomItem> customItems) {

        public static final RegisteredComponents EMPTY =
                new RegisteredComponents(List.of(), List.of(), List.of());
    }

    /**
     * Called by CorePlugin lifecycle. Internal use.
     */
    public static void register(CorePlugin plugin) {
        PLUGINS.put(plugin.getName(), plugin);
    }

    /**
     * Records a plugin's classified components (from its Register).
     */
    public static void registerComponents(CorePlugin plugin,
                                          Collection<? extends SimpleCommand> commands,
                                          Collection<? extends Listener> listeners,
                                          Collection<? extends CustomItem> items) {
        COMPONENTS.put(plugin.getName(), new RegisteredComponents(
                new ArrayList<>(commands), new ArrayList<>(listeners), new ArrayList<>(items)));
    }

    /**
     * Called by CorePlugin lifecycle. Internal use.
     */
    public static void unregister(CorePlugin plugin) {
        PLUGINS.remove(plugin.getName());
        COMPONENTS.remove(plugin.getName());
    }

    /**
     * @return all registered Singularity plugins (name -> instance), insertion-ordered
     */
    @NotNull
    public static Collection<CorePlugin> plugins() {
        return PLUGINS.values();
    }

    /**
     * Looks up a registered plugin by its plugin name.
     */
    @Nullable
    public static CorePlugin get(String name) {
        return PLUGINS.get(name);
    }

    /**
     * Looks up a registered plugin by its plugin name (optional-friendly).
     */
    @NotNull
    public static Optional<CorePlugin> find(String name) {
        return Optional.ofNullable(PLUGINS.get(name));
    }

    /**
     * Returns a plugin's registered components (empty record if none recorded).
     */
    @NotNull
    public static RegisteredComponents componentsOf(@Nullable CorePlugin plugin) {
        if (plugin == null) {
            return RegisteredComponents.EMPTY;
        }
        return COMPONENTS.getOrDefault(plugin.getName(), RegisteredComponents.EMPTY);
    }

    /**
     * @return the number of loaded Singularity plugins
     */
    public static int count() {
        return PLUGINS.size();
    }

    /**
     * Clears the registry (used between MockBukkit test runs).
     */
    public static void clear() {
        PLUGINS.clear();
        COMPONENTS.clear();
    }
}