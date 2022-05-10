package com.guflimc.brick.i18n.minestom.api.namespace;

import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import com.guflimc.brick.i18n.minestom.api.MinestomI18nAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.minestom.server.adventure.Localizable;
import net.minestom.server.extensions.Extension;
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

public class MinestomNamespace extends StandardNamespace {

    private final Logger LOGGER = LoggerFactory.getLogger(MinestomNamespace.class);

    public MinestomNamespace(String id, Locale defaultLocale) {
        super(id, defaultLocale);
    }

    public MinestomNamespace(Extension extension, Locale defaultLocale) {
        super(extension.getOrigin().getName(), defaultLocale);
    }

    // Localizable support

    public Component translate(Localizable localizable, TranslatableComponent component) {
        return translate(localizable.getLocale(), component);
    }

    public final Component translate(Localizable localizable, String key) {
        return translate(localizable, Component.translatable(key));
    }

    public final Component translate(Localizable localizable, String key, Object... args) {
        return translate(localizable, translatable(key, args));
    }

    public final Component translate(Localizable localizable, String key, Component... args) {
        return translate(localizable, translatable(key, args));
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
        return MinestomI18nAPI.global().translate(locale, component);
    }

    @Override
    public void send(Audience sender, TranslatableComponent component) {
        if ( sender instanceof Localizable localizable) {
            sender.sendMessage(translate(localizable, component));
            return;
        }
        super.send(sender, component);
    }

    // LOADING

    public final void loadValues(Extension extension, String pathToResources) {
        // load files from data directory (high priority)
        File directory = extension.getDataDirectory().resolve(pathToResources).toFile();
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
        URL url = extension.getOrigin().getClassLoader().getResource(pathToResources);
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


                Path targetFile = extension.getDataDirectory().resolve(pathToResources).resolve(root.relativize(path).toString());
                if (!targetFile.toFile().exists()) {
                    try (InputStream is = path.toUri().toURL().openStream()) {
                        Files.createDirectories(targetFile.getParent());
                        Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        LOGGER.warn("Cannot save packaged resource '" + path + "' of extension '" + extension.getOrigin().getName() + "'.");
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
