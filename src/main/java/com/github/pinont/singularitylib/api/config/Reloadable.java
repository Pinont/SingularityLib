package com.github.pinont.singularitylib.api.config;

/**
 * Contract for components that support live (hot) reload of configuration.
 *
 * <p>Plugins implement this on any object that should react when its config file
 * changes at runtime — e.g. re-reading values, rebuilding caches, re-registering
 * listeners. The DevTool's live config editor calls {@link #reload()} after saving
 * edits so changes apply without a server restart.
 */
public interface Reloadable {

    /**
     * Reloads configuration state from disk / manager.
     * Must be safe to call repeatedly and from the main thread (or region thread).
     */
    void reload();
}