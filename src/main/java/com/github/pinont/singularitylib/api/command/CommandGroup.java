package com.github.pinont.singularitylib.api.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A command that groups {@link SubCommand}s under one root label, with an
 * auto-generated help menu when invoked with no arguments.
 *
 * <p>Consumers create a subclass, register subcommands in the constructor, and
 * register the group via {@code CommandManager} (or {@code registerComponents()}).
 *
 * <pre>{@code
 * CommandGroup arena = new CommandGroup() {
 *     {
 *         registerSubcommand(new ArenaCreateSub());
 *         registerSubcommand(new ArenaDeleteSub());
 *     }
 * };
 * }</pre>
 */
public abstract class CommandGroup implements SimpleCommand {

    private final Map<String, SubCommand> subcommands = new LinkedHashMap<>();

    /**
     * Registers a subcommand (also registers its aliases via {@code name:alias}).
     */
    public final void registerSubcommand(SubCommand sub) {
        String[] nameAndAliases = sub.getName().toLowerCase().split(":");
        for (String n : nameAndAliases) {
            if (!subcommands.containsKey(n)) {
                subcommands.put(n, sub);
            }
        }
    }

    /**
     * @return immutable list of registered subcommand names
     */
    public List<String> getSubcommandNames() {
        return new ArrayList<>(subcommands.keySet());
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        execute(source.getSender(), args);
    }

    /**
     * Dispatches to subcommands using a plain CommandSender (console-friendly and
     * unit-testable without a CommandSourceStack).
     */
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }
        SubCommand sub = subcommands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(Component.text(getUsageHint(args[0]), NamedTextColor.RED));
            return;
        }
        if (!sub.checkConsole(sender)) {
            sender.sendMessage(Component.text("This subcommand is players-only.", NamedTextColor.RED));
            return;
        }
        if (!sub.checkPermission(sender)) {
            sender.sendMessage(Component.text("You do not have permission to use: /" + getName() + " " + sub.getName(), NamedTextColor.RED));
            return;
        }
        // strip the subcommand name and pass remaining args
        String[] rest = new String[Math.max(0, args.length - 1)];
        System.arraycopy(args, 1, rest, 0, rest.length);
        sub.execute(sender, rest);
    }

    /**
     * @return a red "unknown subcommand" hint
     */
    protected String getUsageHint(String unknown) {
        return "Unknown subcommand: " + unknown + ". Use /" + getName() + " help";
    }

    /**
     * Sends the paginated help listing to the sender.
     */
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("—— " + getName() + " help ——", NamedTextColor.GOLD));
        for (SubCommand sub : subcommands.values()) {
            String line = "/" + getName() + " " + sub.getName() + (sub.getDescription().isEmpty() ? "" : " — " + sub.getDescription());
            sender.sendMessage(Component.text(line, NamedTextColor.YELLOW));
        }
    }

    @Override
    public String usage(Boolean bool) {
        return "/" + getName() + " <subcommand>";
    }
}