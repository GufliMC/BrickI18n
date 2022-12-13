package com.guflimc.brick.i18n.api.tests;

import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import com.guflimc.brick.i18n.api.time.DurationFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class I18nTests {

    @Test
    public void translationNamespaceTest() {
        StandardNamespace global = new StandardNamespace("global", Locale.ENGLISH);
        global.loadValues(getClass().getClassLoader().getResource("languages/en.json"));

        Component translated = global.translate(Locale.ENGLISH, Component.translatable("test.message")
                .args(Component.text("there")));

        String result = PlainTextComponentSerializer.plainText().serialize(translated);
        assertEquals("Hey there!", result);
    }

    @Test
    public void durationFormatterTest() {
        Duration a = Duration.of(17, ChronoUnit.HOURS)
                .plus(48, ChronoUnit.MINUTES)
                .plus(3, ChronoUnit.SECONDS);

        assertEquals("17:48:03", DurationFormatter.COMPACT.format(a));
        assertEquals("17h 48m 03s ", DurationFormatter.COZY.format(a));

        Duration b = Duration.of(96_706_083, ChronoUnit.SECONDS);

        assertEquals("03:23:06:48:03", DurationFormatter.COMPACT.format(b));
        assertEquals("03y 23d 06h 48m 03s ", DurationFormatter.COZY.format(b));
    }

}
