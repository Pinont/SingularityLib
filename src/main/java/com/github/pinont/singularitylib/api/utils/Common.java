package com.github.pinont.singularitylib.api.utils;

import com.github.pinont.singularitylib.api.enums.PlayerInventorySlotType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.pinont.singularitylib.plugin.CorePlugin.getInstance;

/**
 * Utility class providing common operations and helper methods.
 * This class contains various static methods for working with items, players, materials, and text formatting.
 */
public class Common {

    /**
     * Default constructor for Common utility class.
     */
    public Common() {
    }

    /**
     * Colorizes a string message using MiniMessage format.
     *
     * @param message the message to colorize
     * @param noItalic whether to disable italic formatting
     * @return the colorized Component
     */
    public @NotNull Component colorize(@NotNull String message, boolean noItalic) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        if (noItalic) {
            message = "<!italic>" + message;
        }
        return miniMessage.deserialize(message);
    }

    /**
     * Colorizes a string message using MiniMessage format without disabling italic.
     *
     * @param message the message to colorize
     * @return the colorized Component
     */
    public @NotNull Component colorize(@NotNull String message) {
        return colorize(message, false);
    }

    /**
     * Gets all valid item materials, filtering out invalid or non-item materials.
     *
     * @return array of valid item materials
     */
    public static Material[] getAllItemsMaterials() {
        List<Material> filterList = Arrays.asList(Material.AIR, Material.WATER, Material.LAVA, Material.VOID_AIR, Material.MOVING_PISTON, Material.PISTON_HEAD, Material.END_GATEWAY, Material.TALL_SEAGRASS, Material.TALL_GRASS, Material.FIRE, Material.SOUL_FIRE, Material.REDSTONE_WIRE, Material.NETHER_PORTAL, Material.END_PORTAL, Material.PUMPKIN_STEM, Material.MELON_STEM, Material.COCOA, Material.TRIPWIRE, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.FROSTED_ICE, Material.BAMBOO_SAPLING, Material.BUBBLE_COLUMN, Material.POWDER_SNOW, Material.BIG_DRIPLEAF_STEM);

        return Arrays.stream(Material.values()).filter(material -> !filterList.contains(material) && !material.name().startsWith("LEGACY_") && !material.name().contains("WALL_") && !material.name().startsWith("ATTACHED_") && !material.name().endsWith("_CAULDRON") && !material.name().startsWith("POTTED_") && !material.name().endsWith("_CROP") && !material.name().endsWith("_BUSH") && !material.name().endsWith("_PLANT") && !material.name().endsWith("_CAKE") && !material.name().startsWith("CAVE_")).toArray(Material[]::new);
    }

    /**
     * Checks if the given material is a valid item material.
     *
     * @param material the material to check
     * @return true if the material is a valid item, false otherwise
     */
    public static Boolean isItemIsMaterial(Material material) {
        return Arrays.asList(getAllItemsMaterials()).contains(material);
    }

    /**
     * Checks if the given ItemStack is air or null.
     *
     * @param item the ItemStack to check
     * @return true if the item is air or null, false otherwise
     */
    public static Boolean isAir(ItemStack item) {
        return (item == null) || (item.getType() == Material.AIR);
    }

    /**
     * The plugin instance.
     */
    public static Plugin plugin = getInstance();

    /**
     * Checks if a boolean expression is true, logs a message if false.
     *
     * @param expression the boolean expression to check
     * @param falseMessage the message to log if expression is false
     * @param replacements optional parameters for string formatting
     */
    public static void checkBoolean(final boolean expression, final String falseMessage, final Object... replacements) {
        if (!expression) {
            String message = falseMessage;

            try {
                message = String.format(falseMessage, replacements);

            } catch (final Throwable t) {
                Console.logWarning(t.getMessage());
            }
        }
    }

    /**
     * Creates an ItemStack from a material name string.
     *
     * @param itemName the name of the material
     * @return the created ItemStack
     * @throws IllegalArgumentException if the material name is invalid
     */
    public static ItemStack getItem(String itemName) {
        return new ItemStack(Material.valueOf(itemName));
    }

    /**
     * Logs an error message from a throwable.
     *
     * @param t the throwable to log
     */
    public static void sneaky(Throwable t) {
        Console.logError(t.getMessage());
    }

    /**
     * Gets all online players.
     *
     * @return collection of online players
     */
    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    /**
     * Gets the names of all online players.
     *
     * @return list of online player names
     */
    public static List<String> getOnlinePlayersNames() {
        List<String> players = new ArrayList<>();
        for (Player player : getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    /**
     * Normalizes a string name by converting underscores to spaces and capitalizing words.
     *
     * @param string the string to normalize
     * @return the normalized string
     */
    public static String normalizeStringName(String string) {
        String[] words = string.replace("_", " ").split(" ");
        StringBuilder properName = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                properName.append(word.substring(0, 1).toUpperCase());
                properName.append(word.substring(1).toLowerCase());
                properName.append(" ");
            }
        }
        return properName.toString().trim();
    }

    /**
     * Checks if a player's main hand is empty.
     *
     * @param player the player to check
     * @return true if the main hand is empty, false otherwise
     */
    public static Boolean isMainHandEmpty(Player player) {
        return isAir(player.getInventory().getItemInMainHand());
    }

    /**
     * Converts an integer to Roman numeral representation.
     *
     * @param input the integer to convert (must be between 1 and 3999)
     * @return the Roman numeral string, or "Invalid Roman Number Value" if out of range
     */
    public static String IntegerToRomanNumeral(int input) {
        if (input < 1 || input > 3999)
            return "Invalid Roman Number Value";
        StringBuilder s = new StringBuilder();
        while (input >= 1000) {
            s.append("M");
            input -= 1000;
        }
        while (input >= 900) {
            s.append("CM");
            input -= 900;
        }
        while (input >= 500) {
            s.append("D");
            input -= 500;
        }
        while (input >= 400) {
            s.append("CD");
            input -= 400;
        }
        while (input >= 100) {
            s.append("C");
            input -= 100;
        }
        while (input >= 90) {
            s.append("XC");
            input -= 90;
        }
        while (input >= 50) {
            s.append("L");
            input -= 50;
        }
        while (input >= 40) {
            s.append("XL");
            input -= 40;
        }
        while (input >= 10) {
            s.append("X");
            input -= 10;
        }
        while (input == 9) {
            s.append("IX");
            input -= 9;
        }
        while (input >= 5) {
            s.append("V");
            input -= 5;
        }
        while (input == 4) {
            s.append("IV");
            input -= 4;
        }
        while (input >= 1) {
            s.append("I");
            input -= 1;
        }
        return s.toString();
    }

    /**
     * Gets the nearest player to a location within a specified radius.
     *
     * @param location the location to search from
     * @param radius the search radius
     * @return the nearest player, or null if none found
     */
    public static Player getNearestPlayer(Location location, int radius) {
        return location.getNearbyPlayers(radius).stream().findFirst().orElse(null);
    }

    /**
     * Checks if a specific inventory slot contains air for a player.
     *
     * @param type the slot type to check
     * @param player the player whose inventory to check
     * @return true if the slot contains air, false otherwise
     */
    public static boolean checkAirInSlot(PlayerInventorySlotType type, Player player) {
        return switch (type) {
            case MAINHAND -> isAir(player.getInventory().getItemInMainHand());
            case OFFHAND -> isAir(player.getInventory().getItemInOffHand());
            case ARMOR_HEAD -> isAir(player.getInventory().getHelmet());
            case ARMOR_CHEST -> isAir(player.getInventory().getChestplate());
            case ARMOR_LEGS -> isAir(player.getInventory().getLeggings());
            case ARMOR_FEET -> isAir(player.getInventory().getBoots());
        };
    }

    /**
     * Gets the ItemStack in a specific inventory slot for a player.
     *
     * @param type the slot type to get the item from
     * @param player the player whose inventory to check
     * @return the ItemStack in the specified slot, or air if slot is empty
     */
    public static ItemStack getItemInSlot(PlayerInventorySlotType type, Player player) {
        if (checkAirInSlot(type, player)) return new ItemStack(Material.AIR);
        return switch (type) {
            case MAINHAND -> player.getInventory().getItemInMainHand();
            case OFFHAND -> player.getInventory().getItemInOffHand();
            case ARMOR_HEAD -> player.getInventory().getHelmet();
            case ARMOR_CHEST -> player.getInventory().getChestplate();
            case ARMOR_LEGS -> player.getInventory().getLeggings();
            case ARMOR_FEET -> player.getInventory().getBoots();
        };
    }

    /**
     * Removes color codes from a string.
     *
     * @param text the text to remove colors from
     * @return the text without color codes
     */
    public static String resetStringColor(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    /**
     * Converts a Component to its MiniMessage string representation.
     *
     * @param component the component to convert
     * @return the MiniMessage string representation
     */
    public static String resetStringColor(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static long toTicks(long amount, TimeUnit unit) {
        return switch (unit) {
            case SECONDS -> amount * 20;
            case MINUTES -> amount * 20 * 60;
            case HOURS -> amount * 20 * 60 * 60;
            case DAYS -> amount * 20 * 60 * 60 * 24;
            default -> amount;
        };
    }
}