package com.github.pinont.singularitylib.api.runnable;

import com.github.pinont.singularitylib.api.utils.Common;
import com.github.pinont.singularitylib.plugin.CorePlugin;
import io.papermc.paper.threadedregions.scheduler.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class Scheduler {

    private final boolean isFolia = CorePlugin.isFolia();

    private final Server server = Bukkit.getServer();

    private Runner runner;
    private BukkitScheduler bukkitScheduler;
    private AsyncScheduler asyncScheduler;

    public ScheduledTask scheduledTask = null;
    public BukkitTask bukkitTask = null;

    private GlobalRegionScheduler globalScheduler;
    private RegionScheduler regionScheduler;
    private EntityScheduler entityScheduler;

    enum RunnerType {
        SYNC,
        ASYNC,
        GLOBAL,
        REGION,
        ENTITY
    }

    public AsyncScheduler getAsyncScheduler() {
        return asyncScheduler;
    }

    public BukkitScheduler getBukkitScheduler() {
        return bukkitScheduler;
    }

    public GlobalRegionScheduler getGlobalScheduler() {
        return globalScheduler;
    }

    public RegionScheduler getRegionScheduler() {
        return regionScheduler;
    }

    public Object getTask() {
        return isFolia ? scheduledTask : bukkitTask;
    }

    public void runTaskAsync(Runner runner) {
        this.runner = runner;
        if (isFolia) {
            asyncScheduler = server.getAsyncScheduler();
            scheduledTask = asyncScheduler.runNow(Common.plugin, _ -> runner.run());
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTaskAsynchronously(Common.plugin, runner::run);
        }
    }

    public void runTask(Runner runner) {
        this.runner = runner;
        if (isFolia) {
            globalScheduler = server.getGlobalRegionScheduler();
            scheduledTask = globalScheduler.run(Common.plugin, _ -> runner.run());
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTask(Common.plugin, runner::run);
        }
    }

    public void runRepeatingTask(Location location, Runner runner, int delayTicks, long periodTicks, TimeUnit timeUnit) {
        this.runner = runner;
        if (isFolia) {
            regionScheduler = server.getRegionScheduler();
            scheduledTask = regionScheduler.runAtFixedRate(Common.plugin, location, _ -> runner.run(), delayTicks, periodTicks);
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTaskTimer(Common.plugin, runner::run, 0L, periodTicks * timeUnit.toMillis(periodTicks));
        }
    }

    public void runRepeatingTask(World world, Runner runner, int delayTicks, long periodTicks, TimeUnit timeUnit) {
        runRepeatingTask(new Location(world, 0, 0, 0), runner,delayTicks, periodTicks, timeUnit);
    }

    public void runRepeatingTaskAsync(Runner runner, int delayTicks, long periodTicks, TimeUnit timeUnit) {
        if (isFolia) {
            asyncScheduler = server.getAsyncScheduler();
            scheduledTask = asyncScheduler.runAtFixedRate(Common.plugin, _ -> runner.run(), delayTicks, periodTicks, timeUnit);
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTaskTimer(Common.plugin, runner::run, delayTicks, Common.toTicks(periodTicks, timeUnit));
        }
    }

    public void cancelTask() {
        if (isFolia) {
            if (scheduledTask != null) {
                scheduledTask.cancel();
            }
        } else {
            if (bukkitTask != null) {
                bukkitTask.cancel();
            }
        }
    }

    public void cancelAllTasks() {
        if (isFolia) {
            asyncScheduler.cancelTasks(Common.plugin);
        } else {
            bukkitScheduler.cancelTasks(Common.plugin);
        }
    }
}
