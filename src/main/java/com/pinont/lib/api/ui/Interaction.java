package com.pinont.lib.api.ui;

import com.pinont.lib.enums.WorldEnvironment;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Collections;
import java.util.Set;

public interface Interaction {

    String getName();

    Set<Action> getAction();

    default Set<WorldEnvironment> getExecuteWorldEnvironment() {
        return Collections.singleton(WorldEnvironment.ALL);
    }

    default Set<String> getExecuteInWorlds() {
        return Set.of("*");
    }

    void execute(Player player);

}
