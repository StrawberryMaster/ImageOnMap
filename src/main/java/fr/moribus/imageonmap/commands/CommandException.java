/*
 * Copyright or © or Copr. QuartzLib contributors (2015 - 2020)
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
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
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.moribus.imageonmap.commands;

import fr.moribus.imageonmap.gui.GuiUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CommandException extends Exception {
    private final Reason reason;
    private final Command command;
    private final String extra;

    public CommandException(Command command, Reason reason, String extra) {
        super(getReasonString(command, reason, extra));
        this.command = command;
        this.reason = reason;
        this.extra = extra;
    }

    public CommandException() {
        super(getReasonString());
        this.command = null;
        this.reason = null;
        this.extra = null;
    }

    public CommandException(Command command, Reason reason) {
        this(command, reason, "");
    }

    private static String getReasonString(Command command, Reason reason, String extra) {
        switch (reason) {
            case COMMANDSENDER_EXPECTED_PLAYER:
                return Component.text("You must be a player to use this command.").toString();
            case INVALID_PARAMETERS:
                Component prefix = Component.text(Commands.CHAT_PREFIX + " ", NamedTextColor.GOLD);
                return Component.text("\n")
                        .append(Component.text(Commands.CHAT_PREFIX + ' ', NamedTextColor.RED))
                        .append(Component.text("Invalid argument", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text('\n'))
                        .append(GuiUtils.generatePrefixedFixedLengthComponent(Component.text(Commands.CHAT_PREFIX + " ", NamedTextColor.RED), extra))
                        .append(Component.text('\n'))
                        .append(GuiUtils.generatePrefixedFixedLengthComponent(prefix, "Usage: " + command.getUsageString()))
                        .append(Component.text('\n'))
                        .append(GuiUtils.generatePrefixedFixedLengthComponent(prefix, "For more information, use /" +
                                command.getCommandGroup().getUsualName() + " help " + command.getName()))
                        .toString();
            case COMMAND_ERROR:
                return extra.isEmpty() ? Component.text("An unknown error suddenly happened.").toString() : Component.text(extra).toString();
            case SENDER_NOT_AUTHORIZED:
                return Component.text("You do not have the permission to use this command.").toString();
            default:
                return Component.text("An error occurred.").toString();
        }
    }

    public enum Reason {
        COMMANDSENDER_EXPECTED_PLAYER,
        INVALID_PARAMETERS,
        COMMAND_ERROR,
        SENDER_NOT_AUTHORIZED
    }
}
