package com.guflimc.brick.i18n.minestom.api.namespace;

import com.guflimc.brick.i18n.api.NamespaceRegistry;
import net.minestom.server.extensions.ExtensionClassLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MinestomNamespaceRegistry implements NamespaceRegistry<MinestomNamespace> {

    private final Logger LOGGER = LoggerFactory.getLogger(MinestomNamespaceRegistry.class);

    private final Map<String, MinestomNamespace> namespaces = new HashMap<>();

    public MinestomNamespaceRegistry() {
        register(new MinestomNamespace("global", Locale.ENGLISH));
    }

    @Override
    public void register(@NotNull MinestomNamespace namespace) {
        namespaces.put(namespace.id(), namespace);
    }

    @Override
    public MinestomNamespace byId(@NotNull String namespace) {
        if ( namespaces.containsKey(namespace) ) {
            return namespaces.get(namespace);
        }
        return namespaces.get("global"); // default
    }

    @Override
    public MinestomNamespace byObject(@NotNull Object object) {
        ClassLoader classLoader = object.getClass().getClassLoader();
        if ( classLoader instanceof ExtensionClassLoader ecl ) {
            return byId(ecl.getName().substring(4));
        }
        return namespaces.get("global"); // default
    }

}
