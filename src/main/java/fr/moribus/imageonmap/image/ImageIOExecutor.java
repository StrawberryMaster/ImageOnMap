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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.tehbrian.imageonmap.ImageOnMap;
import fr.moribus.imageonmap.map.ImageMap;
import fr.moribus.imageonmap.util.ExceptionCatcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageIOExecutor {

    private static final ExecutorService executor = Executors.newFixedThreadPool(
            Math.min(Runtime.getRuntime().availableProcessors(), 4),
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("Image IO - #%d")
                    .setUncaughtExceptionHandler(ExceptionCatcher::catchException)
                    .build()
    );

    @FunctionalInterface
    interface ExceptionalRunnable {
        void run() throws Throwable;
    }

    private static void run(ExceptionalRunnable runnable) {
        CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                throw new IllegalArgumentException("Error occurred in passed runnable.", t);
            }
        }, executor);
    }

    public static void loadImage(final Path file, final Renderer mapRenderer) {
        run(() -> {
            BufferedImage image = ImageIO.read(file.toFile());
            mapRenderer.setImage(image);
            image.flush(); //Safe to free
        });
    }

    public static void saveImage(final Path file, final BufferedImage image) {
        run(() -> ImageIO.write(image, "png", file.toFile()));
    }

    public static void saveImage(int mapID, BufferedImage image) {
        saveImage(ImageOnMap.get().getImageFile(mapID), image);
    }

    public static void saveImage(int[] mapsIDs, PosterImage image) {
        for (int i = 0, c = mapsIDs.length; i < c; i++) {
            BufferedImage img = image.getImageAt(i);
            ImageIOExecutor.saveImage(ImageOnMap.get().getImageFile(mapsIDs[i]), img);
            img.flush();//Safe to free
        }
    }

    public static void deleteImage(ImageMap map) {
        for (int mapsID : map.getMapsIDs()) {
            deleteImage(ImageOnMap.get().getImageFile(mapsID));
        }
    }

    public static void deleteImage(Path file) {
        run(() -> Files.delete(file));
    }
}
