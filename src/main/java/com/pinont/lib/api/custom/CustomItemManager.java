package com.pinont.lib.api.custom;

import com.pinont.lib.api.utils.Common;
import com.pinont.lib.plugin.CorePlugin;
import com.pinont.lib.plugin.SimpleCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.pinont.lib.api.utils.Common.resetStringColor;

public class CustomItemManager implements SimpleCommand {

    public List<CustomItem> customItems = new ArrayList<>();

    public CustomItemManager register(List<CustomItem> item) {
        customItems.addAll(item);
        return this;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String description() {
        return "Give item to player";
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if (strings.length < 2) {
            commandSourceStack.getSender().sendMessage("Usage: /give <player> <item> [count]");
            return;
        }

        String itemName = strings[1];
        int count = strings.length > 2 ? Integer.parseInt(strings[2]) : 1;

        // Find the item
        ItemStack item = null;
        if (itemName.startsWith("minecraft:")) {
            itemName = itemName.replace("minecraft:", "");
            item = new ItemStack(Objects.requireNonNull(Material.getMaterial(itemName.toUpperCase())), count);
        } else if (itemName.startsWith(CorePlugin.getInstance().getName().toLowerCase() + ":")) {
            String finalItemName = itemName.replace(CorePlugin.getInstance().getName().toLowerCase() + ":", "");
            item = customItems.stream()
                    .filter(customItem -> customItem.getName().equalsIgnoreCase(finalItemName))
                    .findFirst()
                    .map(customItem -> customItem.register().setAmount(count).create())
                    .orElse(null);
        }

        if (item == null) {
            commandSourceStack.getSender().sendMessage("Item not found");
            return;
        }

        // Give the item to the player
        if (strings[0].equalsIgnoreCase("@a")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                giveItemToPlayer(player, item);
            }
        } else if (strings[0].equalsIgnoreCase("@r")) {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            Player randomPlayer = players.get(new Random().nextInt(players.size()));
            giveItemToPlayer(randomPlayer, item);
        } else if (strings[0].equalsIgnoreCase("@s")) {
            Player player = (Player) commandSourceStack.getSender();
            giveItemToPlayer(player, item);
        } else {
            Player player = Bukkit.getPlayer(strings[0]);
            if (player == null) {
                commandSourceStack.getSender().sendMessage("Player not found");
                return;
            }
            giveItemToPlayer(player, item);
        }
    }

    private void giveItemToPlayer(Player player, ItemStack item) {
        if (player != null) {
            player.getInventory().addItem(item);
            player.sendMessage("Gave " + item.getAmount() + " " + item.getItemMeta().getItemName() + ChatColor.RESET + " to " + player.getName());
        } else {
            player.sendMessage("Player not found");
        }
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return switch (args.length) {
            case 0, 1 -> {
                List<String> players = new ArrayList<>();
                players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                players.addAll(Arrays.asList("@a", "@s", "@r"));
                yield players;
            }
            case 2 -> {
                List<String> items = new ArrayList<>();
                items.addAll(Arrays.stream(Common.getAllItemsMaterials())
                        .map(material -> "minecraft:" + material.name().toLowerCase())
                        .toList());
                items.addAll(customItems.stream()
                        .map(item -> CorePlugin.getInstance().getName().toLowerCase() + ":" + item.getName())
                        .toList());
                if (!args[1].isEmpty()) {
                    yield items.stream()
                            .filter(item -> item.split(":")[1].toLowerCase().startsWith(args[1].toLowerCase()))
                            .toList();
                }
                yield items;
            }
            case 3 -> Collections.singletonList("<count>");
            default -> Collections.emptyList();
        };
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender instanceof Player;
    }
}
