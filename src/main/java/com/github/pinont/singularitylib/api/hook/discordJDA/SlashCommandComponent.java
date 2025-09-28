package com.github.pinont.singularitylib.api.hook.discordJDA;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface SlashCommandComponent {
    String name();

    String description();

    default SlashCommandData getSlashCommandData() {
        return Commands.slash(name(), description());
    }

    default CommandData getCommandData() {
        return getSlashCommandData();
    }
}

