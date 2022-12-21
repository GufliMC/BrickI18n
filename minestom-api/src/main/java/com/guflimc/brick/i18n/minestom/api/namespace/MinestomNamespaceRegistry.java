package com.guflimc.brick.i18n.minestom.api.namespace;

import com.guflimc.brick.i18n.api.NamespaceRegistry;
import net.minestom.server.extensions.ExtensionClassLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class MinestomNamespaceRegistry extends NamespaceRegistry<MinestomNamespace> {

    public MinestomNamespaceRegistry() {
        register(new MinestomNamespace("global", Locale.ENGLISH));
    }

    @Override
    public MinestomNamespace byClass(@NotNull Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader instanceof ExtensionClassLoader ecl) {
            return byId(ecl.getName().substring(4));
        }
        return byId("global"); // default
    }

}
