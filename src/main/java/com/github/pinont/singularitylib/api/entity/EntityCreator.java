package com.github.pinont.singularitylib.api.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

/**
 * Builder class for creating and configuring entities before spawning them.
 * Provides a fluent API for setting various entity properties and spawning entities at specific locations.
 */
public class EntityCreator {

    private final EntityType entityType;

    private Entity passenger = null;

    private List<String> ScoreboardTag;


    private boolean isSetFireTicks = false;
    private int fireTicks;

    private boolean isSetGlowing = false;
    private boolean glowing;

    private boolean isSetInvulnerable = false;
    private boolean invulnerable;

    private boolean isSetSilient = false;
    private boolean silent;

    private boolean isSetGravity = false;
    private boolean gravity;

    private boolean isSetSilentGravity = false;
    private boolean persistent;

    private boolean isSetFreezeTicks = false;
    private int freezeTicks;

    private boolean isSetCustomNameVisible = false;
    private boolean customNameVisible;

    private boolean isSetProtalCooldown = false;
    private int portalCooldown;

    private boolean isSetFallingDistance = false;
    private float fallingDistance;

    private boolean isSetRotation = false;
    private float[] rotation = null;

    private boolean isSetVector = false;
    private Vector vector = null;

    private boolean isSetVisualFire = false;
    private boolean visualFire;

    private boolean isSetVisibleByDefault = false;
    private boolean visibleByDefault = true;

    private boolean isSetTicksLived = false;
    private int ticksLived;

    private boolean isSetMaxHealth = false;
    private double maxHealth;

    /**
     * Creates a new EntityCreator for the specified entity type.
     *
     * @param entityType the type of entity to create
     */
    public EntityCreator(EntityType entityType) {
        this.entityType = entityType;
    }

    /**
     * Adds a passenger entity to this entity.
     *
     * @param passenger the entity to add as a passenger
     * @return this EntityCreator for method chaining
     */
    public EntityCreator addPassenger(Entity passenger) {
        this.passenger = passenger;
        return this;
    }

    /**
     * Adds scoreboard tags to the entity.
     *
     * @param ScoreboardTag the scoreboard tags to add
     * @return this EntityCreator for method chaining
     */
    public EntityCreator addScoreboardTag(String... ScoreboardTag) {
        this.ScoreboardTag.addAll(List.of(ScoreboardTag));
        return this;
    }

