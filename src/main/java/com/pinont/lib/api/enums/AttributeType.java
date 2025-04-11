package com.pinont.lib.api.utils.enums;

import org.bukkit.attribute.Attribute;


public enum AttributeType {
    pluginMAX_HEALTH("generic.max_health"),
    pluginMAX_ABSORPTION("generic.max_absorption"),
    pluginFOLLOW_RANGE("generic.follow_range"),
    pluginKNOCKBACK_RESISTANCE("generic.knockback_resistance"),
    pluginMOVEMENT_SPEED("generic.movement_speed"),
    pluginATTACK_DAMAGE("generic.attack_damage"),
    pluginARMOR("generic.armor"),
    pluginARMOR_TOUGHNESS("generic.armor_toughness"),
    pluginATTACK_KNOCKBACK("generic.attack_knockback"),
    pluginATTACK_SPEED("generic.attack_speed"),
    pluginLUCK("generic.luck"),
    pluginFLYING_SPEED("generic.flying_speed"),
    JUMP_STRENGTH("horse.jump_strength"),
    SPAWN_REINFORCEMENTS("zombie.spawn_reinforcements"),
    ;

    public final String name;

    AttributeType(String s) {
        this.name = s;
    }

    public Attribute getAttribute() {
        return switch (this) {
            case pluginMAX_HEALTH -> Attribute.MAX_HEALTH;
            case pluginMAX_ABSORPTION -> Attribute.MAX_ABSORPTION;
            case pluginFOLLOW_RANGE -> Attribute.FOLLOW_RANGE;
            case pluginKNOCKBACK_RESISTANCE -> Attribute.KNOCKBACK_RESISTANCE;
            case pluginMOVEMENT_SPEED -> Attribute.MOVEMENT_SPEED;
            case pluginATTACK_DAMAGE -> Attribute.ATTACK_DAMAGE;
            case pluginARMOR -> Attribute.ARMOR;
            case pluginARMOR_TOUGHNESS -> Attribute.ARMOR_TOUGHNESS;
            case pluginATTACK_KNOCKBACK -> Attribute.ATTACK_KNOCKBACK;
            case pluginATTACK_SPEED -> Attribute.ATTACK_SPEED;
            case pluginLUCK -> Attribute.LUCK;
            case pluginFLYING_SPEED -> Attribute.FLYING_SPEED;
            case JUMP_STRENGTH -> Attribute.JUMP_STRENGTH;
            case SPAWN_REINFORCEMENTS -> Attribute.SPAWN_REINFORCEMENTS;
        };
    }
}