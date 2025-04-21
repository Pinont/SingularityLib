package com.pinont.lib.api.custom.entity;

import org.bukkit.entity.EntityType;

public interface CustomEntity {

    String getName();

    EntityType getType();

    double getHealth();

    int getModelData();

}
