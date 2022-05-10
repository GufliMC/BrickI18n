package com.guflimc.brick.i18n.spigot.namespace;

import com.guflimc.brick.i18n.api.NamespaceRegistry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SpigotNamespaceRegistry implements NamespaceRegistry<SpigotNamespace> {

    private final Logger LOGGER = LoggerFactory.getLogger(SpigotNamespaceRegistry.class);

    private final Map<String, SpigotNamespace> namespaces = new HashMap<>();

    public SpigotNamespaceRegistry() {
        register(new SpigotNamespace("global", Locale.ENGLISH));
    }

    @Override
    public void register(@NotNull SpigotNamespace namespace) {
        namespaces.put(namespace.id(), namespace);
    }

    @Override
    public SpigotNamespace byId(@NotNull String namespace) {
        if ( namespaces.containsKey(namespace) ) {
            return namespaces.get(namespace);
        }
        return namespaces.get("global"); // default
    }

    @Override
    public SpigotNamespace byObject(@NotNull Object object) {
        ClassLoader classLoader = object.getClass().getClassLoader();
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getClass().getClassLoader().equals(classLoader))
                .findAny()
                .map(plugin -> byId(plugin.getName()))
                .orElse(namespaces.get("global"));
    }

}
