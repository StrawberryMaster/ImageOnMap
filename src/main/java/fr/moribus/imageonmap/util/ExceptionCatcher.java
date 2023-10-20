package fr.moribus.imageonmap.util;

import dev.tehbrian.imageonmap.ImageOnMap;

import java.util.logging.Level;

public class ExceptionCatcher {

    public static void catchException(Thread thread, Throwable throwable) {
        ImageOnMap.get().getLogger().log(
                Level.SEVERE,
                "An exception occurred in the thread " + thread.getName(),
                throwable
        );
    }
}
