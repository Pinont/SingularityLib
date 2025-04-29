package com.pinont.lib.plugin.events;

import com.pinont.lib.Singularity;
import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.ui.Button;
import com.pinont.lib.api.ui.Interaction;
import com.pinont.lib.api.ui.Menu;
import com.pinont.lib.api.utils.Common;
import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
            Interaction interaction;
            try {
                interaction = ItemCreator.getInteraction(player, player.getInventory().getItemInMainHand());
            } catch (IllegalArgumentException e) {
                sendInteractionError(player);
                return;
            }
            if (interaction == null) {
                sendInteractionError(player);
                return;
            }
            if (interaction.getAction().contains(event.getAction())) {
                interaction.execute(player);
            }
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasMetadata("Menu")) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
        if (player.hasMetadata("Menu")) {
            final Menu menu = (Menu) player.getMetadata("Menu").getFirst().value();
            if (menu == null) return;
            CorePlugin.sendDebugMessage("Player " + player.getName() + " clicked on " + menu.getTitle());
            for (final Button button : menu.getButtons()) {
                if (button.getSlot() == event.getSlot()) {
                    button.onClick(player);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        removePlayerMenuMetadata(player, Singularity.getInstance());
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removePlayerMenuMetadata(player, Singularity.getInstance());
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        removePlayerMenuMetadata(player, Singularity.getInstance());
    }

    private void removePlayerMenuMetadata(Player player, Plugin plugin) {
        if (player.hasMetadata("Menu")) {
            player.removeMetadata("Menu", plugin);
        }
    }

    private void sendInteractionError(Player player) {
        sendConsoleMessage("Interaction ID is not valid: " + ItemCreator.getItemPersistData(player.getInventory().getItemInMainHand(), "interaction", PersistentDataType.STRING));
    }

}
