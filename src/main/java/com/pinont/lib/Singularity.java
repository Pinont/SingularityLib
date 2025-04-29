package com.pinont.lib;

import com.pinont.lib.api.manager.ConfigManager;
import com.pinont.lib.plugin.CorePlugin;
import com.pinont.lib.plugin.DevTool;
import com.pinont.lib.plugin.events.PlayerListener;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Singularity extends CorePlugin {

    private static ConfigManager configManager;

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onPluginStart() {
        configManager = new ConfigManager(Singularity.getInstance(), "config.yml");
        FileConfiguration config = getConfigManager().getConfig();
        registerEvents(Singularity.getInstance(), new PlayerListener());

        String[] randomNames = {
                "\n" +
                        "        _____ _                   __           _ __        __    _ __\n" +
                        "       / ___/(_)___  ____ ___  __/ /___ ______(_) /___  __/ /   (_) /_\n" +
                        "       \\__ \\/ / __ \\/ __ `/ / / / / __ `/ ___/ / __/ / / / /   / / __ \\\n" +
                        "      ___/ / / / / / /_/ / /_/ / / /_/ / /  / / /_/ /_/ / /___/ / /_/ /\n" +
                        "     /____/_/_/ /_/\\__, /\\__,_/_/\\__,_/_/  /_/\\__/\\__, /_____/_/_.___/\n" +
                        "                  /____/                         /____/                \n",
                "\n" +
                        "        ___  ____  _  _  ___  __  __  __      __    ____  ____  ____  _  _ \n" +
                        "       / __)(_  _)( \\( )/ __)(  )(  )(  )    /__\\  (  _ \\(_  _)(_  _)( \\/ )\n" +
                        "       \\__ \\ _)(_  )  (( (_-. )(__)(  )(__  /(__)\\  )   / _)(_   )(   \\  / \n" +
                        "       (___/(____)(_)\\_)\\___/(______)(____)(__)(__)(_\\_)(____) (__)  (__) \n",
                "\n" +
                        "          ,-,--.   .=-.-..-._            _,---.                          ,---.                   .=-.-.,--.--------.                \n" +
                        "        ,-.'-  _\\ /==/_ /==/ \\  .-._ _.='.'-,  \\ .--.-. .-.-.  _.-.    .--.'  \\      .-.,.---.  /==/_ /==/,  -   , -\\,--.-.  .-,--. \n" +
                        "       /==/_ ,_.'|==|, ||==|, \\/ /, /==.'-     //==/ -|/=/  |.-,.'|    \\==\\-/\\ \\    /==/  `   \\|==|, |\\==\\.-.  - ,-./==/- / /=/_ /  \n" +
                        "       \\==\\  \\   |==|  ||==|-  \\|  /==/ -   .-' |==| ,||=| -|==|, |    /==/-|_\\ |  |==|-, .=., |==|  | `--`\\==\\- \\  \\==\\, \\/=/. /   \n" +
                        "        \\==\\ -\\  |==|- ||==| ,  | -|==|_   /_,-.|==|- | =/  |==|- |    \\==\\,   - \\ |==|   '='  /==|- |      \\==\\_ \\  \\==\\  \\/ -/    \n" +
                        "        _\\==\\ ,\\ |==| ,||==| -   _ |==|  , \\_.' )==|,  \\/ - |==|, |    /==/ -   ,| |==|- ,   .'|==| ,|      |==|- |   |==|  ,_/     \n" +
                        "       /==/\\/ _ ||==|- ||==|  /\\ , \\==\\-  ,    (|==|-   ,   /==|- `-._/==/-  /\\ - \\|==|_  . ,'.|==|- |      |==|, |   \\==\\-, /      \n" +
                        "       \\==\\ - , //==/. //==/, | |- |/==/ _  ,  //==/ , _  .'/==/ - , ,|==\\ _\\.\\=\\.-'/==/  /\\ ,  )==/. /      /==/ -/   /==/._/       \n" +
                        "        `--`---' `--`-` `--`./  `--``--`------' `--`..---'  `--`-----' `--`        `--`-`--`--'`--`-`       `--`--`   `--`-`       \n",
                "\n" +
                        "        ____  __  __ _   ___  _  _  __     __   ____  __  ____  _  _ \n" +
                        "       / ___)(  )(  ( \\ / __)/ )( \\(  )   / _\\ (  _ \\(  )(_  _)( \\/ )\n" +
                        "       \\___ \\ )( /    /( (_ \\) \\/ (/ (_/\\/    \\ )   / )(   )(   )  / \n" +
                        "       (____/(__)\\_)__) \\___/\\____/\\____/\\_/\\_/(__\\_)(__) (__) (__/  \n",
                "\n" +
                        "         _________.__                     .__               .__  __          \n" +
                        "        /   _____/|__| ____    ____  __ __|  | _____ _______|__|/  |_ ___.__. \n" +
                        "        \\_____  \\ |  |/    \\  / ___\\|  |  \\  | \\__  \\\\_  __ \\  \\   __<   |  | \n" +
                        "        /        \\|  |   |  \\/ /_/  >  |  /  |__/ __ \\|  | \\/  ||  |  \\___  | \n" +
                        "       /_______  /|__|___|  /\\___  /|____/|____(____  /__|  |__||__|  / ____| \n" +
                        "               \\/         \\/_____/                 \\/                \\/      \n"
        };

        ChatColor randomChatColor = ChatColor.values()[(int) (Math.random() * ChatColor.values().length)];
        sendConsoleMessage(randomChatColor + randomNames[(int) (Math.random() * randomNames.length)]);
        ConfigManager configManager = getConfigManager();
        if (configManager.isFirstLoad) {
            config.set("debug", false);
            config.set("dev-tool", false);
            configManager.saveConfig();
            config.options().copyDefaults(true);
            return;
        }
        config.options().copyDefaults(true);

        if (config.getBoolean("dev-tool")) {
            final LifecycleEventManager<@NotNull Plugin> lifecycleManager = this.getLifecycleManager();
            lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
                // Handler for the event
                event.registrar().register("devTool", new BasicCommand() {
                    @Override
                    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
                        if (commandSourceStack.getSender() instanceof Player player) {
                            new DevTool(player);
                            return;
                        }
                        commandSourceStack.getSender().sendMessage("This command can only be executed by a player!");
                    }
                });
            });
        }
    }

    @Override
    public void onPluginStop() {
        sendConsoleMessage(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "SingularityAPI " + ChatColor.RESET + "Stopped!" );
    }
}
