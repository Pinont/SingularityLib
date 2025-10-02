package io.github.pinont.singularitylib.api.enums;

import org.bukkit.attribute.Attribute;

/**
 * Enumeration of attribute types available in the game.
 * Each attribute type represents a specific characteristic that can be applied to entities.
 */
public enum AttributeType {
    /**
     * Maximum health attribute.
     */
    MAX_HEALTH("generic.max_health"),
    /**
     * Maximum absorption attribute.
     */
    MAX_ABSORPTION("generic.max_absorption"),
    /**
     * Follow range attribute for mobs.
     */
    FOLLOW_RANGE("generic.follow_range"),
    /**
     * Knockback resistance attribute.
     */
    KNOCKBACK_RESISTANCE("generic.knockback_resistance"),
    /**
     * Movement speed attribute.
     */
    MOVEMENT_SPEED("generic.movement_speed"),
    /**
     * Attack damage attribute.
     */
    ATTACK_DAMAGE("generic.attack_damage"),
    /**
     * Armor attribute.
     */
    ARMOR("generic.armor"),
    /**
     * Armor toughness attribute.
     */
    ARMOR_TOUGHNESS("generic.armor_toughness"),
    /**
     * Attack knockback attribute.
     */
    ATTACK_KNOCKBACK("generic.attack_knockback"),
    /**
     * Attack speed attribute.
     */
    ATTACK_SPEED("generic.attack_speed"),
    /**
     * Luck attribute.
     */
    LUCK("generic.luck"),
    /**
     * Flying speed attribute.
     */
    FLYING_SPEED("generic.flying_speed"),
    /**
     * Jump strength attribute for horses.
     */
    JUMP_STRENGTH("horse.jump_strength"),
    /**
     * Spawn reinforcements attribute for zombies.
     */
    SPAWN_REINFORCEMENTS("zombie.spawn_reinforcements"),
    ;

    /**
     * The internal name of the attribute.
     */
    public final String name;

    /**
     * Constructs an AttributeType with the specified name.
     *
     * @param s the internal name of the attribute
     */
    AttributeType(String s) {
        this.name = s;
    }

    /**
     * Gets the corresponding Bukkit Attribute for this AttributeType.
     *
     * @return the Bukkit Attribute that corresponds to this AttributeType
     */
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