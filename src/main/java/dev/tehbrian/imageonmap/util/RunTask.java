package dev.tehbrian.imageonmap.util;

import dev.tehbrian.imageonmap.ImageOnMap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class RunTask {
    private RunTask() {
    }

    /**
     * See {@link BukkitScheduler#runTask(Plugin, Runnable)} .
     */
    public static void nextTick(Runnable runnable) {
        Bukkit.getScheduler().runTask(ImageOnMap.get(), runnable);
    }

    /**
     * See {@link BukkitScheduler#runTaskLater(Plugin, Runnable, long)} .
     */
    public static void later(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(ImageOnMap.get(), runnable, delay);
    }
}
