package com.pinont.lib.enums;

import org.bukkit.World;

import java.util.List;

public enum WorldEnvironment {
    CUSTOM,
    NORMAL,
    NETHER,
    THE_END,
    ALL;

    public List<World.Environment> getWorldEnvironment() {
        return switch (this) {
            case CUSTOM -> List.of(World.Environment.CUSTOM);
            case NORMAL -> List.of(World.Environment.NORMAL);
            case NETHER -> List.of(World.Environment.NETHER);
            case THE_END -> List.of(World.Environment.THE_END);
            case ALL -> List.of(World.Environment.CUSTOM, World.Environment.NETHER, World.Environment.NORMAL, World.Environment.THE_END);
        };
    }

    public static WorldEnvironment fromWorldEnvironment(World.Environment environment) {
        return switch (environment) {
            case CUSTOM -> CUSTOM;
            case NORMAL -> NORMAL;
            case NETHER -> NETHER;
            case THE_END -> THE_END;
        };
    }
}
