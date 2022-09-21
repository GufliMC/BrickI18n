package com.guflimc.brick.i18n.spigot.api.namespace;

import com.guflimc.brick.i18n.api.namespace.ExtendedNamespace;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Stream;

public class SpigotNamespace extends ExtendedNamespace<Player> {

    private static final Logger logger = LoggerFactory.getLogger(SpigotNamespace.class);

    private final LegacyComponentSerializer serializer = BukkitComponentSerializer.legacy();
    private final BukkitAudiences adventure;

    SpigotNamespace(String id, JavaPlugin plugin, Locale defaultLocale) {
        super(id, defaultLocale);
        adventure = BukkitAudiences.create(plugin);
    }

    public SpigotNamespace(JavaPlugin plugin, Locale defaultLocale) {
        this(plugin.getName(), plugin, defaultLocale);
    }

    @Override
    protected Audience audience(Player subject) {
        return adventure.player(subject);
    }

    @Override
    protected Locale locale(Player subject) {
        return Locale.forLanguageTag(subject.getLocale());
    }

    //

    public String string(Player subject, TranslatableComponent component) {
        return serializer.serialize(translate(locale(subject), component));
    }

    public final String string(Player subject, String key) {
        return string(subject, Component.translatable(key));
    }

    public final String string(Player subject, String key, Object... args) {
        return string(subject, translatable(key, args));
    }

    public final String string(Player subject, String key, Component... args) {
        return string(subject, translatable(key, args));
    }

    // LOAD VALUES

    public final void loadValues(JavaPlugin plugin, String pathToResources) {
        // load files from data directory (high priority)
        File directory = new File(plugin.getDataFolder(), pathToResources);
        if (directory.exists() || directory.mkdirs()) {
            for (File file : directory.listFiles()) {
                try {
                    loadValues(file.toPath());
                } catch (IOException ex) {
                    throw new RuntimeException("Cannot read resource '" + file.getName() + "'", ex);
                }
            }
        }

        // look for embeded files
        URL url = plugin.getClass().getClassLoader().getResource(pathToResources);
        if (url == null) {
            throw new RuntimeException("Resource not found.");
        }

        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid resource path.", e);
        }

        // load embedded files from jar (low priority)
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            Path root = fs.getPath(pathToResources);
            Stream<Path> pathStream = Files.walk(root);
            for (Iterator<Path> it = pathStream.iterator(); it.hasNext(); ) {
                Path path = it.next();
                if (!path.getFileName().toString().contains(".")) {
                    continue;
                }


                Path targetFile = plugin.getDataFolder().toPath().resolve(pathToResources).resolve(root.relativize(path).toString());
                if (!targetFile.toFile().exists()) {
                    try (InputStream is = path.toUri().toURL().openStream()) {
                        Files.createDirectories(targetFile.getParent());
                        Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        logger.warn("Cannot save packaged resource '" + path + "' of plugin '" + plugin.getName() + "'.");
                    }
                    continue;
                }

                loadValues(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot traverse files of given path.", e);
        }
    }

}
