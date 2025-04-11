package com.pinont.lib.enums;

import org.bukkit.attribute.Attribute;


public enum AttributeType {
    MAX_HEALTH("generic.max_health"),
    MAX_ABSORPTION("generic.max_absorption"),
    FOLLOW_RANGE("generic.follow_range"),
    KNOCKBACK_RESISTANCE("generic.knockback_resistance"),
    MOVEMENT_SPEED("generic.movement_speed"),
    ATTACK_DAMAGE("generic.attack_damage"),
    ARMOR("generic.armor"),
    ARMOR_TOUGHNESS("generic.armor_toughness"),
    ATTACK_KNOCKBACK("generic.attack_knockback"),
    ATTACK_SPEED("generic.attack_speed"),
    LUCK("generic.luck"),
    FLYING_SPEED("generic.flying_speed"),
    JUMP_STRENGTH("horse.jump_strength"),
    SPAWN_REINFORCEMENTS("zombie.spawn_reinforcements"),
    ;

    public final String name;

    AttributeType(String s) {
        this.name = s;
    }

    public Attribute getAttribute() {
        return switch (this) {
            case MAX_HEALTH -> Attribute.MAX_HEALTH;
            case MAX_ABSORPTION -> Attribute.MAX_ABSORPTION;
            case FOLLOW_RANGE -> Attribute.FOLLOW_RANGE;
            case KNOCKBACK_RESISTANCE -> Attribute.KNOCKBACK_RESISTANCE;
            case MOVEMENT_SPEED -> Attribute.MOVEMENT_SPEED;
            case ATTACK_DAMAGE -> Attribute.ATTACK_DAMAGE;
            case ARMOR -> Attribute.ARMOR;
            case ARMOR_TOUGHNESS -> Attribute.ARMOR_TOUGHNESS;
            case ATTACK_KNOCKBACK -> Attribute.ATTACK_KNOCKBACK;
            case ATTACK_SPEED -> Attribute.ATTACK_SPEED;
            case LUCK -> Attribute.LUCK;
            case FLYING_SPEED -> Attribute.FLYING_SPEED;
            case JUMP_STRENGTH -> Attribute.JUMP_STRENGTH;
            case SPAWN_REINFORCEMENTS -> Attribute.SPAWN_REINFORCEMENTS;
        };
    }
}