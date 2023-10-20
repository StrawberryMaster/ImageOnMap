package dev.tehbrian.imageonmap;

import fr.moribus.imageonmap.PluginConfiguration;
import fr.moribus.imageonmap.commands.Commands;
import fr.moribus.imageonmap.commands.maptool.DeleteCommand;
import fr.moribus.imageonmap.commands.maptool.ExploreCommand;
import fr.moribus.imageonmap.commands.maptool.GetCommand;
import fr.moribus.imageonmap.commands.maptool.GetRemainingCommand;
import fr.moribus.imageonmap.commands.maptool.GiveCommand;
import fr.moribus.imageonmap.commands.maptool.ListCommand;
import fr.moribus.imageonmap.commands.maptool.NewCommand;
import fr.moribus.imageonmap.commands.maptool.RenameCommand;
import fr.moribus.imageonmap.commands.maptool.UpdateCommand;
import fr.moribus.imageonmap.gui.Gui;
import fr.moribus.imageonmap.i18n.I18n;
import fr.moribus.imageonmap.image.MapInitEvent;
import fr.moribus.imageonmap.map.MapManager;
import fr.moribus.imageonmap.ui.MapItemManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

public final class ImageOnMap extends JavaPlugin {

    @Deprecated
    private static ImageOnMap PLUGIN;

    private final Path mapsDirectory;
    private final Path imagesDirectory;

    public ImageOnMap() {
        PLUGIN = this;

        Path data = getDataFolder().toPath();
        mapsDirectory = data.resolve("maps");
        imagesDirectory = data.resolve("images");
    }

    public static ImageOnMap get() {
        return PLUGIN;
    }

    public Path getMapsDirectory() {
        return mapsDirectory;
    }

    public Path getImagesDirectory() {
        return imagesDirectory;
    }

    public Path getImageFile(int mapId) {
        return imagesDirectory.resolve("map" + mapId + ".png");
    }

    @Override
    public void onEnable() {
        // create images and maps directories if necessary.
        try {
            createDirectoryIfNonexistent(mapsDirectory);
            createDirectoryIfNonexistent(imagesDirectory);
        } catch (final IOException e) {
            getSLF4JLogger().error("Failed to create plugin directory.", e);
            this.setEnabled(true);
            return;
        }

        saveDefaultConfig();
        Gui.clearOpenGuis();

        JarFile pluginJar = getJarFile();

        try {
            I18n.onEnable(pluginJar);
        } finally {
            if (pluginJar != null) {
                try {
                    pluginJar.close();
                } catch (IOException e) {
                    this.getSLF4JLogger().error("Unable to close JAR file " + getFile().getAbsolutePath(), e);
                }
            }
        }

        I18n.setPrimaryLocale(PluginConfiguration.LANG.get());

        MapManager.init();
        MapInitEvent.init();
        MapItemManager.init();

        Commands.register(
                "maptool",
                NewCommand.class,
                ListCommand.class,
                GetCommand.class,
                RenameCommand.class,
                DeleteCommand.class,
                GiveCommand.class,
                GetRemainingCommand.class,
                ExploreCommand.class,
                UpdateCommand.class
        );

        Commands.registerShortcut("maptool", NewCommand.class, "tomap");
        Commands.registerShortcut("maptool", ExploreCommand.class, "maps");
        Commands.registerShortcut("maptool", GiveCommand.class, "givemap");
    }

    private void createDirectoryIfNonexistent(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            Files.createDirectories(directory);
        }
    }

    /**
     * Returns the JAR file that this plugin was loaded by, or null if it couldn't be loaded.
     */
    public @Nullable JarFile getJarFile() {
        try {
            return new JarFile(getFile());
        } catch (IOException e) {
            this.getSLF4JLogger().error("Unable to load JAR file " + getFile().getAbsolutePath(), e);
            return null;
        }
    }

    @Override
    public void onDisable() {
        MapManager.exit();
        MapItemManager.exit();

        Gui.clearOpenGuis();
    }

}
