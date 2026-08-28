package com.github.pinont.singularitylib.api.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A subcommand within a {@link CommandGroup}. Implementations provide a name,
 * optional permission, and the execution + tab-completion for their own args.
 *
 * <p>Usage (from a {@link CommandGroup}):
 * <pre>{@code
 * group.registerSubcommand(new MySubCommand());
 * }</pre>
 */
public abstract class SubCommand {

    /**
     * @return the subcommand name (first arg after the root label)
     */
    public abstract String getName();

    /**
     * @return optional permission node (e.g. "mineplugin.admin"), or {@code null}/{@code ""} for none
     */
    public String getPermission() {
        return "";
    }

    /**
     * @return short description shown in the auto-generated help menu
     */
    public String getDescription() {
        return "";
    }

    /**
     * Executes the subcommand.
     *
     * @param sender the command sender
     * @param args   the arguments AFTER the subcommand name
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Convenience overload used by {@link CommandGroup} (extracts the sender).
     */
    public void execute(CommandSourceStack source, String[] args) {
        execute(source.getSender(), args);
    }

    /**
     * Optionally restrict to players only.
     *
     * @return true if this subcommand requires a Player sender
     */
    public boolean isPlayerOnly() {
        return false;
    }

    /**
     * Checks the sender's permission for this subcommand.
     */
    protected boolean checkPermission(CommandSender sender) {
        if (getPermission() == null || getPermission().isEmpty()) {
            return true;
        }
        return sender.hasPermission(getPermission());
    }

    /**
     * Checks that the sender is allowed for this subcommand (console vs player).
     */
    protected boolean checkConsole(CommandSender sender) {
        return !isPlayerOnly() || sender instanceof Player;
    }
}