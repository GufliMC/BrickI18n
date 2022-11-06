package com.guflimc.brick.i18n.api.namespace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.api.objectmapper.ObjectMapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

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

    private Locale locale(Audience audience) {
        return audience.pointers().get(Identity.LOCALE).orElse(defaultLocale);
    }

    // api

    public final String id() {
        return id;
    }

    public final Component translate(Locale locale, TranslatableComponent component) {
        if (registry.contains(component.key())) {
            return renderer.render(component, locale);
        }
        if (I18nAPI.global() == this) {
            return Component.text("ERROR: missing message key ", NamedTextColor.RED)
                    .append(Component.text(component.key(), NamedTextColor.DARK_RED));
        }

        return I18nAPI.global().translate(locale, component);
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

    //

    public final Component translate(Audience audience, TranslatableComponent component) {
        return translate(locale(audience), component);
    }

    public final Component translate(Audience audience, String key) {
        return translate(locale(audience), key);
    }

    public final Component translate(Audience audience, String key, Object... args) {
        return translate(locale(audience), key, args);
    }

    public final Component translate(Audience audience, String key, Component... args) {
        return translate(locale(audience), key, args);
    }

    //

    public void send(Audience audience, TranslatableComponent component) {
        audience.sendMessage(translate(locale(audience), component));
    }

    public final void send(Audience audience, String key) {
        send(audience, Component.translatable(key));
    }

    public final void send(Audience audience, String key, Object... args) {
        send(audience, translatable(key, args));
    }

    public final void send(Audience audience, String key, Component... args) {
        send(audience, translatable(key, args));
    }

    //

    protected final TranslatableComponent translatable(String key, Object... args) {
        Component[] cargs = new Component[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
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

    //

    public final Component hoverable(Locale locale, @NotNull String msgKey, @NotNull String hoverKey) {
        return translate(locale, msgKey).hoverEvent(HoverEvent.showText(translate(locale, hoverKey)));
    }

    public final Component hoverable(Audience audience, @NotNull String msgKey, @NotNull String hoverKey) {
        return hoverable(locale(audience), msgKey, hoverKey);
    }

    //

    public final Component center(Component... components) {
        return center(60, components);
    }

    public final Component center(int chatWidth, Component... components) {
        if (components.length == 0) {
            return Component.text("");
        }

        IntStream stream = Arrays.stream(components).mapToInt(this::length);

        int max = stream.max().getAsInt();
        int length = stream.sum();
        int air = chatWidth - length;

        int gap = Math.max(1, air / (components.length + 1));

        Component result = Component.text("");
        for (Component component : components) {
            int diff = Math.max(0, max - length(component)) / 2;
            result = result.append(Component.text(" ".repeat(gap + diff)));
            result = result.append(component);
            result = result.append(Component.text(" ".repeat(gap + diff)));
        }

        return result;
    }

    public final int length(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component).length();
    }

    public final void menu(Audience audience, Component... components) {
        menu(audience, '-', NamedTextColor.WHITE, components);
    }

    public final void menu(Audience audience, char borderShape, TextColor borderColor, Component... components) {
        int length = Math.min(60, Arrays.stream(components).mapToInt(this::length).max().getAsInt());
        Component border = Component.text((borderShape + "").repeat(length), borderColor);

        audience.sendMessage(border);
        for (Component component : components) {
            audience.sendMessage(component);
        }
        audience.sendMessage(border);
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