package com.github.pinont.singularitylib.api.items;

import com.github.pinont.singularitylib.TestPlugin;
import com.github.pinont.singularitylib.api.enums.AttributeType;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests the {@link Attributes} item attribute helpers against MockBukkit.
 *
 * <p>MockBukkit's {@code ItemMetaMock} implements the full attribute-modifier
 * surface ({@code addAttributeModifier} / {@code getAttributeModifiers}), so
 * these helpers can be exercised end-to-end without a live server.
 */
public class AttributesTest {

    private TestPlugin plugin;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        this.plugin = MockBukkit.load(TestPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("setAttribute applies an attribute modifier and getAttribute reads it back")
    public void setAndGetRoundTrip() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack modified = Attributes.setAttribute(sword, AttributeType.ATTACK_DAMAGE, 4.0, AttributeModifier.Operation.ADD_NUMBER);

        assertNotSame(sword, modified, "helper returns a new ItemStack, not the input");
        assertEquals(4.0, Attributes.getAttribute(modified, AttributeType.ATTACK_DAMAGE), 1e-9, "modifier amount read back");
        assertEquals(0.0, Attributes.getAttribute(sword, AttributeType.ATTACK_DAMAGE), 1e-9, "original item untouched");
    }

    @Test
    @DisplayName("getAttribute returns 0.0 when no modifiers are present")
    public void getAttributeAbsent() {
        ItemStack plain = new ItemStack(Material.STONE);
        assertEquals(0.0, Attributes.getAttribute(plain, AttributeType.ARMOR), 1e-9, "no modifiers -> 0.0");
    }

    @Test
    @DisplayName("multiple modifiers on the same attribute are summed")
    public void multipleModifiersSum() {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack once = Attributes.setAttribute(sword, AttributeType.ATTACK_DAMAGE, 2.0, AttributeModifier.Operation.ADD_NUMBER);
        ItemStack twice = Attributes.setAttribute(once, AttributeType.ATTACK_DAMAGE, 3.0, AttributeModifier.Operation.ADD_NUMBER);

        assertEquals(5.0, Attributes.getAttribute(twice, AttributeType.ATTACK_DAMAGE), 1e-9, "modifier amounts summed");
    }
}