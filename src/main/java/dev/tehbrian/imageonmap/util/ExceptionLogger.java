package dev.tehbrian.imageonmap.util;

import dev.tehbrian.imageonmap.ImageOnMap;

public class ExceptionLogger {
    public static void log(Thread thread, Throwable throwable) {
        ImageOnMap.get().getSLF4JLogger().error(
                "An exception occurred in thread " + thread.getName() + ".",
                throwable
        );
    }
}
