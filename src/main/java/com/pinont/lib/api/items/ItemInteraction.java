package com.pinont.lib.api.items;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Set;

public interface ItemInteraction {

    default int removeItemAmountOnExecute() {
        return 0;
    }

    String getName();

    Set<Action> getAction();

    void execute(Player player);

    default boolean cancelEvent() {
        return true;
    }

}
