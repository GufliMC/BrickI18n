package com.guflimc.brick.i18n.api;

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

}
