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

import java.util.*;

import static com.github.pinont.singularitylib.plugin.CorePlugin.getInstance;

public class Common {

    public @NotNull Component colorize(String message, boolean noItalic) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        if (noItalic) {
            message = "<!italic>" + message;
        }
        return miniMessage.deserialize(message);
    }

    public @NotNull Component colorize(String message) {
        return colorize(message, false);
    }

    public static Material[] getAllItemsMaterials() {
        List<Material> filterList = Arrays.asList(Material.AIR, Material.WATER, Material.LAVA, Material.VOID_AIR, Material.MOVING_PISTON, Material.PISTON_HEAD, Material.END_GATEWAY, Material.TALL_SEAGRASS, Material.TALL_GRASS, Material.FIRE, Material.SOUL_FIRE, Material.REDSTONE_WIRE, Material.NETHER_PORTAL, Material.END_PORTAL, Material.PUMPKIN_STEM, Material.MELON_STEM, Material.COCOA, Material.TRIPWIRE, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.FROSTED_ICE, Material.BAMBOO_SAPLING, Material.BUBBLE_COLUMN, Material.POWDER_SNOW, Material.BIG_DRIPLEAF_STEM);

        return Arrays.stream(Material.values()).filter(material -> !filterList.contains(material) && !material.name().startsWith("LEGACY_") && !material.name().contains("WALL_") && !material.name().startsWith("ATTACHED_") && !material.name().endsWith("_CAULDRON") && !material.name().startsWith("POTTED_") && !material.name().endsWith("_CROP") && !material.name().endsWith("_BUSH") && !material.name().endsWith("_PLANT") && !material.name().endsWith("_CAKE") && !material.name().startsWith("CAVE_")).toArray(Material[]::new);
    }

    public static Boolean isItemIsMaterial(Material material) {
        return Arrays.asList(getAllItemsMaterials()).contains(material);
    }

    public static Boolean isAir(ItemStack item) {
        return (item == null) || (item.getType() == Material.AIR);
    }

    public static Plugin plugin = getInstance();

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

    public static ItemStack getItem(String itemName) {
        return new ItemStack(Material.valueOf(itemName));
    }

    public static void sneaky(Throwable t) {
        Console.logError(t.getMessage());
    }

    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static List<String> getOnlinePlayersNames() {
        List<String> players = new ArrayList<>();
        for (Player player : getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

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

    public static Boolean isMainHandEmpty(Player player) {
        return isAir(player.getInventory().getItemInMainHand());
    }

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

    public static Player getNearestPlayer(Location location, int radius) {
        return location.getNearbyPlayers(radius).stream().findFirst().orElse(null);
    }

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

    public static ItemStack getItemInSlot(PlayerInventorySlotType type, Player player) {
        if (!checkAirInSlot(type, player)) return new ItemStack(Material.AIR);
        return switch (type) {
            case MAINHAND -> player.getInventory().getItemInMainHand();
            case OFFHAND -> player.getInventory().getItemInOffHand();
            case ARMOR_HEAD -> player.getInventory().getHelmet();
            case ARMOR_CHEST -> player.getInventory().getChestplate();
            case ARMOR_LEGS -> player.getInventory().getLeggings();
            case ARMOR_FEET -> player.getInventory().getBoots();
        };
    }

    public static String resetStringColor(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    public static String resetStringColor(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }
}