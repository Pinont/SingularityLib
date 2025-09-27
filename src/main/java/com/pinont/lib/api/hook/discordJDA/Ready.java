package com.pinont.lib.api.hook.discordJDA;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Ready extends ListenerAdapter {

    DiscordApp app;

    public Ready(DiscordApp app) {
        this.app = app;
    }

    @Override
    public final void onReady(@NotNull ReadyEvent event) {
        app.onAppReady(event);
        for (Guild guild : event.getJDA().getGuilds()) {
            guild.updateCommands().addCommands(
                    app.slashCommands.stream()
                            .map(simpleSlashCommands -> simpleSlashCommands.getCommand().getCommandData())
                            .toList()
            ).queue();
        }
    }

}
