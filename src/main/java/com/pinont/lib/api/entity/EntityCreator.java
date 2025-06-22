package com.pinont.lib.api.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

import static com.pinont.lib.plugin.CorePlugin.sendDebugMessage;

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

    public EntityCreator(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityCreator addPassenger(Entity passenger) {
        this.passenger = passenger;
        return this;
    }

    public EntityCreator addScoreboardTag(String... ScoreboardTag) {
        this.ScoreboardTag.addAll(List.of(ScoreboardTag));
        return this;
    }

    public EntityCreator setMaxHealth(double maxHealth) {
        this.isSetMaxHealth = true;
        this.maxHealth = maxHealth;
        return this;
    }

    public EntityCreator setFireTicks(int ticks) {
        this.isSetFireTicks = true;
        this.fireTicks = ticks;
        return this;
    }

    public EntityCreator setGlowing(boolean glowing) {
        this.isSetGlowing = true;
        this.glowing = glowing;
        return this;
    }

    public EntityCreator setInvulnerable(boolean invulnerable) {
        this.isSetInvulnerable = true;
        this.invulnerable = invulnerable;
        return this;
    }

    public EntityCreator setSilent(boolean silent) {
        this.isSetSilient = true;
        this.silent = silent;
        return this;
    }

    public EntityCreator hasGravity(boolean gravity) {
        this.isSetGravity = true;
        this.gravity = gravity;
        return this;
    }

    public EntityCreator setVelocity(Vector vector) {
        this.isSetVector = true;
        this.vector = vector;
        return this;
    }

    public EntityCreator setPersistent(Boolean persistent) {
        this.isSetSilentGravity = true;
        this.persistent = persistent;
        return this;
    }

    public EntityCreator setFreezeTicks(int ticks) {
        this.isSetFreezeTicks = true;
        this.freezeTicks = ticks;
        return this;
    }

    public EntityCreator setCustomNameVisible(Boolean visible) {
        this.isSetCustomNameVisible = true;
        this.customNameVisible = visible;
        return this;
    }

    public EntityCreator setPortalCooldown(int ticks) {
        this.isSetProtalCooldown = true;
        this.portalCooldown = ticks;
        return this;
    }

    public EntityCreator setFallingDistance(float distance) {
        this.isSetFallingDistance = true;
        this.fallingDistance = distance;
        return this;
    }

    public EntityCreator setRotation(float yaw, float pitch) {
        this.isSetRotation = true;
        this.rotation = new float[]{yaw, pitch};
        return this;
    }

    public EntityCreator setTicksLived(int ticks) {
        this.isSetTicksLived = true;
        this.ticksLived = ticks;
        return this;
    }

    public EntityCreator setVisibleByDefault(boolean visible) {
        this.isSetVisibleByDefault = true;
        this.visibleByDefault = visible;
        return this;
    }

    public EntityCreator setVisualFire(boolean fire) {
        this.isSetVisualFire = true;
        this.visualFire = fire;
        return this;
    }

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

    public Entity spawn(World world, double x, double y, double z) {
        return spawn(new Location(world, x, y, z));
    }

    public EntityType getType() {
        return entityType;
    }

    public Class<? extends Entity> getEntity() {
        return this.entityType.getEntityClass();
    }
}