package com.github.pinont.singularitylib.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

/**
 * Utility class for console logging operations.
 * Provides convenient methods for logging messages, errors, warnings, and debug information to the console.
 */
public class Console {

    /**
     * Default constructor for Console utility class.
     */
    public Console() {
    }

    /**
     * Logs a regular message to the console.
     *
     * @param message the message to log
     */
    public static void log(String message) {
        sendConsoleMessage(Component.text(message));
    }

    /**
     * Logs an error message to the console with red color formatting.
     *
     * @param message the error message to log
     */
    public static void logError(String message) {
        sendConsoleMessage(Component.text("[ERROR] " + message, NamedTextColor.RED));
    }

    /**
     * Logs a warning message to the console with yellow color formatting.
     *
     * @param message the warning message to log
     */
    public static void logWarning(String message) {
        sendConsoleMessage(Component.text("[WARNING] " + message, NamedTextColor.YELLOW));
    }

    /**
     * Logs a debug message to the console.
     *
     * @param message the debug message to log
     */
    public static void debug(String message) {
        sendDebugMessage(message);
    }
}
