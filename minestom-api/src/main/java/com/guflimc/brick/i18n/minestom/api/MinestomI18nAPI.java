package com.guflimc.brick.i18n.minestom.api;

import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespace;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespaceRegistry;

public class MinestomI18nAPI {

    private static MinestomNamespaceRegistry namespaceRegistry;

    public static void setNamespaceRegistry(MinestomNamespaceRegistry registry) {
        namespaceRegistry = registry;
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
