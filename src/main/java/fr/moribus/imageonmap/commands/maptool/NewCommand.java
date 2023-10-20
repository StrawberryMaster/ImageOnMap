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
import fr.moribus.imageonmap.Permissions;
import fr.moribus.imageonmap.commands.IoMCommand;
import fr.moribus.imageonmap.i18n.I;
import fr.moribus.imageonmap.image.ImageRendererExecutor;
import fr.moribus.imageonmap.image.ImageUtils;
import fr.moribus.imageonmap.map.PosterMap;
import fr.moribus.imageonmap.commands.CommandException;
import fr.moribus.imageonmap.commands.CommandInfo;
import fr.zcraft.quartzlib.tools.text.ActionBar;
import java.net.MalformedURLException;
import java.net.URL;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "new", usageParameters = "<URL> [resize]")
public class NewCommand extends IoMCommand {

    private ImageUtils.ScalingType resizeMode() throws CommandException {
        return switch (args[1]) {
            case "resize" -> ImageUtils.ScalingType.CONTAINED;
            case "stretch", "stretched", "resize-stretched" -> ImageUtils.ScalingType.STRETCHED;
            case "cover", "covered", "resize-covered" -> ImageUtils.ScalingType.COVERED;
            default -> {
                throwInvalidArgument(I.t("Invalid Stretching mode."));
                yield  ImageUtils.ScalingType.NONE;
            }
        };
    }

    @Override
    protected void run() throws CommandException {
        final Player player = playerSender();
        ImageUtils.ScalingType scaling = ImageUtils.ScalingType.NONE;
        URL url;
        int width = 0;
        int height = 0;

        if (args.length < 1) {
            throwInvalidArgument(I.t("You must give an URL to take the image from."));
        }

        try {
            url = new URL(args[0]);
        } catch (MalformedURLException ex) {
            throwInvalidArgument(I.t("Invalid URL."));
            return;
        }

        if (args.length >= 2) {
            if (args.length >= 4) {
                width = Integer.parseInt(args[2]);
                height = Integer.parseInt(args[3]);
            }
            scaling = resizeMode();
        }
        try {
            ActionBar.sendPermanentMessage(player, ChatColor.DARK_GREEN + I.t("Rendering..."));
            ImageRendererExecutor.render(url, scaling, player.getUniqueId(), width, height)
                    .exceptionallyAsync((exception) -> {
                        player.sendMessage(I.t("{ce}Map rendering failed: {0}", exception.getMessage()));
                        ImageOnMap.get().getLogger().warning("Rendering from " + player.getName() + " failed: "
                                + exception.getClass().getCanonicalName() + ": " + exception.getMessage());
                        return null;
                    })
                    .thenAccept(result -> {
                        ActionBar.removeMessage(player);
                        player.sendActionBar(Component.text()
                                .color(NamedTextColor.DARK_GREEN)
                                .append(Component.text(I.t("Rendering finished!")))
                                .build()
                        );

                        if (result.give(player)
                                && (result instanceof PosterMap && !((PosterMap) result).hasColumnData())) {
                            info(I.t("The rendered map was too big to fit in your inventory."));
                            info(I.t("Use '/maptool getremaining' to get the remaining maps."));
                        }
                    });
        } finally {
            ActionBar.removeMessage(player);
        }
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return Permissions.NEW.grantedTo(sender);
    }
}
