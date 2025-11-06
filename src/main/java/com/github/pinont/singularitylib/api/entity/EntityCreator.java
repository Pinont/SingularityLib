package com.github.pinont.singularitylib.api.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

/**
 * Builder class for creating and configuring entities before spawning them.
 * Provides a fluent API for setting various entity properties and spawning entities at specific locations.
 */
public class EntityCreator {

    private final EntityType entityType;

    private HashMap<String, Object> properties;

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
        properties.put("passenger", passenger);
        return this;
    }

    /**
     * Adds scoreboard tags to the entity.
     *
     * @param ScoreboardTag the scoreboard tags to add
     * @return this EntityCreator for method chaining
     */
    public EntityCreator addScoreboardTag(String... ScoreboardTag) {
        properties.put("scoreboardTag", ScoreboardTag);
        return this;
    }

    /**
     * Sets the maximum health for the entity (only applies to LivingEntity).
     *
     * @param maxHealth the maximum health value
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setMaxHealth(double maxHealth) {
        properties.put("maxHealth", maxHealth);
        return this;
    }

    /**
     * Sets the number of fire ticks for the entity.
     *
     * @param ticks the number of fire ticks
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setFireTicks(int ticks) {
        properties.put("fireTicks", ticks);
        return this;
    }

    /**
     * Sets whether the entity should glow.
     *
     * @param glowing true if the entity should glow, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setGlowing(boolean glowing) {
        properties.put("glowing", glowing);
        return this;
    }

    /**
     * Sets whether the entity is invulnerable to damage.
     *
     * @param invulnerable true if the entity should be invulnerable, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setInvulnerable(boolean invulnerable) {
        properties.put("invulnerable", invulnerable);
        return this;
    }

    /**
     * Sets whether the entity makes sounds.
     *
     * @param silent true if the entity should be silent, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setSilent(boolean silent) {
        properties.put("silent", silent);
        return this;
    }

    /**
     * Sets whether the entity is affected by gravity.
     *
     * @param gravity true if the entity should have gravity, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator hasGravity(boolean gravity) {
        properties.put("gravity", gravity);
        return this;
    }

    /**
     * Sets the velocity vector for the entity.
     *
     * @param vector the velocity vector
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setVelocity(Vector vector) {
        properties.put("vector", vector);
        return this;
    }

    /**
     * Sets whether the entity is persistent (won't despawn).
     *
     * @param persistent true if the entity should be persistent, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setPersistent(Boolean persistent) {
        properties.put("persistent", persistent);
        return this;
    }

    /**
     * Sets the number of freeze ticks for the entity.
     *
     * @param ticks the number of freeze ticks
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setFreezeTicks(int ticks) {
        properties.put("freezeTicks", ticks);
        return this;
    }

    /**
     * Sets whether the entity's custom name is visible.
     *
     * @param visible true if the custom name should be visible, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setCustomNameVisible(Boolean visible) {
        properties.put("customNameVisible", visible);
        return this;
    }

    /**
     * Sets the portal cooldown for the entity.
     *
     * @param ticks the portal cooldown in ticks
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setPortalCooldown(int ticks) {
        properties.put("portalCooldown", ticks);
        return this;
    }

    /**
     * Sets the falling distance for the entity.
     *
     * @param distance the falling distance
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setFallingDistance(float distance) {
        properties.put("fallingDistance", distance);
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
        properties.put("rotation", new float[]{yaw, pitch});
        return this;
    }

    /**
     * Sets the number of ticks the entity has been alive.
     *
     * @param ticks the number of ticks lived
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setTicksLived(int ticks) {
        properties.put("ticksLived", ticks);
        return this;
    }

    /**
     * Sets whether the entity is visible by default.
     *
     * @param visible true if the entity should be visible by default, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setVisibleByDefault(boolean visible) {
        properties.put("visibleByDefault", visible);
        return this;
    }

    /**
     * Sets whether the entity has visual fire effect.
     *
     * @param fire true if the entity should have visual fire, false otherwise
     * @return this EntityCreator for method chaining
     */
    public EntityCreator setVisualFire(boolean fire) {
        properties.put("visualFire", fire);
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
        if (properties != null) return entity;
        if (properties.containsKey("maxHealth")) {
            double maxHealth = (double) properties.get("maxHealth");
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setMaxHealth(maxHealth);
                livingEntity.setHealth(maxHealth);
            } else {
                sendDebugMessage("Entity " + entityType + " does not support health setting.");
            }
        }
        if (properties.containsKey("passenger")) {
            Entity passenger = (Entity) properties.get("passenger");
            entity.addPassenger(passenger);
        }
        if (properties.containsKey("scoreboardTag")) {
            String[] tags = (String[]) properties.get("scoreboardTag");
            for (String tag : tags) {
                entity.addScoreboardTag(tag);
            }
        }
        if (properties.containsKey("fireTicks")) {
            int ticks = (int) properties.get("fireTicks");
            entity.setFireTicks(ticks);
        }
        if (properties.containsKey("glowing")) {
            boolean glowing = (boolean) properties.get("glowing");
            entity.setGlowing(glowing);
        }
        if (properties.containsKey("invulnerable")) {
            boolean invulnerable = (boolean) properties.get("invulnerable");
            entity.setInvulnerable(invulnerable);
        }
        if (properties.containsKey("silent")) {
            boolean silent = (boolean) properties.get("silent");
            entity.setSilent(silent);
        }
        if (properties.containsKey("gravity")) {
            boolean gravity = (boolean) properties.get("gravity");
            entity.setGravity(gravity);
        }
        if (properties.containsKey("vector")) {
            Vector vector = (Vector) properties.get("vector");
            entity.setVelocity(vector);
        }
        if (properties.containsKey("persistent")) {
            boolean persistent = (boolean) properties.get("persistent");
            entity.setPersistent(persistent);
        }
        if (properties.containsKey("freezeTicks")) {
            int ticks = (int) properties.get("freezeTicks");
            entity.setFreezeTicks(ticks);
        }
        if (properties.containsKey("customNameVisible")) {
            boolean visible = (boolean) properties.get("customNameVisible");
            entity.setCustomNameVisible(visible);
        }
        if (properties.containsKey("portalCooldown")) {
            int ticks = (int) properties.get("portalCooldown");
            entity.setPortalCooldown(ticks);
        }
        if (properties.containsKey("fallingDistance")) {
            float distance = (float) properties.get("fallingDistance");
            entity.setFallDistance(distance);
        }
        if (properties.containsKey("rotation")) {
            float[] rotation = (float[]) properties.get("rotation");
            entity.setRotation(rotation[0], rotation[1]);
        }
        if (properties.containsKey("ticksLived")) {
            int ticks = (int) properties.get("ticksLived");
            entity.setTicksLived(ticks);
        }
        if (properties.containsKey("visibleByDefault")) {
            boolean visible = (boolean) properties.get("visibleByDefault");
            entity.setVisibleByDefault(visible);
        }
        if (properties.containsKey("visualFire")) {
            boolean fire = (boolean) properties.get("visualFire");
            entity.setVisualFire(fire);
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