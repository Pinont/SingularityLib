package com.pinont.lib.api.creator.items;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Set;

public interface ItemInteraction {

    String getName();

    Set<Action> getAction();

    void execute(Player player);

    default boolean cancelEvent() {
        return true;
    }

}
