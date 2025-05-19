package com.pinont.lib.plugin.events;

import com.pinont.lib.api.manager.FileManager;
import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.util.List;
import java.util.Random;

public class ServerListPingListener implements Listener {

    @EventHandler
    public void serverPing(ServerListPingEvent event) {
        if (CorePlugin.getInstance().getConfig().contains("motds")) {
            List<String> motds = CorePlugin.getInstance().getConfig().getStringList("motds");
            if (motds.isEmpty()) {
                return;
            } event.setMotd(motds.get(new Random().nextInt(motds.size())));
        }
    }

}
