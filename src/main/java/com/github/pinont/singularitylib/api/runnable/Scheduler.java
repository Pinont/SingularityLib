package com.github.pinont.singularitylib.api.runnable;

import com.github.pinont.singularitylib.api.utils.Common;
import com.github.pinont.singularitylib.plugin.CorePlugin;
import io.papermc.paper.threadedregions.scheduler.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

/**
 * Platform-agnostic task scheduler facade. Picks Folia schedulers when running
 * under Folia and the Bukkit scheduler otherwise.
 *
 * <h2>Tick semantics</h2>
 * Unless a method is explicitly named {@code ...Async} with a {@link TimeUnit}
 * parameter, all {@code delay} and {@code period} values are interpreted as
 * <b>server ticks</b> (20 ticks = 1 second at normal rate) on both Paper and
 * Folia paths:
 * <ul>
 *     <li>Folia's {@code runAtFixedRate} variants take ticks natively.</li>
 *     <li>The Bukkit scheduler's {@code runTaskTimer} takes ticks natively.</li>
 * </ul>
 * The async repeating variant interprets its delay/period in the supplied
 * {@link TimeUnit}: on Folia they are forwarded to {@code AsyncScheduler}
 * (which is time-based), on Paper they are converted to ticks via
 * {@link Common#toTicks(long, TimeUnit)} for {@code runTaskTimer}.
 */
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

    public void runTaskAsync(Plugin plugin, Runner runner) {
        this.runner = runner;
        if (isFolia) {
            asyncScheduler = server.getAsyncScheduler();
            scheduledTask = asyncScheduler.runNow(plugin, _ -> runner.run());
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTaskAsynchronously(plugin, runner::run);
        }
    }

    public void runTask(Plugin plugin, Runner runner) {
        this.runner = runner;
        if (isFolia) {
            globalScheduler = server.getGlobalRegionScheduler();
            scheduledTask = globalScheduler.run(plugin, _ -> runner.run());
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTask(plugin, runner::run);
        }
    }

    /**
     * Runs a task repeatedly on the region owning the given location (Folia)
     * or on the main thread (Paper).
     *
     * @param plugin     the plugin that owns the task
     * @param location   the location whose region executes the task (Folia only)
     * @param runner     the task to run
     * @param delayTicks initial delay in server ticks before the first run
     * @param periodTicks period between runs in server ticks
     * @param timeUnit   ignored: kept for source compatibility; both delay and
     *                   period are always interpreted in ticks
     */
    public void runRepeatingTask(Plugin plugin, Location location, Runner runner, int delayTicks, long periodTicks, TimeUnit timeUnit) {
        this.runner = runner;
        if (isFolia) {
            regionScheduler = server.getRegionScheduler();
            scheduledTask = regionScheduler.runAtFixedRate(plugin, location, _ -> runner.run(), delayTicks, periodTicks);
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTaskTimer(plugin, runner::run, delayTicks, periodTicks);
        }
    }

    /**
     * Runs a task repeatedly in the given world (at its spawn chunk origin on
     * Folia) or on the main thread (Paper). See the class Javadoc for tick semantics.
     *
     * @param plugin      the plugin that owns the task
     * @param world       the world whose region executes the task (Folia only)
     * @param runner      the task to run
     * @param delayTicks  initial delay in server ticks
     * @param periodTicks period between runs in server ticks
     * @param timeUnit    ignored: kept for source compatibility; ticks only
     */
    public void runRepeatingTask(Plugin plugin, World world, Runner runner, int delayTicks, long periodTicks, TimeUnit timeUnit) {
        new Scheduler().runRepeatingTask(plugin, new Location(world, 0, 0, 0), runner, delayTicks, periodTicks, timeUnit);
    }

    /**
     * Runs a task repeatedly off the main thread / region threads.
     * Here delay and period are interpreted in the supplied {@link TimeUnit}
     * on both platforms: Folia's async scheduler is time-based natively, while
     * the Paper path converts to ticks via {@link Common#toTicks(long, TimeUnit)}.
     *
     * @param plugin   the plugin that owns the task
     * @param runner   the task to run
     * @param delay    initial delay before the first run
     * @param period   period between runs
     * @param timeUnit unit of {@code delay} and {@code period}; must not be null
     */
    public void runRepeatingTaskAsync(Plugin plugin, Runner runner, int delay, long period, TimeUnit timeUnit) {
        if (isFolia) {
            asyncScheduler = server.getAsyncScheduler();
            scheduledTask = asyncScheduler.runAtFixedRate(plugin, _ -> runner.run(), delay, period, timeUnit);
        } else {
            bukkitScheduler = server.getScheduler();
            bukkitTask = bukkitScheduler.runTaskTimer(plugin, runner::run, Common.toTicks(delay, timeUnit), Common.toTicks(period, timeUnit));
        }
    }

    /**
     * Cancels the single task started by this Scheduler instance, if any.
     */
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

    /**
     * Cancels every task owned by the plugin across all schedulers used by
     * this class: on Paper the whole Bukkit scheduler; on Folia the async and
     * global-region schedulers. Region-scheduler tasks have no bulk cancel API
     * in Folia/Paper 26.x — they must be cancelled individually through their
     * {@link ScheduledTask} handles (see {@link #cancelTask()}).
     * Schedulers never touched by this instance are skipped via null guards.
     *
     * @param plugin the plugin whose tasks should be cancelled
     */
    public void cancelAllTasks(Plugin plugin) {
        if (isFolia) {
            if (asyncScheduler == null) {
                asyncScheduler = server.getAsyncScheduler();
            }
            asyncScheduler.cancelTasks(plugin);
            if (globalScheduler == null) {
                globalScheduler = server.getGlobalRegionScheduler();
            }
            globalScheduler.cancelTasks(plugin);
        } else {
            if (bukkitScheduler == null) {
                bukkitScheduler = server.getScheduler();
            }
            bukkitScheduler.cancelTasks(plugin);
        }
    }
}
