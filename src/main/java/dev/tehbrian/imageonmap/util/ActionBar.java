package dev.tehbrian.imageonmap.util;

import dev.tehbrian.imageonmap.ImageOnMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class to send action bar messages to players.
 */
public final class ActionBar {
    private ActionBar() {
    }

    private static final Map<UUID, Component> activeMessages = new ConcurrentHashMap<>();

    private static final Runnable updater = () -> {
        for (Map.Entry<UUID, Component> entry : activeMessages.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                player.sendActionBar(entry.getValue());
            }
        }
    };
    private static BukkitTask updaterTask;

    @Deprecated
    public static void showPermanentMessage(Player player, String message) {
        showPermanentMessage(player, Component.text(message));
    }

    /**
     * Show a constant message to the given player.
     *
     * @param player  the player
     * @param message the message to display
     */
    public static void showPermanentMessage(Player player, Component message) {
        activeMessages.put(player.getUniqueId(), message);
        player.sendActionBar(message);
        checkUpdaterTaskState();
    }

    /**
     * Removes the current action bar message shown to the given player.
     * <p>
     * If {@code instant}, the message will be removed instantly. If not, the message will dismiss progressively.
     * It may be displayed for a few more seconds.
     *
     * @param player  the player
     * @param instant whether to instantly remove the message
     */
    public static void removeMessage(Player player, boolean instant) {
        activeMessages.remove(player.getUniqueId());
        if (instant) {
            player.sendActionBar(Component.empty());
        }
        checkUpdaterTaskState();
    }

    /**
     * Removes the action bar message shown to the given player.
     *
     * @param player the player
     */
    public static void removeMessage(Player player) {
        removeMessage(player, false);
    }

    /**
     * Checks if the task sending the permanent actions message needs to run and is not running, or
     * is useless and running. Stops or launches the task if needed.
     */
    private static void checkUpdaterTaskState() {
        int count = activeMessages.size();

        if (count == 0 && updaterTask != null) {
            updaterTask.cancel();
            updaterTask = null;
        } else if (count > 0 && updaterTask == null) {
            updaterTask = Bukkit.getScheduler().runTaskTimer(ImageOnMap.get(), updater, 20, 30);
        }
    }
}
