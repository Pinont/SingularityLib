package com.pinont.lib.api.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Set;

public interface Interaction {

    String getName();

    Set<Action> getAction();

    void execute(Player player);

}
