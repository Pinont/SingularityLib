package com.pinont.lib.plugin;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleCommand implements BasicCommand, TabCompleter {
    private final String name;
    private final ArrayList<SimpleCommand> commands = new ArrayList<>();

    @Override
    public final void execute(CommandSourceStack commandSourceStack, String[] strings) {

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }

    public SimpleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Boolean command(CommandSender commandSender, String[] args);

    public abstract List<String> tabComplete(CommandSender commandSender, String[] args, List<String> completions);
}
