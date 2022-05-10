package com.guflimc.brick.i18n.minestom;

import com.guflimc.brick.i18n.minestom.api.MinestomI18nAPI;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespace;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespaceRegistry;
import net.minestom.server.extensions.Extension;

public class MinestomBrickI18n extends Extension {

    @Override
    public void initialize() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        MinestomNamespaceRegistry registry = new MinestomNamespaceRegistry();
        MinestomI18nAPI.setNamespaceRegistry(registry);

        // global namespace
        MinestomNamespace global = registry.byId("global");
        global.loadValues(this, "languages");

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void terminate() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getOrigin().getName() + " v" + getOrigin().getVersion();
    }

}
