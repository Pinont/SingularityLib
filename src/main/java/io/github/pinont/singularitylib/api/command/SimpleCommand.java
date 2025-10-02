package io.github.pinont.singularitylib.api.command;

import io.papermc.paper.command.brigadier.BasicCommand;

/**
 * Interface for creating simple commands that extend Paper's BasicCommand.
 * This interface provides a simplified way to create commands with automatic registration.
 */
public interface SimpleCommand extends BasicCommand {

    /**
     * Gets the name of the command.
     * This is used for command registration and execution.
     *
     * @return the command name
     */
    String getName();

    /**
     * Gets the usage string for this command.
     *
     * @param bool unused parameter for compatibility
     * @return the usage string for this command
     */
    default String usage(Boolean bool) {
        return "/" + getName();
    }

    /**
     * Gets the description of the command.
     * This description is used in help messages and command documentation.
     *
     * @return the command description
     */
    String description();
}
