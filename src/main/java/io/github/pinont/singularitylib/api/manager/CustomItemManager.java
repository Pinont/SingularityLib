package io.github.pinont.singularitylib.api.manager;

import io.github.pinont.singularitylib.api.command.SimpleCommand;
import io.github.pinont.singularitylib.api.items.CustomItem;
import io.github.pinont.singularitylib.api.utils.Common;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.pinont.singularitylib.plugin.CorePlugin.getInstance;
import static io.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

/**
 * Manages custom items and provides a give command for them.
 * This class handles registration of custom items and provides functionality
 * to give items to players through commands.
 */
public class CustomItemManager implements SimpleCommand {

    /**
     * List of registered custom items.
     */
    public List<CustomItem> customItems = new ArrayList<>();

    /**
     * Default constructor for CustomItemManager.
     */
    public CustomItemManager() {
    }

    /**
     * Gets the list of registered custom items.
     *
     * @return the list of custom items
     */
    public List<CustomItem> getCustomItems() {
        return customItems;
    }

    /**
     * Registers a list of custom items with the manager.
     *
     * @param item the list of custom items to register
     */
    public void register(List<CustomItem> item) {
        if (item.isEmpty()) return;
        sendConsoleMessage("Registering " + item.size() + " custom items");
        for (CustomItem customItem : item) {
            customItems.add(customItem);
            customItem.register(); // call register to ensure the item is properly initialized
            customItem.getItem(); // ensure the item is created and interaction is set up
            if (customItem.getInteraction() != null) {
                sendConsoleMessage("Registering interaction " + customItem.getInteraction().getName() + " for item " + customItem.getName());
            }
        }
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
        } else if (itemName.startsWith(getInstance().getName().toLowerCase() + ":")) {
            String finalItemName = itemName.replace(getInstance().getName().toLowerCase() + ":", "");
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
                        .map(item -> getInstance().getName().toLowerCase() + ":" + item.getName())
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
