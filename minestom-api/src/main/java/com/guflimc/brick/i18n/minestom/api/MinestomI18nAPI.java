package com.guflimc.brick.i18n.minestom.api;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespace;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespaceRegistry;
import org.jetbrains.annotations.ApiStatus;

public class MinestomI18nAPI {

    private static MinestomNamespaceRegistry namespaceRegistry;

    @ApiStatus.Internal
    public static void setNamespaceRegistry(MinestomNamespaceRegistry registry) {
        namespaceRegistry = registry;
        I18nAPI.setNamespaceRegistry(registry);
    }

    //

    public static MinestomNamespaceRegistry get() {
        return namespaceRegistry;
    }

    public static MinestomNamespace global() {
        return get().byId("global");
    }

    public static MinestomNamespace get(String namespace) {
        return get().byId(namespace);
    }

    public static MinestomNamespace get(Object object) {
        return get().byObject(object);
    }

}
