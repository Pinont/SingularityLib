package com.github.pinont.singularitylib.plugins;

import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.plugin.register.Register;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Failure-path coverage for the registration system:
 * what happens when classes are bad, missing ctors, or unknown.
 */
public class RegistrationFailureTest {

    private Register register;

    @BeforeEach
    public void setUp() {
        Register.resetDedupe();
        register = new Register();
    }

    /** A component class WITHOUT a no-arg ctor — must be caught, not thrown. */
    public static class NoDefaultCtorCommand implements SimpleCommand {
        @SuppressWarnings("unused")
        public NoDefaultCtorCommand(String arg) {
        }

        @Override public String getName() { return "noarg"; }
        @Override public void execute(io.papermc.paper.command.brigadier.CommandSourceStack s, String[] a) {
        }
    }

    /** A component class whose ctor throws — must be caught, not thrown. */
    public static class ThrowingCtorCommand implements SimpleCommand {
        public ThrowingCtorCommand() {
            throw new IllegalStateException("boom");
        }

        @Override public String getName() { return "throw"; }
        @Override public void execute(io.papermc.paper.command.brigadier.CommandSourceStack s, String[] a) {
        }
    }

    @Test
    @DisplayName("register(Class) with missing no-arg ctor is caught, not thrown")
    public void noDefaultCtor() {
        Assertions.assertDoesNotThrow(() -> register.register(NoDefaultCtorCommand.class));
        Assertions.assertEquals(0, register.getCommands().size(), "broken class not added");
    }

    @Test
    @DisplayName("register(Class) with throwing ctor is caught, not thrown")
    public void throwingCtor() {
        Assertions.assertDoesNotThrow(() -> register.register(ThrowingCtorCommand.class));
        Assertions.assertEquals(0, register.getCommands().size(), "throwing class not added");
    }

    @Test
    @DisplayName("dedupe prevents double-registration of same class")
    public void dedupe() {
        register.register(com.github.pinont.singularitylib.plugins.RegistrationTest.TestCommand.class);
        register.register(com.github.pinont.singularitylib.plugins.RegistrationTest.TestCommand.class);
        Assertions.assertEquals(1, register.getCommands().size(), "same class registered once");
    }

    @Test
    @DisplayName("unknown class name in index stream is tolerated, not thrown")
    public void unknownIndexClass() {
        Assertions.assertDoesNotThrow(() -> register.scanAndCollect("com.github.pinont.does.not.exist"));
    }
}