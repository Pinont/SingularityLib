package com.github.pinont.plugin.listener;

import com.github.pinont.singularitylib.api.annotation.AutoRegister;
import com.github.pinont.singularitylib.api.event.PlayerDamageByPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@AutoRegister
public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {
            PlayerDamageByPlayerEvent playerDamageEvent = new PlayerDamageByPlayerEvent(player, damager);
            event.setCancelled(playerDamageEvent.callEvent());
        }
    }

}
