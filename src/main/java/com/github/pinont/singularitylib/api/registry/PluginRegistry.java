package com.github.pinont.singularitylib.api.registry;

import com.github.pinont.singularitylib.plugin.CorePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Runtime registry of every loaded Singularity plugin.
 *
 * <p>Each {@link CorePlugin} registers itself here during {@code onEnable} and
 * unregisters during {@code onDisable}. This is the discovery surface consumed
 * by Singularity-DevTool's plugin auto-discovery, and available to any plugin
 * that wants to enumerate the singularity ecosystem on the server.
 */
public final class PluginRegistry {

    private static final Map<String, CorePlugin> PLUGINS = new LinkedHashMap<>();

    private PluginRegistry() {
    }

    /**
     * Called by CorePlugin lifecycle. Internal use.
     */
    public static void register(CorePlugin plugin) {
        PLUGINS.put(plugin.getName(), plugin);
    }

    /**
     * Called by CorePlugin lifecycle. Internal use.
     */
    public static void unregister(CorePlugin plugin) {
        PLUGINS.remove(plugin.getName());
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
    }
}