package com.github.pinont.singularitylib.api.command;

import io.papermc.paper.command.brigadier.BasicCommand;

public interface SimpleCommand extends BasicCommand {

    String getName();

    default String usage(Boolean bool) {
        return "/" + getName();
    }

    String description();
}
