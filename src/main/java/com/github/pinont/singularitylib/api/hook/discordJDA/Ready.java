package com.github.pinont.singularitylib.api.hook.discordJDA;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Discord bot ready event listener.
 * Handles the initialization of Discord bot commands when the bot becomes ready.
 */
public class Ready extends ListenerAdapter {

    DiscordApp app;

    /**
     * Creates a new Ready listener for the specified Discord app.
     *
     * @param app the Discord application instance
     */
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
