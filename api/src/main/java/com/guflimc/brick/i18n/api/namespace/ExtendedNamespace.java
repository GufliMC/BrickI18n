package com.guflimc.brick.i18n.api.namespace;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

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

    public final ChatMenuBuilder chatMenu(T subject) {
        return new ChatMenuBuilder(subject);
    }

    public class ChatMenuBuilder {

        private final static PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

        public class Button {
            Component text;
            Component hover;
            ClickEvent clickEvent;

            private Button(Component text, Component hover, ClickEvent clickEvent) {
                this.text = text;
                this.hover = hover;
                this.clickEvent = clickEvent;
            }
        }

        private final T subject;

        private Component message = Component.text("");
        private Component dividerPattern = Component.text("-", NamedTextColor.WHITE);

        private final List<Button> buttons = new ArrayList<>();

        public ChatMenuBuilder(T subject) {
            this.subject = subject;
        }

        public ChatMenuBuilder withMessage(@NotNull Component message) {
            this.message = message;
            return this;
        }

        public ChatMenuBuilder withMessage(@NotNull String key, Object... args) {
            return withMessage(translate(subject, key, args));
        }

        public ChatMenuBuilder withMessage(@NotNull TranslatableComponent component) {
            return withMessage(translate(subject, component));
        }

        public ChatMenuBuilder withDividerPattern(@NotNull Component component) {
            this.dividerPattern = component;
            return this;
        }

        public ChatMenuBuilder withDividerPattern(@NotNull TranslatableComponent component) {
            return withMessage(translate(subject, component));
        }

        public ChatMenuBuilder addButton(@NotNull String msgKey, String hoverKey, @NotNull ClickEvent clickEvent) {
            buttons.add(new Button(translate(subject, msgKey), translate(subject, hoverKey), clickEvent));
            return this;
        }

        public ChatMenuBuilder addButton(@NotNull String msgKey, @NotNull ClickEvent clickEvent) {
            buttons.add(new Button(translate(subject, msgKey), null, clickEvent));
            return this;
        }

        public void send() {
            Audience audience = audience(subject);
            int msgLength = Math.min(serializer.serialize(message).length(), 60);
            Component[] result = compile(msgLength, buttons);

            Component divider = Component.text("");
            for ( int i = 0; i < msgLength * 0.9; i++ ) {
                divider = divider.append(dividerPattern);
            }

            audience.sendMessage(divider);

            if (message != null) audience.sendMessage(message);
            audience.sendMessage(Component.text(""));

            Arrays.stream(result).forEach(r -> {
                audience.sendMessage(r);
                audience.sendMessage(Component.text(""));
            });

            audience.sendMessage(divider);
        }

        private Component[] compile(int msgLength, List<Button> buttons) {
            int btnLength = buttons.stream().mapToInt(b -> serializer.serialize(b.text).length()).sum();
            int gap = Math.max(1, (msgLength - btnLength) / (buttons.size() + 1));

            if (gap <= 2) {
                int split = (int) Math.ceil(buttons.size() / 2.d);
                return Stream.concat(
                                Arrays.stream(compile(msgLength, buttons.subList(0, split))),
                                Arrays.stream(compile(msgLength, buttons.subList(split, buttons.size()))))
                        .toArray(Component[]::new);
            }

            Component result = Component.text("");
            for (Button btn : buttons) {
                result = result.append(Component.text(" ".repeat(gap)));
                Component text = btn.text.clickEvent(btn.clickEvent);
                if (btn.hover != null) text = text.hoverEvent(HoverEvent.showText(btn.hover));
                result = result.append(text);
            }

            return new Component[]{result};
        }
    }

}
