package com.github.pinont.singularitylib.api.command;

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
 * Tests CommandGroup/SubCommand dispatch + help using MockBukkit's console sender
 * and the sender-based execute path (no CommandSourceStack needed).
 */
public class CommandGroupTest {

    private ConsoleCommandSender sender;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        sender = MockBukkit.getMock().getConsoleSender();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    static class EchoSub extends SubCommand {
        final List<String> received = new ArrayList<>();

        @Override public String getName() { return "echo"; }
        @Override public String getDescription() { return "Echo args"; }
        @Override public void execute(org.bukkit.command.CommandSender s, String[] args) {
            received.add(String.join(" ", args));
        }
    }

    static class Group extends CommandGroup {
        final EchoSub echo = new EchoSub();
        Group() { registerSubcommand(echo); }
        @Override public String getName() { return "testgroup"; }
    }

    @Test
    @DisplayName("dispatches to matching subcommand with remaining args")
    public void dispatch() {
        Group g = new Group();
        g.execute(sender, new String[]{"echo", "hello", "world"});
        Assertions.assertEquals(1, g.echo.received.size());
        Assertions.assertEquals("hello world", g.echo.received.get(0));
    }

    @Test
    @DisplayName("no args -> help sent, no dispatch")
    public void help() {
        Group g = new Group();
        g.execute(sender, new String[]{});
        Assertions.assertEquals(0, g.echo.received.size());
    }

    @Test
    @DisplayName("unknown subcommand -> no dispatch")
    public void unknown() {
        Group g = new Group();
        g.execute(sender, new String[]{"nope"});
        Assertions.assertEquals(0, g.echo.received.size());
    }
}