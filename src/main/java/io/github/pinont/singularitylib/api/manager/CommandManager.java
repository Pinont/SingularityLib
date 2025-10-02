package io.github.pinont.singularitylib.api.manager;

import io.github.pinont.singularitylib.api.command.SimpleCommand;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static io.github.pinont.singularitylib.plugin.CorePlugin.getStartTime;
import static io.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

/**
 * Manages the registration of commands for the plugin.
 * This class handles the registration of SimpleCommand instances and their aliases
 * during the plugin's lifecycle events.
 */
public class CommandManager {

    private int success = 0;
    private int failure = 0;
    private int target_amount = 0;

    /**
     * Default constructor for CommandManager.
     */
    public CommandManager() {
    }

    /**
     * Registers a list of SimpleCommand instances with the plugin.
     * This method registers commands and their aliases during the COMMANDS lifecycle event.
     * It tracks registration success and failure counts and provides console feedback.
     *
     * @param plugin the plugin instance to register commands for
     * @param simpleCommands the list of SimpleCommand instances to register
     */
    public void register(Plugin plugin, List<SimpleCommand> simpleCommands) {
        final LifecycleEventManager<@NotNull Plugin> lifecycleManager = plugin.getLifecycleManager();
        sendConsoleMessage(ChatColor.WHITE + "Registering Commands: " + Arrays.toString(simpleCommands.stream().map(SimpleCommand::getName).toArray()));
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            for (SimpleCommand simpleCommand : simpleCommands) {
                String[] aliases = simpleCommand.getName().toLowerCase().split(":");
                sendConsoleMessage(ChatColor.WHITE + "Founded " + ChatColor.YELLOW + (aliases.length - 1) + ChatColor.WHITE + " aliases in " + ChatColor.YELLOW + aliases[0]);
                target_amount++;
                try {
                    event.registrar().register(aliases[0], simpleCommand);
                    sendConsoleMessage(ChatColor.WHITE + "Registered command: " + aliases[0]);
                    success++;
                } catch (Exception e) {
                    failure++;
                    sendConsoleMessage(ChatColor.YELLOW + "Failed to register command: " + aliases[0] + "\n\nERROR TRACE: \n" + e.getMessage());
                }
                if (aliases.length > 1) {
                    for (int i = 1; i < aliases.length; i++) {
                        target_amount++;
                        try {
                            event.registrar().register(aliases[i], simpleCommand);
                            sendConsoleMessage(ChatColor.WHITE + "Registered alias: " + aliases[i]);
                            success++;
                        } catch (Exception e) {
                            failure++;
                            sendConsoleMessage(ChatColor.YELLOW + "Failed to register alias: " + aliases[i] + "\n\nERROR TRACE: \n" + e.getMessage());
                        }
                    }
                }
            }
            if (failure > 0) {
                sendConsoleMessage(ChatColor.GREEN + "Command Register: successfully registered " + success + "/" + target_amount + " commands, " + ChatColor.RED + failure + " command failures");
            }
            sendConsoleMessage(ChatColor.GREEN + "Command Register: successfully registered " + success + "/" + target_amount + " commands.");
            Long startTime = System.currentTimeMillis() - getStartTime();
            sendConsoleMessage(ChatColor.WHITE + "Plugin is Enabled (took " + ChatColor.YELLOW + startTime + ChatColor.WHITE + " ms)");
        });
    }
}
