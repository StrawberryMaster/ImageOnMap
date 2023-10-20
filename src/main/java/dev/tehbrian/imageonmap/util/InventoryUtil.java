package dev.tehbrian.imageonmap.util;

import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public abstract class InventoryUtil {
    private InventoryUtil() {
    }

    /**
     * Checks whether two inventories are equal.
     *
     * @param inv1 the first inventory
     * @param inv2 the other inventory
     * @return whether the given inventories are equal
     */
    public static boolean areInventoriesEqual(Inventory inv1, Inventory inv2) {
        if (inv1 == inv2) {
            return true;
        } else if (inv1 == null || inv2 == null) {
            return false;
        }

        return inv1.getType() == inv2.getType() && Arrays.equals(inv1.getContents(), inv2.getContents());
    }
}
