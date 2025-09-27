package io.github.pinont.lib.api.hook.discordJDA;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class SimpleSlashCommands extends ListenerAdapter {
    public abstract SlashCommandComponent getCommand();
}
