package com.guflimc.brick.i18n.spigot.api.namespace;

import com.guflimc.brick.i18n.api.NamespaceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

public class SpigotNamespaceRegistry extends NamespaceRegistry<SpigotNamespace> {

    public SpigotNamespaceRegistry(JavaPlugin plugin) {
        register(new SpigotNamespace("global", plugin, Locale.ENGLISH));
    }

    @Override
    public SpigotNamespace byClass(@NotNull Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getClass().getClassLoader().equals(classLoader))
                .findAny()
                .map(plugin -> byId(plugin.getName()))
                .orElse(byId("global"));
    }

    public SpigotNamespace byPlugin(JavaPlugin plugin) {
        return byId(plugin.getName());
    }

}
