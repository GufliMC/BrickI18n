package com.guflimc.brick.i18n.spigot.api;

import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespaceRegistry;

public class SpigotI18nAPI {

    private static SpigotNamespaceRegistry namespaceRegistry;

    public static void setNamespaceRegistry(SpigotNamespaceRegistry registry) {
        namespaceRegistry = registry;
    }

    //

    public static SpigotNamespaceRegistry get() {
        return namespaceRegistry;
    }

    public static SpigotNamespace global() {
        return get().byId("global");
    }

    public static SpigotNamespace get(String namespace) {
        return get().byId(namespace);
    }

    public static SpigotNamespace get(Object object) {
        return get().byObject(object);
    }

}
