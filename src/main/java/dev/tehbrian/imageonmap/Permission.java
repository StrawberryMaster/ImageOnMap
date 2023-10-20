package dev.tehbrian.imageonmap;

import org.bukkit.permissions.Permissible;

public enum Permission {
    NEW("imageonmap.new"),
    LIST("imageonmap.list"),
    LISTOTHER("imageonmap.listother"),
    GET("imageonmap.get"),
    GETOTHER("imageonmap.getother"),
    RENAME("imageonmap.rename"),
    PLACE_SPLATTER_MAP("imageonmap.placesplattermap"),
    REMOVE_SPLATTER_MAP("imageonmap.removesplattermap"),
    DELETE("imageonmap.delete"),
    DELETEOTHER("imageonmap.deleteother"),
    UPDATE("imageonmap.update"),
    UPDATEOTHER("imageonmap.updateother"),
    ADMINISTRATIVE("imageonmap.administrative"),
    BYPASS_SIZE("imageonmap.bypasssize"),
    GIVE("imageonmap.give");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    /**
     * Check whether the permission is granted to the provided permissible.
     *
     * @param permissible the permissible to check
     * @return whether this permission is granted to the permissible
     */
    public boolean grantedTo(Permissible permissible) {
        return permissible.hasPermission(permission);
    }
}