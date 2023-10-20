/*
 * Copyright or © or Copr. Moribus (2013)
 * Copyright or © or Copr. ProkopyL <prokopylmc@gmail.com> (2015)
 * Copyright or © or Copr. Amaury Carrade <amaury@carrade.eu> (2016 – 2021)
 * Copyright or © or Copr. Vlammar <valentin.jabre@gmail.com> (2019 – 2021)
 *
 * This software is a computer program whose purpose is to allow insertion of
 * custom images in a Minecraft world.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package fr.moribus.imageonmap.map;

import fr.moribus.imageonmap.ImageOnMap;
import fr.moribus.imageonmap.PluginConfiguration;
import fr.moribus.imageonmap.image.ImageIOExecutor;
import fr.moribus.imageonmap.image.PosterImage;
import fr.moribus.imageonmap.map.MapManagerException.Reason;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

public abstract class MapManager {
    private static final ArrayList<PlayerMapStore> playerMaps = new ArrayList<>();

    public static void init() {
        load();
    }

    public static void exit() {
        save();
        playerMaps.clear();
    }

    public static boolean managesMap(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.getType() != Material.FILLED_MAP) {
            return false;
        }

        synchronized (playerMaps) {
            for (PlayerMapStore mapStore : playerMaps) {
                if (mapStore.managesMap(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ImageMap createMap(UUID playerUUID, int mapID) throws MapManagerException {
        ImageMap newMap = new SingleMap(playerUUID, mapID);
        addMap(newMap);
        return newMap;
    }

    public static ImageMap createMap(PosterImage image, UUID playerUUID, int[] mapsIDs) throws MapManagerException {
        ImageMap newMap;

        if (image.getImagesCount() == 1) {
            newMap = new SingleMap(playerUUID, mapsIDs[0]);
        } else {
            newMap = new PosterMap(playerUUID, mapsIDs, image.getColumns(), image.getLines());
        }
        addMap(newMap);
        return newMap;
    }

    public static int[] getNewMapsIds(int amount) {
        int[] mapsIds = new int[amount];
        for (int i = 0; i < amount; i++) {
            mapsIds[i] = Bukkit.createMap(Bukkit.getWorlds().get(0)).getId();
        }
        return mapsIds;
    }

    /**
     * Returns the map ID from an ItemStack
     *
     * @param item The item stack
     * @return The map ID, or 0 if invalid.
     */
    public static int getMapIdFromItemStack(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof MapMeta)) {
            return 0;
        }

        return ((MapMeta) meta).hasMapId() ? ((MapMeta) meta).getMapId() : 0;
    }

    public static void addMap(ImageMap map) throws MapManagerException {
        getPlayerMapStore(map.getUserUUID()).addMap(map);
    }

    public static void deleteMap(ImageMap map) throws MapManagerException {
        getPlayerMapStore(map.getUserUUID()).deleteMap(map);
        ImageIOExecutor.deleteImage(map);
    }

    public static String getNextAvailableMapID(String mapId, UUID playerUUID) {
        return getPlayerMapStore(playerUUID).getNextAvailableMapID(mapId);
    }

    public static List<ImageMap> getMapList(UUID playerUUID) {
        return getPlayerMapStore(playerUUID).getMapList();
    }

    public static ImageMap[] getMaps(UUID playerUUID) {
        return getPlayerMapStore(playerUUID).getMaps();
    }

    /**
     * Returns the number of minecraft maps used by the images rendered by the given player.
     *
     * @param playerUUID The player's UUID.
     * @return The count.
     */
    public static int getMapPartCount(UUID playerUUID) {
        return getPlayerMapStore(playerUUID).getMapCount();
    }

    public static ImageMap getMap(UUID playerUUID, String mapId) {
        return getPlayerMapStore(playerUUID).getMap(mapId);
    }

    /**
     * Returns the {@link ImageMap} this map belongs to.
     *
     * @param mapId The ID of the Minecraft map.
     * @return The {@link ImageMap}.
     */
    public static ImageMap getMap(int mapId) {
        synchronized (playerMaps) {
            for (PlayerMapStore mapStore : playerMaps) {
                if (mapStore.managesMap(mapId)) {
                    for (ImageMap map : mapStore.getMapList()) {
                        if (map.managesMap(mapId)) {
                            return map;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns the {@link ImageMap} this map belongs to.
     *
     * @param item The map, as an {@link ItemStack}.
     * @return The {@link ImageMap}.
     */
    public static ImageMap getMap(ItemStack item) {
        if (item == null) {
            return null;
        }
        if (item.getType() != Material.FILLED_MAP) {
            return null;
        }
        return getMap(getMapIdFromItemStack(item));
    }

    public static void clear(Inventory inventory, ImageMap map) {
        for (int i = 0, c = inventory.getSize(); i < c; i++) {
            if (map.managesMap(inventory.getItem(i))) {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

    private static UUID getUUIDFromFile(Path file) {
        String fileName = file.getFileName().toString();
        int fileExtPos = fileName.lastIndexOf('.');
        if (fileExtPos <= 0) {
            return null;
        }

        String fileExt = fileName.substring(fileExtPos + 1);
        if (!fileExt.equals("yml")) {
            return null;
        }

        try {
            return UUID.fromString(fileName.substring(0, fileExtPos));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    //Loading
    public static void load() {
        try (var list = Files.list(ImageOnMap.getPlugin().getMapsDirectory())) {
            list.map(MapManager::getUUIDFromFile)
                    .filter(Objects::nonNull)
                    .forEach(MapManager::getPlayerMapStore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        synchronized (playerMaps) {
            for (PlayerMapStore tmpStore : playerMaps) {
                tmpStore.save();
            }
        }
    }

    public static void checkMapLimit(int newMapsCount, UUID userUUID) throws MapManagerException {
        int limit = PluginConfiguration.MAP_GLOBAL_LIMIT.get();

        if (limit > 0 && getMapCount() + newMapsCount > limit) {
            throw new MapManagerException(Reason.MAXIMUM_SERVER_MAPS_EXCEEDED);
        }

        getPlayerMapStore(userUUID).checkMapLimit(newMapsCount);
    }

    /**
     * Returns the total number of minecraft maps used by ImageOnMap images.
     *
     * @return The count.
     */
    public static int getMapCount() {
        int mapCount = 0;
        synchronized (playerMaps) {
            for (PlayerMapStore tmpStore : playerMaps) {
                mapCount += tmpStore.getMapCount();
            }
        }
        return mapCount;
    }

    public static PlayerMapStore getPlayerMapStore(UUID playerUUID) {
        PlayerMapStore store;
        synchronized (playerMaps) {
            store = getExistingPlayerMapStore(playerUUID);
            if (store == null) {
                store = new PlayerMapStore(playerUUID);

                playerMaps.add(store);
                store.load();
            }
        }
        return store;
    }

    private static PlayerMapStore getExistingPlayerMapStore(UUID playerUUID) {
        synchronized (playerMaps) {
            for (PlayerMapStore mapStore : playerMaps) {
                if (mapStore.getUUID().equals(playerUUID)) {
                    return mapStore;
                }
            }
        }
        return null;
    }
}
