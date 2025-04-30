package com.pinont.lib.plugin.events;

import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.ui.Button;
import com.pinont.lib.api.ui.ItemInteraction;
import com.pinont.lib.api.ui.Menu;
import com.pinont.lib.api.utils.Common;
import com.pinont.lib.plugin.CorePlugin;
import com.pinont.lib.plugin.DevTool;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import static com.pinont.lib.plugin.CorePlugin.sendConsoleMessage;

public class PlayerListener implements Listener {

    @EventHandler
    public void interaction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Common.isMainHandEmpty(player) && ItemCreator.isItemHasPersistData(player.getInventory().getItemInMainHand(), "interaction", PersistentDataType.STRING)) {
            event.setCancelled(true);
            ItemInteraction itemInteraction;
            try {
                itemInteraction = ItemCreator.getInteraction(player, player.getInventory().getItemInMainHand());
            } catch (IllegalArgumentException e) {
                sendInteractionError(player);
                return;
            }
            if (itemInteraction == null) {
                sendInteractionError(player);
                return;
            }
            if (itemInteraction.getAction().contains(event.getAction())) {
                itemInteraction.execute(player);
            }
        }
    }

    @EventHandler
    public void sendChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("devTool")) {
            DevTool.WorldCreatorContent worldCreatorContent = (DevTool.WorldCreatorContent) player.getMetadata("devTool").getFirst().value();
            if (worldCreatorContent.getInputContent() == null) {
                player.sendMessage(ChatColor.RED + "Seem like devTool World creator has occur an error.");
                return;
            }
            event.setCancelled(true);
            switch (worldCreatorContent.getInputContent()) {
                case "worldName" : {
                    new DevTool().showWorldCreator(player, event.getMessage(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    break;
                }
                case "worldBorder" : {
                    try {
                        int borderSize = Integer.parseInt(event.getMessage());
                        if (borderSize <= 0) {
                            player.sendMessage(ChatColor.RED + "World border size must be greater than 0");
                            new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                            return;
                        }
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), borderSize, worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "World border size must be a number.");
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    }
                    break;
                }
                case "worldSeed" : {
                    try {
                        long seed = Long.parseLong(event.getMessage());
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), seed);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "World border size must be a number.");
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    }
                    break;
                }
            }
            removePlayerMetadata(player, CorePlugin.getInstance(), "devTool");
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasMetadata("Menu")) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
        if (player.hasMetadata("Menu")) {
            event.setCancelled(true);
            final Menu menu = (Menu) player.getMetadata("Menu").getFirst().value();
            if (menu == null) return;
            CorePlugin.sendDebugMessage("Player " + player.getName() + " clicked on " + menu.getTitle());
            for (final Button button : menu.getButtons()) {
                if (button.getSlot() == event.getSlot()) {
                    button.onClick(player);
                }
            }
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        removePlayerMetadata(player, CorePlugin.getInstance(), "Menu");
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removePlayerMetadata(player, CorePlugin.getInstance(), "Menu", "DevTool");
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        removePlayerMetadata(player, CorePlugin.getInstance(), "Menu", "DevTool");
    }

    private void removePlayerMetadata(Player player, Plugin plugin, String... keys) {
        for (String metaKey : keys) {
            if (player.hasMetadata(metaKey)) {
                player.removeMetadata(metaKey, plugin);
            }
        }
    }

    private void sendInteractionError(Player player) {
        sendConsoleMessage("Interaction ID is not valid: " + ItemCreator.getItemPersistData(player.getInventory().getItemInMainHand(), "interaction", PersistentDataType.STRING));
    }

}
