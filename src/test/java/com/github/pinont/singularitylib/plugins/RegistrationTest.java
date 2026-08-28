package com.github.pinont.singularitylib.plugins;

import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.plugin.register.Register;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the explicit registration DSL (the fast, no-scanning path introduced in v2):
 * {@link Register#register(Class[])} and {@link Register#register(Object[])}.
 */
public class RegistrationTest {

    public static class TestListener implements Listener {
    }

    public static class TestCommand implements SimpleCommand {
        @Override
        public String getName() {
            return "testcmd";
        }

        @Override
        public void execute(io.papermc.paper.command.brigadier.CommandSourceStack commandSourceStack, String[] strings) {
        }
    }

    private Register register;

    @BeforeEach
    public void setUp() {
        register = new Register();
    }

    @Test
    @DisplayName("register(Class...) instantiates and classifies components")
    public void registerClasses() {
        register.register(TestListener.class, TestCommand.class);
        Assertions.assertEquals(1, register.getListeners().size(), "listener collected");
        Assertions.assertEquals(1, register.getCommands().size(), "command collected");
    }

    @Test
    @DisplayName("register(Object...) accepts prebuilt instances")
    public void registerInstances() {
        register.register(new TestListener(), new TestCommand());
        Assertions.assertEquals(1, register.getListeners().size());
        Assertions.assertEquals(1, register.getCommands().size());
    }

    @Test
    @DisplayName("registerAll(Plugin) requires a running server — collection is the unit under test")
    public void collectionOnly() {
        register.register(TestCommand.class);
        Assertions.assertEquals(1, register.getCommands().size());
        Assertions.assertTrue(register.getListeners().isEmpty());
    }
}