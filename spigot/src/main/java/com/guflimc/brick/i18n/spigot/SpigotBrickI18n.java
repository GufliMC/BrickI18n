package com.guflimc.brick.i18n.spigot;

import com.guflimc.brick.i18n.api.objectmapper.ObjectMapper;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespaceRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBrickI18n extends JavaPlugin {

    @Override
    public void onEnable() {
        // object mappers
        ObjectMapper.register(Location.class, loc -> Component.text(String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ())));

        // registry
        SpigotNamespaceRegistry registry = new SpigotNamespaceRegistry(this);
        SpigotI18nAPI.setNamespaceRegistry(registry);

        // global namespace
        SpigotNamespace global = registry.byId("global");
        global.loadValues(this, "languages");

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getDescription().getName() + " v" + getDescription().getVersion();
    }
}
