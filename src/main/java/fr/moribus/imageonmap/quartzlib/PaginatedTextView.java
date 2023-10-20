/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 *
 * http://amaury.carrade.eu
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

package fr.moribus.imageonmap.quartzlib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * A utility to send paginated chat views to players, mainly for commands.
 *
 * @param <T> Data type to display.
 */
public abstract class PaginatedTextView<T> {
    public static final int DEFAULT_LINES_IN_NON_EXPANDED_CHAT_VIEW = 10;

    // items minus one header line minus pagination links
    private final int itemsPerPage = DEFAULT_LINES_IN_NON_EXPANDED_CHAT_VIEW - 2;
    private final boolean doNotPaginateForConsole = true;

    private T[] data;
    private int currentPage;
    private int pagesCount;

    /* ========== User configuration ========== */


    /**
     * Sets the data to display.
     *
     * @param data The data.
     * @return Instance for chaining.
     */
    public PaginatedTextView<T> setData(final T[] data) {
        this.data = data;
        recalculatePagination();

        return this;
    }

    /**
     * Sets the current page to be displayed.
     *
     * @param page The page.
     * @return Instance for chaining.
     */
    public PaginatedTextView<T> setCurrentPage(final int page) {
        if (page < 1) {
            currentPage = 1;
        } else {
            currentPage = Math.min(page, pagesCount);
        }

        return this;
    }

    /**
     * Displays the paginated view page, as configured.
     *
     * @param receiver The receiver of the text view.
     */
    public void display(CommandSender receiver) {
        int from;
        int to;

        if (!doNotPaginateForConsole || receiver instanceof Player) {
            from = ((currentPage - 1) * itemsPerPage);
            to = Math.min(from + itemsPerPage, data.length);
        } else {
            from = 0;
            to = data.length;
        }

        displayHeader(receiver);

        for (int i = from; i < to; i++) {
            displayItem(receiver, data[i]);
        }

        displayFooter(receiver);
    }



    /* ========== Overrider & internal utilities ========== */


    /**
     * Gets the data.
     *
     * @return The data, for use in the overridden methods.
     */
    protected T[] data() {
        return data;
    }

    /**
     * Recalculates the page count based on the data length and the items per page.
     */
    private void recalculatePagination() {
        pagesCount = (int) Math.ceil(((double) data.length) / ((double) itemsPerPage));
    }



    /* ========== Methods to override ========== */


    /**
     * Displays a header.
     *
     * <p>If this method is not overridden, no header will be displayed.</p>
     *
     * @param receiver The receiver of the paginated view.
     */
    protected void displayHeader(CommandSender receiver) {
    }

    /**
     * Displays an item.
     * <p>This method will be called for each displayed item.</p>
     *
     * @param receiver The receiver of the paginated view.
     * @param item     The item to be displayed.
     */
    protected abstract void displayItem(CommandSender receiver, T item);

    /**
     * Displays a footer.
     *
     * <p>If this method is not overridden, the default implementation will print pagination links: a “previous” link,
     * if we're not in the first page, the current page, and a “next” link if we're not on the last page.</p>
     *
     * @param receiver The receiver of the paginated view.
     * @see #getCommandToPage(int) Method to override to fully use the default footer.
     */
    protected void displayFooter(CommandSender receiver) {
        if (pagesCount <= 1 || (doNotPaginateForConsole && !(receiver instanceof Player))) {
            return;
        }

        TextComponent.Builder footer = Component.text();

        //RawTextPart<?> footer = new RawText("");

        if (currentPage > 1) {
            String command = getCommandToPage(currentPage - 1);
            if (command != null) {
                footer.append(Component.text("« Previous"))
                        .clickEvent(ClickEvent.runCommand(command))
                        .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (currentPage - 1))))
                        .append(Component.text(" ⋅ "))
                        .color(NamedTextColor.GRAY);
            }
        }

        footer.append(Component.text("Page " + currentPage + " of " + pagesCount))
                .color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD);

        if (currentPage < pagesCount) {
            String command = getCommandToPage(currentPage + 1);
            if (command != null) {
                footer.append(Component.text(" ⋅ "))
                        .append(Component.text("Next »"))
                        .color(NamedTextColor.GRAY)
                        .clickEvent(ClickEvent.runCommand(command))
                        .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (currentPage + 1))));
            }
        }

        receiver.sendMessage(footer.build());
    }

    /**
     * Returns the command to be executed by the player to access the nth page, or {@code null} if not applicable.
     * <p>If you use the default footer, you should override this method. If this returns {@code null},
     * links to previous and next pages will not be displayed.</p>
     *
     * @param page The page.
     * @return The command to be executed to display the page.
     */
    protected String getCommandToPage(int page) {
        return null;
    }
}
