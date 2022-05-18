package com.guflimc.brick.i18n.spigot.api.namespace;

import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
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

public class SpigotNamespace extends StandardNamespace {

    private static final Logger logger = LoggerFactory.getLogger(SpigotNamespace.class);

    SpigotNamespace(String id, Locale defaultLocale) {
        super(id, defaultLocale);
    }

    public SpigotNamespace(JavaPlugin plugin, Locale defaultLocale) {
        super(plugin.getName(), defaultLocale);
    }

    // Localizable support

    public Component translate(Player player, TranslatableComponent component) {
        return translate(Locale.forLanguageTag(player.getLocale()), component);
    }

    public final Component translate(Player player, String key) {
        return translate(player, Component.translatable(key));
    }

    public final Component translate(Player player, String key, Object... args) {
        return translate(player, translatable(key, args));
    }

    public final Component translate(Player player, String key, Component... args) {
        return translate(player, translatable(key, args));
    }

    // override

    @Override
    public Component translate(Locale locale, TranslatableComponent component) {
        if (registry.contains(component.key()) ) {
            return renderer.render(component, locale);
        }
        if ( id.equals("global") ) {
            return Component.text("");
        }
        return SpigotI18nAPI.global().translate(locale, component);
    }

    @Override
    public void send(Audience sender, TranslatableComponent component) {
        if ( sender instanceof Player player) {
            sender.sendMessage(translate(player, component));
            return;
        }
        super.send(sender, component);
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
