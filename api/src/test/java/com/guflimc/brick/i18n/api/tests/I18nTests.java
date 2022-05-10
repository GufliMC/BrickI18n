package com.guflimc.brick.i18n.api.tests;

import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

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

}
