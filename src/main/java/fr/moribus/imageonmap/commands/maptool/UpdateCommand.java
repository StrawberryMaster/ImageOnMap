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

package fr.moribus.imageonmap.commands.maptool;

import dev.tehbrian.imageonmap.ImageOnMap;
import dev.tehbrian.imageonmap.Permission;
import dev.tehbrian.imageonmap.util.ActionBar;
import fr.moribus.imageonmap.commands.CommandException;
import fr.moribus.imageonmap.commands.CommandInfo;
import fr.moribus.imageonmap.commands.IoMCommand;
import fr.moribus.imageonmap.i18n.I;
import fr.moribus.imageonmap.image.ImageRendererExecutor;
import fr.moribus.imageonmap.image.ImageUtils;
import fr.moribus.imageonmap.map.ImageMap;
import fr.moribus.imageonmap.map.MapManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

@CommandInfo(name = "update", usageParameters = "[player name]:<map name> <new url> [stretched|covered] ")
public class UpdateCommand extends IoMCommand {
    @Override
    protected void run() throws CommandException {
        //TODO fix the issue where to many quick usage of offlineNameFetch will return null
        ArrayList<String> arguments = getArgs();
        String warningMsg;
        if (arguments.size() > 4) {
            warningMsg = "Too many parameters!"
                    + " Usage: /maptool update [player name]:<map name> <new url> [stretched|covered]";
            warning(I.t(warningMsg));
            return;
        }
        if (arguments.size() < 2) {
            warningMsg =
                    "Too few parameters! Usage: /maptool update [player name]:<map name> <new url> [stretched|covered]";
            warning(I.t(warningMsg));
            return;
        }
        final String playerName;
        final String mapName;
        final String url;
        final String resize;
        final Player playerSender;
        Player playerSender1;
        try {
            playerSender1 = playerSender();
        } catch (CommandException ignored) {
            if (arguments.size() == 2) {
                throwInvalidArgument(
                        I.t("Usage: /maptool update [player name]:<map name> <new url> [stretched|covered]"));
            }
            playerSender1 = null;
        }
        playerSender = playerSender1;

        //Sent by a non player and not enough arguments
        if (arguments.size() == 2 && playerSender == null) {
            throwInvalidArgument("Usage: /maptool update [player name]:<map name> <new url> [stretched|covered]");
            return;
        }

        if (arguments.size() == 2) {
            resize = "";
            playerName = playerSender.getName();
            mapName = arguments.get(0);
            url = arguments.get(1);
        } else {
            if (arguments.size() == 4) {
                if (!Permission.UPDATEOTHER.grantedTo(sender)) {
                    throwNotAuthorized();
                    return;
                }
                playerName = arguments.get(0);
                mapName = arguments.get(1);
                url = arguments.get(2);
                resize = arguments.get(3);
            } else {
                if (arguments.size() == 3) {
                    if (arguments.get(2).equals("covered") || arguments.get(2).equals("stretched")) {
                        playerName = sender.getName();
                        mapName = arguments.get(0);
                        url = arguments.get(1);
                        resize = arguments.get(2);
                    } else {
                        if (!Permission.UPDATEOTHER.grantedTo(sender)) {
                            throwNotAuthorized();
                            return;
                        }
                        playerName = arguments.get(0);
                        mapName = arguments.get(1);
                        url = arguments.get(2);
                        resize = "";
                    }
                } else {
                    resize = "";
                    playerName = "";
                    url = "";
                    mapName = "";
                }
            }
        }

        final ImageUtils.ScalingType scaling = switch (resize) {
            case "stretched" -> ImageUtils.ScalingType.STRETCHED;
            case "covered" -> ImageUtils.ScalingType.COVERED;
            default -> ImageUtils.ScalingType.CONTAINED;
        };

        //TODO passer en static
        //ImageOnMap.getPlugin().getCommandWorker().offlineNameFetch(playerName, uuid -> {
        retrieveUUID(playerName, uuid -> {

            ImageMap map = MapManager.getMap(uuid, mapName);

            if (map == null) {
                warning(sender, I.t("This map does not exist."));
                return;
            }

            URL url1;
            try {
                url1 = new URL(url);
                //TODO replace by a check of the load status.(if not loaded load the mapmanager)
                MapManager.load();//we don't want to spam the console each time we reload the mapManager

                Integer[] size = {1, 1};
                if (map.getType() == ImageMap.Type.POSTER) {
                    size = ImageMap.getSize(map.getUserUUID(), map.getId());
                }

                if (size == null) {
                    warning(sender, I.t("This map does not exist.")); // fixme: better message?
                    return;
                }

                int width = size[0];
                int height = size[1];
                try {
                    if (playerSender != null) {
                        ActionBar.showPermanentMessage(playerSender, Component.text(I.t("Updating...")).color(NamedTextColor.DARK_GREEN));
                    }
                    ImageRendererExecutor.update(url1, scaling, uuid, map, width, height)
                            .exceptionally(exception -> {
                                if (playerSender != null) {
                                    playerSender.sendMessage(
                                            I.t("{ce}Map rendering failed: {0}", exception.getMessage())
                                    );
                                }
                                ImageOnMap.get().getLogger()
                                        .warning("Rendering from " + (playerSender != null ? playerSender.getName() : sender.getName()) + " failed: "
                                                + exception.getClass().getCanonicalName() + ": "
                                                + exception.getMessage());
                                return null;
                            })
                            .thenAccept(result -> {
                                if (playerSender != null) {
                                    ActionBar.removeMessage(playerSender);
                                    playerSender.sendActionBar(Component.text()
                                            .color(NamedTextColor.DARK_GREEN)
                                            .append(Component.text(I.t("The map was updated using the new image!")))
                                            .build()
                                    );
                                }
                            });
                } finally {
                    if (playerSender != null) {
                        ActionBar.removeMessage(playerSender);
                    }
                }
            } catch (MalformedURLException ex) {
                warning(sender, I.t("Invalid URL."));
            }
        });


    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return Permission.UPDATE.grantedTo(sender);
    }
}
