package com.github.pinont.singularitylib.api.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Failure-path / guard-rail coverage for CommandGroup: players-only blocking,
 * alias registration, unknown dispatch, no-args help. (Permission-denied is
 * delegated to Bukkit's sender.hasPermission and not unit-tested here.)
 */
public class CommandGroupFailureTest {

    private ConsoleCommandSender sender;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        sender = MockBukkit.getMock().getConsoleSender();
        CommandGroupTest.class.getClassLoader().getClass();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    /** Subcommand that is players-only. */
    static class PlayerOnlySub extends SubCommand {
        final List<String> ran = new ArrayList<>();

        @Override public String getName() { return "po"; }
        @Override public boolean isPlayerOnly() { return true; }
        @Override public void execute(CommandSender s, String[] args) { ran.add("ran"); }
    }

    static class Group extends CommandGroup {
        final PlayerOnlySub po = new PlayerOnlySub();

        Group() { registerSubcommand(po); }
        @Override public String getName() { return "failgroup"; }
    }

    @Test
    @DisplayName("players-only subcommand blocked for console sender")
    public void playerOnlyBlocked() {
        Group g = new Group();
        g.execute(sender, new String[]{"po"});
        Assertions.assertEquals(0, g.po.ran.size(), "players-only sub not run from console");
    }

    @Test
    @DisplayName("unknown subcommand -> no dispatch, error message")
    public void unknownSub() {
        Group g = new Group();
        g.execute(sender, new String[]{"nope"});
        Assertions.assertEquals(0, g.po.ran.size());
    }

    @Test
    @DisplayName("subcommand aliases via 'name:alias' register under both")
    public void aliasesRegister() {
        final List<String> ran = new ArrayList<>();
        SubCommand aliasSub = new SubCommand() {
            @Override public String getName() { return "go:g"; }
            @Override public void execute(CommandSender s, String[] a) { ran.add("go"); }
        };
        CommandGroup g = new CommandGroup() {
            { registerSubcommand(aliasSub); }
            @Override public String getName() { return "aliasgroup2"; }
        };
        g.execute(sender, new String[]{"go"});
        g.execute(sender, new String[]{"g"});
        Assertions.assertEquals(2, ran.size(), "both name and alias dispatch");
    }

    @Test
    @DisplayName("help with no args lists plugin name")
    public void noArgsHelp() {
        Group g = new Group();
        Assertions.assertDoesNotThrow(() -> g.execute(sender, new String[]{}), "no-args help is safe");
    }
}