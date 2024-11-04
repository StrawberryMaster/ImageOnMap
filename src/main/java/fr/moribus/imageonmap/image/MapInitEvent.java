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

package fr.moribus.imageonmap.image;

import dev.tehbrian.imageonmap.ImageOnMap;
import fr.moribus.imageonmap.map.MapManager;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class MapInitEvent implements Listener {
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new MapInitEvent(), ImageOnMap.get());

        for (World world : Bukkit.getWorlds()) {
            for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class)) {
                initMap(frame.getItem());
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            initMap(player.getInventory().getItemInMainHand());
        }
    }

    public static void initMap(ItemStack item) {
        if (item != null && item.getType() == Material.FILLED_MAP) {
            initMap(MapManager.getMapIdFromItemStack(item));
        }
    }

    public static void initMap(int id) {
        initMap(Bukkit.getServer().getMap(id));
    }

    public static void initMap(MapView map) {
        if (map == null) {
            return;
        }
        if (Renderer.isHandled(map)) {
            return;
        }

        Path imageFile = ImageOnMap.get().getImageFile(map.getId());
        if (Files.isRegularFile(imageFile)) {
            ImageIOExecutor.loadImage(imageFile, Renderer.installRenderer(map));
        }
    }

    @EventHandler
    public void onMapInitialized(MapInitializeEvent event) {
        initMap(event.getMap());
    }

    @EventHandler
    public void onPlayerInv(PlayerItemHeldEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        initMap(item);
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof HumanEntity)) {
            return;
        }
        initMap(event.getItem().getItemStack());
    }

    @EventHandler
    public void onPlayerInventoryPlace(InventoryClickEvent event) {
        switch (event.getAction()) {
            case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR,
                    CLONE_STACK, COLLECT_TO_CURSOR, DROP_ALL_CURSOR, DROP_ALL_SLOT,
                    DROP_ONE_CURSOR, DROP_ONE_SLOT, HOTBAR_SWAP,
                    MOVE_TO_OTHER_INVENTORY, NOTHING, PICKUP_ALL, PICKUP_HALF,
                    PICKUP_ONE, PICKUP_SOME, UNKNOWN ->
                initMap(event.getCursor());
        }
    }
}
