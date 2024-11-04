package dev.tehbrian.imageonmap.util;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class ItemUtil {
    private ItemUtil() {
    }

    /**
     * Simulates the player consuming the given {@code ItemStack}.
     * <p>
     * This decreases the item's stack size by one or replaces it with air
     * if nothing is left. If the player is in creative mode, the item stack is
     * left unchanged.
     *
     * @param player the player
     * @param item   the item stack to consume
     */
    public static void consumeItem(Player player, ItemStack item) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (item.getAmount() == 1) {
            player.getInventory().removeItem(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }

    /**
     * Emulates the behavior of the /give command.
     *
     * @param player the player to give the item to
     * @param item   the item to give to the player
     * @return whether the player received the item in their inventory
     */
    public static boolean give(Player player, ItemStack item) {
        final Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);

        if (leftovers.isEmpty()) {
            return true;
        } else {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 0.2f, 1.0f);
            // not everything fit.
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            for (ItemStack leftover : leftovers.values()) {
                player.getInventory().setItemInMainHand(leftover);
                player.dropItem(true);
            }
            player.getInventory().setItemInMainHand(heldItem);
            return false;
        }
    }
}
