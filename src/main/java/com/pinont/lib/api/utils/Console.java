package com.pinont.lib.api.utils;

import org.bukkit.ChatColor;

import static com.pinont.lib.plugin.CorePlugin.sendConsoleMessage;
import static com.pinont.lib.plugin.CorePlugin.sendDebugMessage;

public class Console {
    public static void log(String message) {
        sendConsoleMessage(message);
    }

    public static void logError(String message) {
        sendConsoleMessage(ChatColor.RED + "[ERROR] " + message);
    }

    public static void debug(String message) {
        sendDebugMessage(message);
    }
}
