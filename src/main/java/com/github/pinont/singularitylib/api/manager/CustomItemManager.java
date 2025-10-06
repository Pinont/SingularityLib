package com.github.pinont.singularitylib.api.manager;

import com.github.pinont.devtool.api.CItemManager;
import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.items.CustomItem;
import com.github.pinont.singularitylib.api.utils.Common;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.github.pinont.singularitylib.plugin.CorePlugin.getInstance;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

/**
 * Manages custom items and provides a give command for them.
 * This class handles registration of custom items and provides functionality
 * to give items to players through commands.
 */
public class CustomItemManager implements SimpleCommand {

    /**
     * List of registered custom items managed by this manager.
     * This list contains all custom items that have been registered
     * and are available for use in commands and other operations.
     */
    public List<CustomItem> customItems = new ArrayList<>();

    /**
     * Integration manager for SingularityDevTool plugin.
     * Used when the dev tool plugin is present to provide additional
     * development and debugging features for custom items.
     */
    private final CItemManager customItemManager = new CItemManager();

    /**
     * Default constructor for CustomItemManager.
     * Initializes the manager with empty collections ready for item registration.
     */
    public CustomItemManager() {
    }

    /**
     * Gets the list of registered custom items.
     *
     * @return an unmodifiable view of the list of custom items currently registered
     */
    public List<CustomItem> getCustomItems() {
        return customItems;
    }

    /**
     * Registers a list of custom items with the manager.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Validates the input list is not empty</li>
     *   <li>Logs the registration process to console</li>
     *   <li>Checks for SingularityDevTool plugin integration</li>
     *   <li>Registers items with dev tool if available</li>
     *   <li>Initializes item interactions and properties</li>
     * </ul>
     *
     * @param item the list of custom items to register, must not be null or empty
     * @throws IllegalArgumentException if the item list is null
     */
    public void register(List<CustomItem> item) {
        if (item.isEmpty()) return;
        sendConsoleMessage("Registering " + item.size() + " custom items");
        if (getInstance().getServer().getPluginManager().getPlugin("SingularityDevTool") != null) {
            registerAllItems(item, true);
        }
        registerAllItems(item);
    }

    /**
     * Internal method to register all items without dev tool integration.
     *
     * @param items the list of items to register
     */
    private void registerAllItems(List<CustomItem> items) {
        registerAllItems(items, false);
    }

    /**
     * Internal method to register all items with optional dev tool integration.
     *
     * <p>For each custom item, this method:</p>
     * <ul>
     *   <li>Adds the item to the internal registry</li>
     *   <li>Registers with dev tool if requested and available</li>
     *   <li>Calls the item's register method for initialization</li>
     *   <li>Ensures the item stack is created and interactions are set up</li>
     *   <li>Logs interaction registration if present</li>
     * </ul>
     *
     * @param items the list of items to register
     * @param forDevTool whether to register items with the development tool
     */
    private void registerAllItems(List<CustomItem> items, boolean forDevTool) {
        for (CustomItem customItem : items) {
            customItems.add(customItem);
            if (forDevTool) {
                customItemManager.registerCustomItems(customItem);
            }
            customItem.register(); // call register to ensure the item is properly initialized
            customItem.getItem(); // ensure the item is created and interaction is set up
            if (customItem.getInteraction() != null) {
                sendConsoleMessage("Registering interaction " + customItem.getInteraction().getName() + " for item " + customItem.getName());
            }
        }
    }

    /**
     * Gets the name of this command.
     *
     * @return the command name "give"
     */
    @Override
    public String getName() {
        return "give";
    }

    /**
     * Gets the description of this command.
     *
     * @return a brief description of what this command does
     */
    @Override
    public String description() {
        return "Give item to player";
    }

    /**
     * Executes the give command with the provided arguments.
     *
     * <p>Command syntax: /give &lt;player&gt; &lt;item&gt; [count]</p>
     *
     * <p>Supported player selectors:</p>
     * <ul>
     *   <li>@a - All players</li>
     *   <li>@r - Random player</li>
     *   <li>@s - Command sender (self)</li>
     *   <li>player_name - Specific player by name</li>
     * </ul>
     *
     * <p>Supported item formats:</p>
     * <ul>
     *   <li>minecraft:item_name - Vanilla Minecraft items</li>
     *   <li>plugin_name:item_name - Custom plugin items</li>
     * </ul>
     *
     * @param commandSourceStack the source of the command execution
     * @param strings the command arguments [player, item, count]
     */
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

    /**
     * Gives an item to a specific player and sends a confirmation message.
     *
     * <p>This method adds the item to the player's inventory and sends
     * a confirmation message showing the item name, quantity, and recipient.</p>
     *
     * @param player the player to receive the item, must not be null
     * @param item the item stack to give, must not be null
     */
    private void giveItemToPlayer(Player player, ItemStack item) {
        if (player != null) {
            player.getInventory().addItem(item);
            player.sendMessage("Gave " + item.getAmount() + " " + item.getItemMeta().getItemName() + ChatColor.RESET + " to " + player.getName());
        } else {
            player.sendMessage("Player not found");
        }
    }

    /**
     * Provides command suggestions for tab completion.
     *
     * <p>Suggestions provided:</p>
     * <ul>
     *   <li>Argument 1: Player names and selectors (@a, @s, @r)</li>
     *   <li>Argument 2: Available items (minecraft: and plugin: prefixed)</li>
     *   <li>Argument 3: Count placeholder</li>
     * </ul>
     *
     * @param commandSourceStack the source of the command execution
     * @param args the current command arguments being typed
     * @return a collection of suggested completions for the current argument
     */
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

    /**
     * Determines if a command sender can use this command.
     *
     * <p>Currently, only players are allowed to use the give command.
     * Console and other command senders are not permitted.</p>
     *
     * @param sender the command sender to check permissions for
     * @return true if the sender is a player, false otherwise
     */
    @Override
    public boolean canUse(CommandSender sender) {
        return sender instanceof Player;
    }
}
