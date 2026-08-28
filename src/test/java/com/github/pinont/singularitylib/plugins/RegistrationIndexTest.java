package com.github.pinont.singularitylib.plugins;

import com.github.pinont.singularitylib.api.annotation.AutoRegister;
import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.items.CustomItem;
import com.github.pinont.singularitylib.api.items.ItemCreator;
import com.github.pinont.singularitylib.api.items.ItemInteraction;
import com.github.pinont.singularitylib.plugin.register.Register;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

/**
 * Tests the compile-time {@code @AutoRegister} index registration rework:
 * <ul>
 *   <li>the index resource filename constant used by both the runtime loader
 *       ({@link Register#INDEX_RESOURCE}) and the annotation processor;</li>
 *   <li>that explicitly-instantiated {@code @AutoRegister} classes are collected
 *       into listeners / commands / custom items.</li>
 * </ul>
 * The {@code @AutoRegister} annotation itself must be visible to the annotation
 * processor at compile time, so its retention is asserted to be {@code CLASS}.
 */
public class RegistrationIndexTest {

    /** Mirrors the resource path the processor writes and {@link Register} reads. */
    @AutoRegister
    public static class IndexListener implements Listener {
    }

    @AutoRegister
    public static class IndexCommand implements SimpleCommand {
        @Override
        public String getName() {
            return "indexcmd";
        }

        @Override
        public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        }
    }

    @AutoRegister
    public static class IndexCustomItem extends CustomItem {
        @Override
        public ItemInteraction getInteraction() {
            return new ItemInteraction("index", Set.of()) {
                @Override
                public void execute(Player player) {
                }
            };
        }

        @Override
        public ItemCreator register() {
            return new ItemCreator(null, Material.STONE);
        }
    }

    private Register register;

    @BeforeEach
    public void setUp() {
        Register.resetDedupe();
        register = new Register();
    }

    @Test
    @DisplayName("index resource filename is the expected constant")
    public void indexResourceFilename() {
        Assertions.assertEquals(
                "META-INF/singularitylib/auto-register-index.properties",
                Register.INDEX_RESOURCE,
                "runtime loader and annotation processor must agree on the index path"
        );
    }

    @Test
    @DisplayName("@AutoRegister keeps CLASS retention (visible to the processor at compile time)")
    public void autoRegisterRetentionIsClass() {
        Retention retention = AutoRegister.class.getAnnotation(Retention.class);
        Assertions.assertNotNull(retention, "@AutoRegister must declare @Retention");
        Assertions.assertEquals(RetentionPolicy.CLASS, retention.value());
    }

    @Test
    @DisplayName("explicitly-instantiated @AutoRegister classes are collected into listeners/commands/items")
    public void collectAnnotatedClasses() {
        register.register(IndexListener.class, IndexCommand.class, IndexCustomItem.class);

        Assertions.assertEquals(1, register.getListeners().size(),
                "index listener must be collected into listeners");
        Assertions.assertEquals(1, register.getCommands().size(),
                "index command must be collected into commands");

        // CustomItem is abstract-free with a no-arg ctor; classification is into
        // custom items (not listeners/commands).
        Assertions.assertEquals(1, register.getCustomItems().size(),
                "index custom item must be collected into custom items");
    }

    @Test
    @DisplayName("collection respects the shared dedupe (first registration wins)")
    public void dedupeSkipsSecondRegistration() {
        register.register(IndexListener.class);
        Register second = new Register();
        second.register(IndexListener.class);

        Assertions.assertEquals(1, register.getListeners().size());
        Assertions.assertTrue(second.getListeners().isEmpty(),
                "duplicate class names are skipped after the first registration");
    }
}