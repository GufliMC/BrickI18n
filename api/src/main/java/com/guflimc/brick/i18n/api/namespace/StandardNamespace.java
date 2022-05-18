package com.guflimc.brick.i18n.api.namespace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guflimc.brick.i18n.api.objectmapper.ObjectMapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.TranslationRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StandardNamespace {

    protected final TranslationRegistry registry;
    protected final Locale defaultLocale;
    protected final String id;

    protected final StandardComponentRenderer renderer;

    public StandardNamespace(String id, Locale defaultLocale) {
        final Key key = Key.key("i18n:" + id.toLowerCase());
        this.registry = TranslationRegistry.create(key);
        this.renderer = StandardComponentRenderer.usingTranslationSource(registry);
        this.defaultLocale = defaultLocale;
        this.id = id;

        this.registry.defaultLocale(defaultLocale);
    }

    // api

    public final String id() {
        return id;
    }

    public Component translate(Locale locale, TranslatableComponent component) {
        if (registry.contains(component.key()) ) {
            return renderer.render(component, locale);
        } else {
            return Component.text(""); //I18nAPI.global().translate(locale, component);
        }
    }

    public final Component translate(Locale locale, String key) {
        return translate(locale, Component.translatable(key));
    }

    public final Component translate(Locale locale, String key, Object... args) {
        return translate(locale, translatable(key, args));
    }

    public final Component translate(Locale locale, String key, Component... args) {
        return translate(locale, translatable(key, args));
    }

    public void send(Audience sender, TranslatableComponent component) {
        sender.sendMessage(translate(defaultLocale, component));
    }

    public final void send(Audience sender, String key) {
        send(sender, Component.translatable(key));
    }

    public final void send(Audience sender, String key, Object... args) {
        send(sender, translatable(key, args));
    }

    public final void send(Audience sender, String key, Component... args) {
        send(sender, translatable(key, args));
    }

    protected final TranslatableComponent translatable(String key, Object... args) {
        Component[] cargs = new Component[args.length];
        for ( int i = 0; i < args.length; i++ ) {
            if ( args[i] == null ) {
                cargs[i] = Component.text("null");
                continue;
            }

            cargs[i] = ObjectMapper.map(args[i]);
        }

        return Component.translatable(key).args(cargs);
    }

    protected final TranslatableComponent translatable(String key, Component... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                args[i] = Component.text("null");
            }
        }

        return Component.translatable(key).args(args);
    }

    // LOADING

    public final void loadValues(Path path) throws MalformedURLException {
        Locale locale = Locale.forLanguageTag(path.getFileName().toString().split("\\.")[0]);
        loadValues(path.toUri().toURL(), locale);
    }

    public final void loadValues(URL resource) {
        String[] parts = resource.getFile().split("/");
        Locale locale = Locale.forLanguageTag(parts[parts.length - 1].split("\\.")[0]);
        loadValues(resource, locale);
    }

    public final void loadValues(URL resource, Locale locale) {
        try {
            load(resource.openStream(), locale);
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse json of file '" + resource.getFile() + "'.", e);
        }
    }

    private void load(InputStream inputStream, Locale locale) throws IOException {
        try (
                inputStream;
                InputStreamReader isr = new InputStreamReader(inputStream)
        ) {
            JsonObject config = JsonParser.parseReader(isr).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : config.entrySet()) {
                if (!registry.contains(entry.getKey())) {
                    registry.register(entry.getKey(), locale, new MessageFormat(entry.getValue().getAsString()));
                }
            }
        }
    }

}
