package com.github.pinont.singularitylib.plugin.cmd;

import com.github.pinont.singularitylib.api.command.SimpleCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

import java.util.Collection;

@Deprecated(since = "2.1.0", forRemoval = true)
public class Vanish implements SimpleCommand {
    @Override
    public String getName() {
        return "vanish:v";
    }

    @Override
    public String description() {
        return "Vanish player";
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {

    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return SimpleCommand.super.suggest(commandSourceStack, args);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return SimpleCommand.super.canUse(sender);
    }
}
