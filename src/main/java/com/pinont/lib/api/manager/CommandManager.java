package com.pinont.lib.api.manager;

import com.pinont.lib.api.command.SimpleCommand;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.pinont.lib.plugin.CorePlugin.sendConsoleMessage;

public class CommandManager {

    private int success = 0;
    private int failure = 0;
    private int target_amount = 0;

    public void register(Plugin plugin, List<SimpleCommand> simpleCommands) {
        final LifecycleEventManager<@NotNull Plugin> lifecycleManager = plugin.getLifecycleManager();
        sendConsoleMessage(Color.WHITE + "Registering Commands: " + Arrays.toString(simpleCommands.stream().map(SimpleCommand::getName).toArray()));
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            for (SimpleCommand simpleCommand : simpleCommands) {
                String[] aliases = simpleCommand.getName().toLowerCase().split(":");
                sendConsoleMessage(Color.YELLOW + "Founded " + (aliases.length - 1) + " aliases: " + Arrays.toString(aliases) + " in " + aliases[0]);
                if (aliases.length > 1) {
                    for (String alias : aliases) {
                        target_amount++;
                        try {
                            event.registrar().register(alias, simpleCommand);
                            sendConsoleMessage(ChatColor.WHITE + "Registered alias: " + alias);
                            success++;
                        } catch (Exception e) {
                            failure++;
                            sendConsoleMessage(ChatColor.YELLOW + "Failed to register alias: " + alias + "\n\nERROR TRACE: \n" + e.getMessage());
                        }
                    }
                } else {
                    target_amount++;
                    try {
                        event.registrar().register(aliases[0], simpleCommand);
                        sendConsoleMessage(ChatColor.WHITE + "Registered command: " + aliases[0]);
                        success++;
                    } catch (Exception e) {
                        failure++;
                        sendConsoleMessage(ChatColor.YELLOW + "Failed to register " + aliases[0] + ": " + e.getMessage());
                    }
                }
            }
            if (failure > 0) {
                sendConsoleMessage(ChatColor.GREEN + "Command Register: successfully registered " + success + "/" + target_amount + " commands, " + ChatColor.RED + failure + " command failures");
            }
            sendConsoleMessage(ChatColor.GREEN + "Command Register: successfully registered " + success + "/" + target_amount + " commands.");
        });
    }
}
