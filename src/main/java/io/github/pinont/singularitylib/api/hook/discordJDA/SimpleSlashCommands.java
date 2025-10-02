package io.github.pinont.singularitylib.api.hook.discordJDA;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Abstract base class for Discord slash commands.
 * Extends ListenerAdapter to handle Discord events and provides command structure.
 */
public abstract class SimpleSlashCommands extends ListenerAdapter {

    /**
     * Default constructor for SimpleSlashCommands.
     */
    public SimpleSlashCommands() {
    }

    /**
     * Gets the slash command component for this command.
     *
     * @return the SlashCommandComponent defining this command
     */
    public abstract SlashCommandComponent getCommand();
}
