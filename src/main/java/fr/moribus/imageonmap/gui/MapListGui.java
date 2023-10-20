package fr.moribus.imageonmap.gui;

import dev.tehbrian.imageonmap.Permission;
import fr.moribus.imageonmap.PluginConfiguration;
import fr.moribus.imageonmap.i18n.I;
import fr.moribus.imageonmap.map.ImageMap;
import fr.moribus.imageonmap.map.MapManager;
import fr.moribus.imageonmap.map.PosterMap;
import fr.moribus.imageonmap.map.SingleMap;
import fr.moribus.imageonmap.ui.MapItemManager;
import fr.moribus.imageonmap.ui.SplatterMapManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapListGui extends ExplorerGui<ImageMap> {
    private final OfflinePlayer player;
    private final String name;

    public MapListGui(OfflinePlayer p, String name) {
        this.player = p;
        this.name = name;
    }

    @Override
    protected ItemStack getViewItem(ImageMap map) {
        String mapDescription;
        if (map instanceof SingleMap) {
            /// Displayed subtitle description of a single map on the list GUI
            mapDescription = I.tl(getPlayerLocale(), "{white}Single map");
        } else {
            PosterMap poster = (PosterMap) map;
            if (poster.hasColumnData()) {
                /// Displayed subtitle description of a poster map on the list GUI (columns × rows in english)
                mapDescription = I.tl(getPlayerLocale(), "{white}Poster map ({0} × {1})", poster.getColumnCount(),
                        poster.getRowCount());
            } else {
                /// Displayed subtitle description of a poster map without column data on the list GUI
                mapDescription = I.tl(getPlayerLocale(), "{white}Poster map ({0} parts)", poster.getMapCount());
            }
        }

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();

        meta.displayName(Component.text(I.tl(getPlayerLocale(), "{green}{bold}{0}", map.getName())));
        List<String> lore = new ArrayList<>(Arrays.asList(
                mapDescription,
                "",
                I.tl(getPlayerLocale(), "{gray}Map ID: {0}", map.getId()),
                ""
        ));
        if (Permission.GET.grantedTo(getPlayer())) {
            lore.add(I.tl(getPlayerLocale(), "{gray}» {white}Left-click{gray} to get this map"));
        }
        lore.add(I.tl(getPlayerLocale(), "{gray}» {white}Right-click{gray} for details and options"));
        meta.lore(lore.stream().map(Component::text).toList());

        meta.setColor(Color.GREEN);
        mapItem.setItemMeta(meta);

        return mapItem;
    }

    @Override
    protected ItemStack getEmptyViewItem() {
        ItemStack item = new ItemStack(Material.MAP);
        ItemMeta meta = item.getItemMeta();
        if (player.getUniqueId().equals(getPlayer().getUniqueId())) {
            meta.displayName(Component.text(I.tl(getPlayerLocale(), "{red}You don't have any map.")));

            List<String> lore = new ArrayList<>();
            if (Permission.NEW.grantedTo(getPlayer())) {
                lore.addAll(GuiUtils.generateLore(I.tl(getPlayerLocale(),
                        "{gray}Get started by creating a new one using {white}/tomap <URL> [resize]{gray}!")));
            } else {
                lore.addAll(GuiUtils.generateLore(I.tl(getPlayerLocale(),
                        "{gray}Unfortunately, you are not allowed to create one.")));
            }
            if (!lore.isEmpty()) {
                meta.lore(lore.stream().map(Component::text).toList());
            }
        } else {
            meta.displayName(Component.text(I.tl(getPlayerLocale(), "{red}{0} doesn't have any map.", name)));
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected void onRightClick(ImageMap data) {
        Gui.open(getPlayer(), new MapDetailGui(data, getPlayer(), name), this);
    }

    @Override
    protected ItemStack getPickedUpItem(ImageMap map) {
        if (!Permission.GET.grantedTo(getPlayer())) {
            return null;
        }

        if (map instanceof SingleMap) {
            return MapItemManager.createMapItem(map.getMapsIDs()[0], map.getName(), false, true);
        } else if (map instanceof PosterMap poster) {
            if (poster.hasColumnData()) {
                return SplatterMapManager.makeSplatterMap((PosterMap) map);
            }

            MapItemManager.giveParts(getPlayer(), poster);
            return null;
        }

        MapItemManager.give(getPlayer(), map);
        return null;
    }

    @Override
    protected void onUpdate() {
        ImageMap[] maps = MapManager.getMaps(player.getUniqueId());
        setData(maps);
        /// The maps list GUI title
        //Equal if the person who send the command is the owner of the mapList
        if (player.getUniqueId().equals(getPlayer().getUniqueId())) {
            setTitle(I.tl(getPlayerLocale(), "{black}Your maps {reset}({0})", maps.length));
        } else {
            setTitle(I.tl(getPlayerLocale(), "{black}{1}'s maps {reset}({0})", maps.length, name));
        }

        setKeepHorizontalScrollingSpace(true);


        /* ** Statistics ** */
        int mapPartCount = MapManager.getMapPartCount(player.getUniqueId());

        int mapGlobalLimit = PluginConfiguration.MAP_GLOBAL_LIMIT.get();
        int mapPersonalLimit = PluginConfiguration.MAP_PLAYER_LIMIT.get();

        int mapPartGloballyLeft = mapGlobalLimit - MapManager.getMapCount();
        int mapPartPersonallyLeft = mapPersonalLimit - mapPartCount;

        int mapPartLeft;
        if (mapGlobalLimit <= 0 && mapPersonalLimit <= 0) {
            mapPartLeft = -1;
        } else if (mapGlobalLimit <= 0) {
            mapPartLeft = mapPartPersonallyLeft;
        } else if (mapPersonalLimit <= 0) {
            mapPartLeft = mapPartGloballyLeft;
        } else {
            mapPartLeft = Math.min(mapPartGloballyLeft, mapPartPersonallyLeft);
        }

        int imagesCount = MapManager.getMapList(player.getUniqueId()).size();
        double percentageUsed =
                mapPartLeft < 0 ? 0 : ((double) mapPartCount) / ((double) (mapPartCount + mapPartLeft)) * 100;

        ItemStack statistics = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = statistics.getItemMeta();
        meta.displayName(Component.text(I.t(getPlayerLocale(), "{blue}Usage statistics")));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(I.tn(getPlayerLocale(),
                "{white}{0}{gray} image rendered", "{white}{0}{gray} images rendered", imagesCount));
        lore.add(I.tn(getPlayerLocale(), "{white}{0}{gray} Minecraft map used",
                "{white}{0}{gray} Minecraft maps used", mapPartCount));

        if (mapPartLeft >= 0) {
            lore.add("");
            lore.add(I.t(getPlayerLocale(), "{blue}Minecraft maps limits"));
            lore.add("");
            lore.add(mapGlobalLimit == 0
                    ? I.t(getPlayerLocale(), "{gray}Server-wide limit: {white}unlimited")
                    : I.t(getPlayerLocale(), "{gray}Server-wide limit: {white}{0}", mapGlobalLimit));
            lore.add("");
            lore.add(I.t(getPlayerLocale(), "{white}{0} %{gray} of your quota used",
                    (int) Math.rint(percentageUsed)));
            lore.add(I.tn(getPlayerLocale(), "{white}{0}{gray} map left", "{white}{0}{gray} maps left",
                    mapPartLeft));
        }
        meta.lore(lore.stream().map(Component::text).toList());
        meta.addItemFlags(ItemFlag.values());
        statistics.setItemMeta(meta);

        action("", getSize() - 5, statistics);
    }
}
