package com.pinont.lib.api.manager;

import com.pinont.lib.api.command.SimpleCommand;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.ChatColor;
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
        });
    }
}
