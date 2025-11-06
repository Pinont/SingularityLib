package com.github.pinont.singularitylib.api.hook.discordJDA;

import com.github.pinont.singularitylib.api.manager.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;

import static com.github.pinont.plugin.CorePlugin.sendConsoleMessage;

/**
 * Abstract base class for Discord bot applications using JDA (Java Discord API).
 * Provides a framework for creating Discord bots with slash command support and configuration management.
 */
public abstract class DiscordApp {

    /**
     * List of registered slash commands for the Discord bot.
     */
    public final ArrayList<SimpleSlashCommands> slashCommands = new ArrayList<>();
    private final boolean multiThread;
    private final ConfigManager configManager;
    private final String configPath;
    private Thread jdaThread;
    private JDA jda;

    /**
     * Creates a new DiscordApp with single-threaded execution.
     *
     * @param configPath the path to the bot configuration file
     */
    public DiscordApp(String configPath) {
        this(configPath, false);
    }

    /**
     * Creates a new DiscordApp with specified threading configuration.
     *
     * @param configPath the path to the bot configuration file
     * @param multiThread whether to run the bot in a separate thread
     */
    public DiscordApp(String configPath, boolean multiThread) {
        this.configPath = configPath;
        configManager = new ConfigManager(configPath);
        this.multiThread = multiThread;
    }

    /**
     * Reloads the bot configuration and restarts the bot.
     */
    public void reloadConfig() {
        shutdown();
        start();
    }

    /**
     * Starts the Discord bot with the configured settings.
     * If multi-threading is enabled, the bot will run in a separate thread.
     */
    public final void start() {
        String token = getToken();
        if (multiThread) {
            try {
                jdaThread = new Thread(() -> {
                    bot(token);
                }, "JDA-Thread");
                jdaThread.start();
            } catch (Exception e) {
                sendConsoleMessage(ChatColor.RED + "Make sure you have correct bot token in " + configPath + "!");
                throw e;
            }
        } else {
            bot(token);
        }
    }

    private String getToken() {
        if (configManager.getConfig().getString("bot_token") == null) {
            configManager.set("bot_token", "BOT_TOKEN_HERE");
            configManager.saveConfig();
            sendConsoleMessage(ChatColor.RED + "Please set the bot_token in " + configPath + ".");
            return "BOT_TOKEN_HERE";
        }
        return configManager.getConfig().getString("bot_token");
    }

    private void bot(String token) {
        onStartup();
        JDABuilder bot = JDABuilder.createDefault(token);
        for (SimpleSlashCommands command : slashCommands) {
            bot.addEventListeners(new Ready(this), command);
        }
        jda = bot.build();
    }

    /**
     * Shuts down the Discord bot and cleans up resources.
     * If running in multi-threaded mode, waits for the thread to complete.
     */
    public final void shutdown() {
        onShutdown();
        if (multiThread) {
            if (jda != null) {
                jda.shutdown();
            }
            if (jdaThread != null && jdaThread.isAlive()) {
                try {
                    jdaThread.join(5000); // Wait up to 5 seconds for thread to finish
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Adds slash commands to the bot.
     *
     * @param commands the slash commands to add
     */
    public void addCommands(SimpleSlashCommands... commands) {
        Collections.addAll(slashCommands, commands);
    }

    /**
     * Called when the bot is starting up. Override this method to add startup logic.
     */
    public abstract void onStartup();

    /**
     * Called when the bot is ready and connected to Discord.
     *
     * @param event the ready event from Discord
     */
    public abstract void onAppReady(ReadyEvent event);

    /**
     * Called when the bot is shutting down. Override this method to add cleanup logic.
     */
    public abstract void onShutdown();
}
