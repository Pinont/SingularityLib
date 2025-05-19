package com.pinont.lib.api.utils;

import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Common {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Material[] getAllItemsMaterials() {
        List<Material> filterList = Arrays.asList(Material.AIR, Material.WATER, Material.LAVA, Material.VOID_AIR, Material.MOVING_PISTON, Material.PISTON_HEAD, Material.END_GATEWAY, Material.TALL_SEAGRASS, Material.TALL_GRASS, Material.FIRE, Material.SOUL_FIRE, Material.REDSTONE_WIRE, Material.NETHER_PORTAL, Material.END_PORTAL, Material.PUMPKIN_STEM, Material.MELON_STEM, Material.COCOA, Material.TRIPWIRE, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.FROSTED_ICE, Material.BAMBOO_SAPLING, Material.BUBBLE_COLUMN, Material.POWDER_SNOW, Material.BIG_DRIPLEAF_STEM);

        return Arrays.stream(Material.values()).filter(material -> !filterList.contains(material) && !material.name().startsWith("LEGACY_") && !material.name().contains("WALL_") && !material.name().startsWith("ATTACHED_") && !material.name().endsWith("_CAULDRON") && !material.name().startsWith("POTTED_") && !material.name().endsWith("_CROP") && !material.name().endsWith("_BUSH") && !material.name().endsWith("_PLANT") && !material.name().endsWith("_CAKE") && !material.name().startsWith("CAVE_")).toArray(Material[]::new);
    }

    public static Boolean isItemMaterial(Material material) {
        return Arrays.asList(getAllItemsMaterials()).contains(material);
    }

    public static Plugin plugin = CorePlugin.getInstance();

    public static Set<String> getCommands() {
        return plugin.getDescription().getCommands().keySet();
    }

    public static void checkBoolean(final boolean expression, final String falseMessage, final Object... replacements) {
        if (!expression) {
            String message = falseMessage;

            try {
                message = String.format(falseMessage, replacements);

            } catch (final Throwable t) {
                Bukkit.getLogger().warning(t.getMessage());
            }
        }
    }

    public static ItemStack getItem(String itemName) {
        return new ItemStack(Material.valueOf(itemName));
    }

    public static void sneaky(Throwable t) {
        throw new RuntimeException(t);
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
            if (word.length() > 0) {
                properName.append(word.substring(0, 1).toUpperCase());
                properName.append(word.substring(1).toLowerCase());
                properName.append(" ");
            }
        }
        return properName.toString().trim();
    }

    public static Boolean isMainHandEmpty(Player player) {
        return (player.getInventory().getItemInMainHand().getType() == Material.AIR) || (player.getInventory().getItemInMainHand() == null);
    }

    public static String IntegerToRomanNumeral(int input) {
        if (input < 1 || input > 3999)
            return "Invalid Roman Number Value";
        String s = "";
        while (input >= 1000) {
            s += "M";
            input -= 1000;        }
        while (input >= 900) {
            s += "CM";
            input -= 900;
        }
        while (input >= 500) {
            s += "D";
            input -= 500;
        }
        while (input >= 400) {
            s += "CD";
            input -= 400;
        }
        while (input >= 100) {
            s += "C";
            input -= 100;
        }
        while (input >= 90) {
            s += "XC";
            input -= 90;
        }
        while (input >= 50) {
            s += "L";
            input -= 50;
        }
        while (input >= 40) {
            s += "XL";
            input -= 40;
        }
        while (input >= 10) {
            s += "X";
            input -= 10;
        }
        while (input >= 9) {
            s += "IX";
            input -= 9;
        }
        while (input >= 5) {
            s += "V";
            input -= 5;
        }
        while (input >= 4) {
            s += "IV";
            input -= 4;
        }
        while (input >= 1) {
            s += "I";
            input -= 1;
        }
        return s;
    }

    public static Player getNearestPlayer(Location location, int radius) {
        return location.getNearbyPlayers(radius).stream().findFirst().orElse(null);
    }

    public static String resetStringColor(String name) {
        return name.replaceAll("§[0-9a-fk-or]", "");
    }
}