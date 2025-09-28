package com.github.pinont.singularitylib.api.utils;

import org.bukkit.ChatColor;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

public class Console {
    public static void log(String message) {
        sendConsoleMessage(message);
    }

    public static void logError(String message) {
        sendConsoleMessage(ChatColor.RED + "[ERROR] " + message);
    }

    public static void logWarning(String message) {
        sendConsoleMessage(ChatColor.YELLOW + "[WARNING] " + message);
    }

    public static void debug(String message) {
        sendDebugMessage(message);
    }
}
