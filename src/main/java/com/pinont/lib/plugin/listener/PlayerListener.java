package com.pinont.lib.plugin.listener;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import com.pinont.lib.api.event.ItemExecuteEvent;
import com.pinont.lib.api.items.ItemCreator;
import com.pinont.lib.api.items.ItemInteraction;
import com.pinont.lib.api.ui.Button;
import com.pinont.lib.api.ui.Menu;
import com.pinont.lib.api.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

import static com.pinont.lib.plugin.CorePlugin.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void interaction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!Common.isMainHandEmpty(player) && ItemCreator.isItemHasPersistData(item, "interaction", PersistentDataType.STRING)) {
            if (event.getPlayer().getCooldown(item) > 0) return;
            ItemInteraction itemInteraction;
            try {
                itemInteraction = ItemCreator.getInteraction(item);
            } catch (IllegalArgumentException e) {
                sendInteractionError(player);
                return;
            }
            if (itemInteraction == null) {
                sendInteractionError(player);
                return;
            }
            ItemExecuteEvent itemExecuteEvent = new ItemExecuteEvent(event, item, itemInteraction);
            Bukkit.getPluginManager().callEvent(itemExecuteEvent);
            if (!itemExecuteEvent.isCancelled()) {
                itemExecuteEvent.execute();
                event.setCancelled(itemInteraction.cancelEvent());
            }
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
            sendDebugMessage("Player " + player.getName() + " clicked on " + menu.getTitle());
            for (final Button button : menu.getButtons()) {
                if (button.getSlot() == event.getSlot()) {
                    button.onClick(player);
                }
            }
        }
    }

    @EventHandler
    public void recipeOpen(PlayerRecipeBookClickEvent event) {
        Player player = event.getPlayer();
        sendDebugMessage("Player " + player.getName() + " opened.");
        if (player.isInvulnerable()) {
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasMetadata("god")) {
            player.setGameMode((GameMode) Objects.requireNonNull(player.getMetadata("god").getFirst().value()));
        }
        removePlayerMetadata(player, getInstance(), "Menu", "god");
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removePlayerMetadata(player, getInstance(), "Menu", "DevTool");
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.isInvulnerable()) {
            player.setAllowFlight(player.isInvulnerable());
        }
        removePlayerMetadata(player, getInstance(), "Menu", "DevTool");
    }

    @EventHandler
    public void hungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isInvulnerable()) {
                event.setCancelled(true);
                if (player.getFoodLevel() < 20) {
                    event.setFoodLevel(20);
                }
            }
        }
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
