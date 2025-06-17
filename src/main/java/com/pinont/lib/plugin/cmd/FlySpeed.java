package com.pinont.lib.plugin.cmd;

import com.pinont.lib.api.command.SimpleCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class FlySpeed implements SimpleCommand {

    @Override
    public String getName() {
        return "flyspeed:fs";
    }

    @Override
    public String description() {
        return "Set Player Flying speed";
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        Player player = (Player) commandSourceStack.getSender();
        if (strings.length == 0) {
            player.sendMessage("Your current fly speed is: " + player.getFlySpeed());
        } else if (strings[0].equalsIgnoreCase("reset")) {
            player.setFlySpeed(0.1f);
            player.sendMessage("§aFly speed reset to default.");
        } else {
            try {
                float speed = Float.parseFloat(strings[0])/10;
                if (speed > 1f || speed < -1f) {
                    player.sendMessage(ChatColor.RED + "Speed must be between -10 and 10.");
                    return;
                }
                player.setFlySpeed(speed);
                player.sendMessage("§aFly speed set to " + speed*10);
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid speed value.");
            }
        }
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender instanceof Player && sender.hasPermission("singularity.flyspeed.use");
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] strings) {
        if (strings.length == 1) {
            return List.of("<speed>|reset");
        } return List.of();
    }
}