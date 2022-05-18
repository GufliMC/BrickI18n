package com.guflimc.brick.i18n.api;

import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import org.jetbrains.annotations.ApiStatus;

public class I18nAPI {

    private static NamespaceRegistry<?> namespaceRegistry;

    @ApiStatus.Internal
    public static void setNamespaceRegistry(NamespaceRegistry<?> registry) {
        namespaceRegistry = registry;
    }

    //

    /**
     * Get registered namespace registry.
     */
    public static NamespaceRegistry<?> get() {
        return namespaceRegistry;
    }


    public static StandardNamespace global() {
        return get().byId("global");
    }

    public static StandardNamespace get(String namespace) {
        return get().byId(namespace);
    }

    public static StandardNamespace get(Object object) {
        return get().byObject(object);
    }

}
