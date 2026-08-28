package com.github.pinont.singularitylib.api.manager;

import com.github.pinont.singularitylib.api.command.SimpleCommand;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.github.pinont.singularitylib.plugin.CorePlugin.getStartTime;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

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
        sendConsoleMessage(Component.text("Registering Commands: " + Arrays.toString(simpleCommands.stream().map(SimpleCommand::getName).toArray()), NamedTextColor.WHITE));
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            for (SimpleCommand simpleCommand : simpleCommands) {
                String[] aliases = simpleCommand.getName().toLowerCase().split(":");
                sendConsoleMessage(Component.text("Founded ", NamedTextColor.WHITE).append(Component.text(aliases.length - 1, NamedTextColor.YELLOW)).append(Component.text(" aliases in ", NamedTextColor.WHITE)).append(Component.text(aliases[0], NamedTextColor.YELLOW)));
                target_amount++;
                try {
                    event.registrar().register(aliases[0], simpleCommand);
                    sendConsoleMessage(Component.text("Registered command: " + aliases[0], NamedTextColor.WHITE));
                    success++;
                } catch (Exception e) {
                    failure++;
                    sendConsoleMessage(Component.text("Failed to register command: " + aliases[0] + "\n\nERROR TRACE: \n" + e.getMessage(), NamedTextColor.YELLOW));
                }
                if (aliases.length > 1) {
                    for (int i = 1; i < aliases.length; i++) {
                        target_amount++;
                        try {
                            event.registrar().register(aliases[i], simpleCommand);
                            sendConsoleMessage(Component.text("Registered alias: " + aliases[i], NamedTextColor.WHITE));
                            success++;
                        } catch (Exception e) {
                            failure++;
                            sendConsoleMessage(Component.text("Failed to register alias: " + aliases[i] + "\n\nERROR TRACE: \n" + e.getMessage(), NamedTextColor.YELLOW));
                        }
                    }
                }
            }
            if (failure > 0) {
                sendConsoleMessage(Component.text("Command Register: successfully registered " + success + "/" + target_amount + " commands, ", NamedTextColor.GREEN).append(Component.text(failure + " command failures", NamedTextColor.RED)));
            }
            sendConsoleMessage(Component.text("Command Register: successfully registered " + success + "/" + target_amount + " commands.", NamedTextColor.GREEN));
            Long startTime = System.currentTimeMillis() - getStartTime();
            sendConsoleMessage(Component.text("Plugin is Enabled (took ", NamedTextColor.WHITE).append(Component.text(startTime, NamedTextColor.YELLOW)).append(Component.text(" ms)", NamedTextColor.WHITE)));
        });
    }
}
