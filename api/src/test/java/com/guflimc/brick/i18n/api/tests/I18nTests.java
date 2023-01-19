package com.guflimc.brick.i18n.api.tests;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.api.NamespaceRegistry;
import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import com.guflimc.brick.i18n.api.time.BrickChronoUnit;
import com.guflimc.brick.i18n.api.time.DurationParser;
import com.guflimc.brick.i18n.api.time.DurationFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class I18nTests {

    private static StandardNamespace global;

    @BeforeAll
    public static void setup() {
        global = new StandardNamespace("global", Locale.ENGLISH);
        global.loadValues(I18nTests.class.getClassLoader().getResource("languages/en.json"));

        NamespaceRegistry<StandardNamespace> registry = new NamespaceRegistry<>();
        registry.register(global);

        I18nAPI.setNamespaceRegistry(registry);
    }

    @Test
    public void messageTest() {
        Component translated = global.translate(Locale.ENGLISH, Component.translatable("test.message")
                .args(Component.text("there")));

        String result = PlainTextComponentSerializer.plainText().serialize(translated);
        assertEquals("Hey there!", result);
    }

    @Test
    public void durationTranslateTest() {
        Duration d = Duration.ofDays(7).plusMinutes(1).plusSeconds(27);
        Component translated = global.translate(Locale.ENGLISH, d);

        String result = PlainTextComponentSerializer.plainText()
                .serialize(translated);

        assertEquals("7 days, 1 minute, 27 seconds", result);
    }

    @Test
    public void durationFormatterTest() {
        Duration a = Duration.ZERO
                .plus(17, ChronoUnit.HOURS)
                .plus(0, ChronoUnit.MINUTES)
                .plus(3, ChronoUnit.SECONDS);
        assertEquals("17H 0M 3S", DurationFormatter.COZY.format(a));
        assertEquals("17H 3S", DurationFormatter.COMPACT.format(a));
        assertEquals("17H", DurationFormatter.HIGHEST.format(a));
        assertEquals("17:00:03", DurationFormatter.DIGITAL.format(a));

        Duration b = Duration.ZERO
                        .plus(2, BrickChronoUnit.YEARS)
                        .plus(5, BrickChronoUnit.MONTHS)
                        .plus(17, ChronoUnit.HOURS);
        assertEquals("2y 5m 0d 17H", DurationFormatter.COZY.format(b));
        assertEquals("2y 5m 17H", DurationFormatter.COMPACT.format(b));
        assertEquals("2y", DurationFormatter.HIGHEST.format(b));

        Duration c = Duration.ZERO
                .plus(49, ChronoUnit.HOURS);
        assertEquals("2d 1H", DurationFormatter.COZY.format(c));
        assertEquals("2d", DurationFormatter.HIGHEST.format(c));

        Duration d = Duration.ZERO;
        assertEquals("0S", DurationFormatter.COZY.format(d));
        assertEquals("0S", DurationFormatter.COMPACT.format(d));
    }

    @Test
    public void durationParserTest() {
        assertEquals("1d 17H 4M", DurationFormatter.COZY.format(DurationParser.parse("1d 17H 04M")));
        assertEquals("1d 17H 4M", DurationFormatter.COZY.format(DurationParser.parse("1d17H04M")));
        assertEquals("1d 12H", DurationFormatter.COZY.format(DurationParser.parse("36H")));

        assertThrows(DateTimeParseException.class, () -> DurationParser.parse("2d 48H"));
        assertThrows(DateTimeParseException.class, () -> DurationParser.parse("2d xH"));
        assertThrows(DateTimeParseException.class, () -> DurationParser.parse("2d zz"));
    }

    @Test
    public void maybeTranslateTest() {
        Component translateThis = Component.text("Blah blah. ")
                .append(Component.text("It's been time.years."));

        Component translated = global.maybeTranslate(Locale.ENGLISH, translateThis);

        String result = PlainTextComponentSerializer.plainText().serialize(translated);
        assertEquals("Blah blah. It's been years.", result);
    }

}
