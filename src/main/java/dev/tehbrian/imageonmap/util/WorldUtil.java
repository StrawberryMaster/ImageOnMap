package dev.tehbrian.imageonmap.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class WorldUtil {
    private WorldUtil() {
    }

    /**
     * Determines whether two locations point to the same block, that is, whether
     * their block coordinates are equal.
     *
     * @param loc1 the first location
     * @param loc2 the other location
     * @return whether the two given locations point to the same block
     */
    public static boolean pointToSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX() != loc2.getBlockX() || loc1.getBlockY() != loc2.getBlockY() || loc1.getBlockZ() != loc2.getBlockZ();
    }

    /**
     * Returns the orientation of the specified location, as a BlockFace.
     * The precision of the returned BlockFace is restricted to NORTH, SOUTH,
     * EAST and WEST only.
     *
     * @param loc The location.
     * @return the orientation of the specified location, as a BlockFace.
     */
    public static BlockFace getOrientation(Location loc) {
        float yaw = Math.abs(loc.getYaw()) - 180F;

        if (yaw <= 45 && yaw > -45) {
            return BlockFace.NORTH;
        }

        if (yaw <= -45 && yaw > -135) {
            return BlockFace.WEST;
        }

        if (yaw <= -135 || yaw > 135) {
            return BlockFace.SOUTH;
        }

        if (yaw <= 135) {
            return BlockFace.EAST;
        }

        return BlockFace.SELF;
    }
}
