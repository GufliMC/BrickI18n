package com.guflimc.brick.i18n.api.namespace;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public abstract class StandardComponentRenderer extends TranslatableComponentRenderer<Locale> {

    public static @NotNull StandardComponentRenderer usingTranslationSource(final @NotNull Translator source) {
        requireNonNull(source, "source");
        return new StandardComponentRenderer() {
            @Override
            protected @Nullable MessageFormat translate(final @NotNull String key, @NotNull Locale context) {
                return source.translate(key, context);
            }
        };
    }

    @Override
    protected @NotNull Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull Locale context) {
        final @Nullable MessageFormat format = this.translate(component.key(), context);
        if (format == null) {
            return Component.text(component.key());
        }

        String x = format.format(new Object[0]);
        Component result = MiniMessage.miniMessage().deserialize(x);
        final List<Component> args = component.args();
        for (int i = 0; i < args.size(); i++) {
            final int index = i;
            result = result.replaceText(builder -> builder.match("(\\{[" + Pattern.quote(index + "") + "]})")
                    .replacement((matchResult, textbuilder) -> args.get(index)));
        }

        return result;
    }
}
