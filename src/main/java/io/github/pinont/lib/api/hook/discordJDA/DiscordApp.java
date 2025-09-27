package io.github.pinont.lib.api.hook.discordJDA;

import io.github.pinont.lib.api.manager.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;

import static io.github.pinont.lib.plugin.CorePlugin.sendConsoleMessage;

public abstract class DiscordApp {

    public final ArrayList<SimpleSlashCommands> slashCommands = new ArrayList<>();
    private final boolean multiThread;
    private final ConfigManager configManager;
    private final String configPath;
    private Thread jdaThread;
    private JDA jda;

    public DiscordApp(String configPath) {
        this(configPath, false);
    }

    public DiscordApp(String configPath, boolean multiThread) {
        this.configPath = configPath;
        configManager = new ConfigManager(configPath);
        this.multiThread = multiThread;
    }

    public void reloadConfig() {
        shutdown();
        start();
    }

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

    public void addCommands(SimpleSlashCommands... commands) {
        Collections.addAll(slashCommands, commands);
    }

    public abstract void onStartup();

    public abstract void onAppReady(ReadyEvent event);

    public abstract void onShutdown();
}