    /**
     * Sets the maximum health for the entity (only applies to LivingEntity).
     *
     * @param maxHealth the maximum health value
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setMaxHealth(double maxHealth) {
        this.isSetMaxHealth = true;
        this.maxHealth = maxHealth;
        return this;
    }

    /**
     * Sets the number of fire ticks for the entity.
     *
     * @param ticks the number of fire ticks
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setFireTicks(int ticks) {
        this.isSetFireTicks = true;
        this.fireTicks = ticks;
        return this;
    }

    /**
     * Sets whether the entity should glow.
     *
     * @param glowing true if the entity should glow, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setGlowing(boolean glowing) {
        this.isSetGlowing = true;
        this.glowing = glowing;
        return this;
    }

    /**
     * Sets whether the entity is invulnerable to damage.
     *
     * @param invulnerable true if the entity should be invulnerable, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setInvulnerable(boolean invulnerable) {
        this.isSetInvulnerable = true;
        this.invulnerable = invulnerable;
        return this;
    }

    /**
     * Sets whether the entity makes sounds.
     *
     * @param silent true if the entity should be silent, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setSilent(boolean silent) {
        this.isSetSilient = true;
        this.silent = silent;
        return this;
    }

    /**
     * Sets whether the entity is affected by gravity.
     *
     * @param gravity true if the entity should have gravity, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator hasGravity(boolean gravity) {
        this.isSetGravity = true;
        this.gravity = gravity;
        return this;
    }

    /**
     * Sets the velocity vector for the entity.
     *
     * @param vector the velocity vector
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setVelocity(Vector vector) {
        this.isSetVector = true;
        this.vector = vector;
        return this;
    }

    /**
     * Sets whether the entity is persistent (won't despawn).
     *
     * @param persistent true if the entity should be persistent, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setPersistent(Boolean persistent) {
        this.isSetSilentGravity = true;
        this.persistent = persistent;
        return this;
    }

    /**
     * Sets the number of freeze ticks for the entity.
     *
     * @param ticks the number of freeze ticks
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setFreezeTicks(int ticks) {
        this.isSetFreezeTicks = true;
        this.freezeTicks = ticks;
        return this;
    }

    /**
     * Sets whether the entity's custom name is visible.
     *
     * @param visible true if the custom name should be visible, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setCustomNameVisible(Boolean visible) {
        this.isSetCustomNameVisible = true;
        this.customNameVisible = visible;
        return this;
    }

    /**
     * Sets the portal cooldown for the entity.
     *
     * @param ticks the portal cooldown in ticks
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setPortalCooldown(int ticks) {
        this.isSetProtalCooldown = true;
        this.portalCooldown = ticks;
        return this;
    }

    /**
     * Sets the falling distance for the entity.
     *
     * @param distance the falling distance
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setFallingDistance(float distance) {
        this.isSetFallingDistance = true;
        this.fallingDistance = distance;
        return this;
    }

    /**
     * Sets the rotation (yaw and pitch) for the entity.
     *
     * @param yaw the yaw rotation
     * @param pitch the pitch rotation
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setRotation(float yaw, float pitch) {
        this.isSetRotation = true;
        this.rotation = new float[]{yaw, pitch};
        return this;
    }

    /**
     * Sets the number of ticks the entity has been alive.
     *
     * @param ticks the number of ticks lived
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setTicksLived(int ticks) {
        this.isSetTicksLived = true;
        this.ticksLived = ticks;
        return this;
    }

    /**
     * Sets whether the entity is visible by default.
     *
     * @param visible true if the entity should be visible by default, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setVisibleByDefault(boolean visible) {
        this.isSetVisibleByDefault = true;
        this.visibleByDefault = visible;
        return this;
    }

    /**
     * Sets whether the entity has visual fire effect.
     *
     * @param fire true if the entity should have visual fire, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setVisualFire(boolean fire) {
        this.isSetVisualFire = true;
        this.visualFire = fire;
        return this;
    }

    /**
     * Spawns the entity at the specified location with all configured properties.
     *
     * @param location the location to spawn the entity at
     * @return the spawned entity
     */
    public Entity spawn(Location location) {
        Entity entity = Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);
        sendDebugMessage("Spawned " + entityType + " at " + location);
        if (isSetMaxHealth) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setMaxHealth(maxHealth);
                livingEntity.setHealth(maxHealth);
            } else {
                sendDebugMessage("Entity " + entityType + " does not support health setting.");
            }
        }
        if (passenger != null) entity.addPassenger(passenger);
        if (ScoreboardTag != null) {
            for (String tag : ScoreboardTag) {
                entity.addScoreboardTag(tag);
            }
        }
        if (isSetFireTicks) entity.setFireTicks(fireTicks);
        if (isSetGlowing) entity.setGlowing(glowing);
        if (isSetInvulnerable) entity.setInvulnerable(invulnerable);
        if (isSetSilient) entity.setSilent(silent);
        if (isSetGravity) entity.setGravity(gravity);
        if (isSetSilentGravity) entity.setPersistent(persistent);
        if (isSetFreezeTicks) entity.setFreezeTicks(freezeTicks);
        if (isSetCustomNameVisible) entity.setCustomNameVisible(customNameVisible);
        if (isSetProtalCooldown) entity.setPortalCooldown(portalCooldown);
        if (isSetFallingDistance) entity.setFallDistance(fallingDistance);
        if (isSetRotation) {
            entity.setRotation(rotation[0], rotation[1]);
        }
        if (isSetVector) {
            entity.setVelocity(vector);
        }
        if (isSetVisualFire) entity.setVisualFire(visualFire);
        if (isSetVisibleByDefault) {
            entity.setVisibleByDefault(visibleByDefault);
        }
        if (isSetTicksLived) {
            entity.setTicksLived(ticksLived);
        }
        return entity;
    }

    /**
     * Spawns the entity at the specified coordinates in the given world.
     *
     * @param world the world to spawn the entity in
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the spawned entity
     */
    public Entity spawn(World world, double x, double y, double z) {
        return spawn(new Location(world, x, y, z));
    }

    /**
     * Gets the entity type this creator will spawn.
     *
     * @return the entity type
     */
    public EntityType getType() {
        return entityType;
    }

    /**
     * Gets the entity class for the entity type this creator will spawn.
     *
     * @return the entity class
     */
    public Class<? extends Entity> getEntity() {
        return this.entityType.getEntityClass();
    }
}