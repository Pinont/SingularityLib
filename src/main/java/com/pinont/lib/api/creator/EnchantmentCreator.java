package com.pinont.lib.api.creator;

import com.pinont.lib.api.command.SimpleCommand;
import com.pinont.lib.api.utils.Common;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.pinont.lib.plugin.CorePlugin.getInstance;
import static com.pinont.lib.plugin.CorePlugin.sendConsoleMessage;

public class EnchantmentCreator implements SimpleCommand {

    private static final Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess
            .registryAccess()
            .getRegistry(RegistryKey.ENCHANTMENT);

    private final List<Enchant> enchants = new ArrayList<>();

    public List<Enchant> getEnchants() {
        return enchants;
    }

    public void addEnchant(Enchant enchantment) {
        enchants.add(enchantment);
    }

    public Enchant create(Enchant enchantment) {
        return enchantment;
    }

    @Override
    public String getName() {
        return "enchant";
    }

    @Override
    public String description() {
        return "Enchant Item";
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getSender() instanceof Player)) sendConsoleMessage(ChatColor.RED + "You must be a player to use this command!");
        Player player = (Player) commandSourceStack.getSender();
        if (args.length < 2) {
            player.sendMessage("§cUsage: /enchant <player> <enchantment>");
            return;
        }
        String targetName = args[0];
        String enchantmentName = args[1];
        if (!enchantmentName.contains(":") || enchantmentName.split(":").length != 2) {
            player.sendMessage("§cEnchantment must be in the format <namespace>:<name>");
            return;
        }
        int enchantmentLevel = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        switch (args[0]) {
            case "@a" -> {
                for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                    enchantItem(player, enchantmentName, enchantmentLevel, target);
                }
            }
            case "@s" -> {
                enchantItem(player, enchantmentName, enchantmentLevel, player);
            }
            case "@r" -> {
                List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
                Player target = players.get((int) (Math.random() * players.size()));
                enchantItem(player, enchantmentName, enchantmentLevel, target);
            }
            default -> {
                Player target = Bukkit.getPlayer(targetName);
                if (target == null) {
                    player.sendMessage("§cPlayer not found");
                    return;
                }
                enchantItem(player, enchantmentName, enchantmentLevel, target);
            }
        }
    }

    private void enchantItem(Player commandSender, String enchantmentName, int enchantLevel, Player target) {
        Enchantment enchantment = getEnchantment(Arrays.stream(enchantmentName.split(":")).toList().get(0), Arrays.stream(enchantmentName.split(":")).toList().get(1));
        if (enchantment != null) {
            target.getInventory().getItemInMainHand().addUnsafeEnchantment(enchantment, enchantLevel);
            target.sendMessage(ChatColor.WHITE + "Applied enchantment " + ChatColor.GRAY + Common.normalizeStringName(enchantment.getKey().getKey()) + " " + Common.IntegerToRomanNumeral(enchantLevel) + ChatColor.WHITE + " to " + ChatColor.YELLOW + target.getName() + ChatColor.WHITE + "'s item.");
        } else {
            commandSender.sendMessage(ChatColor.RED + "Could not find enchantment '" + enchantmentName +  "' from enchantment registry");
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        switch (args.length) {
            case 0,1 -> {
                List<String> mentionList = new ArrayList<>();
                mentionList.addAll(List.of("@a", "@s", "@r"));
                mentionList.addAll(Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
                return mentionList;
            }
            case 2 -> {
                List<String> suggestions = new ArrayList<>();

                // Add Minecraft vanilla enchantments
                suggestions.addAll(Arrays.stream(Enchantment.values()).map(enchantment -> enchantment.getKey().getNamespace() + ":" + enchantment.getKey().getKey()).toList());

                // Add custom enchantments
                suggestions.addAll(enchants.stream()
                        .map(enchant -> enchant.getNamespace() + ":" + enchant.getName())
                        .toList());

                return suggestions;
            }
            default -> {
                return List.of();
            }
        }
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender instanceof Player;
    }

    public interface Enchant {
        default String getNamespace() {
            return getInstance().getName();
        }

        String getName();

        String getDescription();

        int getMaxLevel();

        int getAnvilCost();

        TagKey<ItemType> getSupportItem();

        int getFoundRate();

        default EnchantmentRegistryEntry.EnchantmentCost getMinimumCost() {
            return EnchantmentRegistryEntry.EnchantmentCost.of(1, 1);
        }

        default EnchantmentRegistryEntry.EnchantmentCost getMaximumCost() {
            return EnchantmentRegistryEntry.EnchantmentCost.of(3, 1);
        }

        EquipmentSlotGroup getActiveSlotGroup();
    }

    public static Enchantment getEnchantment(String namespace, String name) {
        Enchantment enchantment;
        try {
            enchantment = enchantmentRegistry.get(EnchantmentKeys.create(Key.key(namespace + ":" + name)));
        } catch (IllegalArgumentException e) {
            return null;
        }
        return enchantment;
    }
}
