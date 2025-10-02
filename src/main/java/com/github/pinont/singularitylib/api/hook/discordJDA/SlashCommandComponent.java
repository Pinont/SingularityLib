package com.github.pinont.singularitylib.api.hook.discordJDA;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Interface for defining Discord slash command components.
 * Provides the structure and data needed to create Discord slash commands.
 */
public interface SlashCommandComponent {

    /**
     * Gets the name of the slash command.
     *
     * @return the command name
     */
    String name();

    /**
     * Gets the description of the slash command.
     *
     * @return the command description
     */
    String description();

    /**
     * Gets the SlashCommandData for this command.
     * Default implementation creates a basic slash command.
     *
     * @return the SlashCommandData
     */
    default SlashCommandData getSlashCommandData() {
        return Commands.slash(name(), description());
    }

    /**
     * Gets the CommandData for this command.
     * Default implementation returns the SlashCommandData.
     *
     * @return the CommandData
     */
    default CommandData getCommandData() {
        return getSlashCommandData();
    }
}
