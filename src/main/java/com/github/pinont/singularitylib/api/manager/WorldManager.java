package com.github.pinont.singularitylib.api.manager;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

import static com.github.pinont.singularitylib.plugin.CorePlugin.getInstance;

public final class WorldManager {

    private final String worldName;

    private static final ConfigManager worldConfig = new ConfigManager("devTools", "worlds.yml");

    public WorldManager(String worldName) {
        this.worldName = worldName;
    }

    public void create(WorldType worldType, World.Environment environment, boolean generateStructures, int borderSize, Difficulty difficulty) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.type(worldType);
        worldCreator.generateStructures(generateStructures);
        worldCreator.environment(environment);
        createWorld(borderSize, difficulty, worldCreator);
    }

    public void create(WorldType worldType, World.Environment environment, boolean generateStructures, int borderSize, Difficulty difficulty, long seed) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.type(worldType);
        worldCreator.generateStructures(generateStructures);
        worldCreator.environment(environment);
        worldCreator.seed(seed);
        createWorld(borderSize, difficulty, worldCreator);
    }

    private void createWorld(int borderSize, Difficulty difficulty, WorldCreator worldCreator) {
        World world = Bukkit.createWorld(worldCreator);
        assert world != null;
        world.setDifficulty(difficulty);
        world.setGameRuleValue("doMobSpawning", "false");
        WorldBorder border = world.getWorldBorder();
        border.setSize(borderSize);
        world.setMetadata("loader", new FixedMetadataValue(getInstance(), getInstance().getName()));
        setWorldConfig();
    }

    public void setWorldConfig() {
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        worldConfig.set(worldName + ".name", worldName);
        worldConfig.set(worldName + ".seed", world.getSeed());
        worldConfig.set(worldName + ".borderSize", world.getWorldBorder().getSize());
        worldConfig.set(worldName + ".worldType", Objects.requireNonNull(world.getWorldType()).getName());
        worldConfig.set(worldName + ".spawnLocation.world", Objects.requireNonNull(world.getSpawnLocation().getWorld()).getName());
        worldConfig.set(worldName + ".spawnLocation.x", world.getSpawnLocation().getX());
        worldConfig.set(worldName + ".spawnLocation.y", world.getSpawnLocation().getY());
        worldConfig.set(worldName + ".spawnLocation.z", world.getSpawnLocation().getZ());
        worldConfig.set(worldName + ".generateStructures", world.canGenerateStructures());
        worldConfig.set(worldName + ".environment", world.getEnvironment().getId());
        worldConfig.set(worldName + ".difficulty", world.getDifficulty().toString());
        for (String key : world.getGameRules()) {
            String value = world.getGameRuleValue(key);
            if (value != null) {
                worldConfig.set(worldName + ".gameRule." + key, value);
            }
        }
        worldConfig.saveConfig();
    }

    public void saveWorld() {
        setWorldConfig();
    }

    public static void load(String worldName) {
        Bukkit.createWorld(WorldCreator.name(worldName));
    }

    public static void unload(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, true);
        }
    }

    public static Boolean delete(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (worldConfig.get(worldName) != null) {
            if (world == null) {
                world = Bukkit.createWorld(WorldCreator.name(worldName));
            }
            assert world != null;
            world.removeMetadata("loader", getInstance());
            world.getWorldFolder().deleteOnExit();
            Bukkit.unloadWorld(world, false);
        } else {
            return false;
        }
        worldConfig.set(worldName, null);
        worldConfig.saveConfig();
        return true;
    }

    public static World getWorld(String worldName) {
        if (Bukkit.getWorld(worldName) == null && worldConfig.get(worldName) != null) {
            return Bukkit.createWorld(WorldCreator.name(worldName));
        }
        return null;
    }

    public static void autoLoadWorlds() {
        if (ConfigManager.isExists("devTools", "worlds.yml"))
            for (String worldName : worldConfig.getConfig().getKeys(false)) {
                load(worldName);
                World world = Bukkit.getWorld(worldName);
                assert world != null;
                world.setMetadata("loader", new FixedMetadataValue(getInstance(), getInstance().getName()));
            }
    }

//    public static void reloadWorlds() {
//        World[] worlds = Bukkit.getWorlds().toArray(World[]::new);
//        for (World world : worlds) {
//            assert world != null;
//            setWorldFromConfig(world);
//        }
//    }

//    public static void importWorld(String name) {
//        Bukkit.getLogger().info("Importing world " + name);
//        World world = Bukkit.createWorld(WorldCreator.name(name));
//    }

    public static void setWorldFromConfig(World world) {
        String worldName = world.getName();
        ConfigurationSection section = worldConfig.getConfig().getConfigurationSection(worldName);
        assert section != null;
        ConfigurationSection gameruleSection = section.getConfigurationSection("gameRule");
        world.getWorldBorder().setSize(section.getDouble("borderSize"));
        Location spawnLocation = new Location(
                Bukkit.getWorld(Objects.requireNonNull(section.getString("spawnLocation.world"))),
                section.getDouble("spawnLocation.x"),
                section.getDouble("spawnLocation.y"),
                section.getDouble("spawnLocation.z")
        );
        world.setSpawnLocation(spawnLocation);
        world.setDifficulty(Difficulty.valueOf(Objects.requireNonNull(section.getString("difficulty"))));
        for (String key : gameruleSection.getKeys(false)) {
            String value = gameruleSection.getString(key);
            if (value != null) {
                world.setGameRuleValue(key, value);
            }
        }
    }
}