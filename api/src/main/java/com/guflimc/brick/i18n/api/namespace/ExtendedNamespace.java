package com.guflimc.brick.i18n.api.namespace;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class ExtendedNamespace<T> extends StandardNamespace {

    public ExtendedNamespace(String id, Locale defaultLocale) {
        super(id, defaultLocale);
    }

    protected abstract Audience audience(T subject);

    protected abstract Locale locale(T subject);

    //

    public Component translate(T subject, TranslatableComponent component) {
        return translate(locale(subject), component);
    }

    public final Component translate(T subject, String key) {
        return translate(subject, Component.translatable(key));
    }

    public final Component translate(T subject, String key, Object... args) {
        return translate(subject, translatable(key, args));
    }

    public final Component translate(T subject, String key, Component... args) {
        return translate(subject, translatable(key, args));
    }

    // extended

    public void send(T subject, TranslatableComponent component) {
        audience(subject).sendMessage(translate(subject, component));
    }

    public final void send(T subject, String key) {
        audience(subject).sendMessage(translate(subject, key));
    }

    public final void send(T subject, String key, Object... args) {
        audience(subject).sendMessage(translate(subject, key, args));
    }

    public final void send(T subject, String key, Component... args) {
        audience(subject).sendMessage(translate(subject, key, args));
    }

    //

    public final Component hoverable(T subject, @NotNull String msgKey, @NotNull String hoverKey) {
        return hoverable(locale(subject), msgKey, hoverKey);
    }

    //

    public final Component maybeTranslate(T subject, Component component) {
        return maybeTranslate(locale(subject), component);
    }

}
