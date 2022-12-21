package com.guflimc.brick.i18n.api;

import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NamespaceRegistry<T extends StandardNamespace> {

    private final Map<String, T> namespaces = new HashMap<>();

    public final void register(@NotNull T namespace) {
        namespaces.put(namespace.id(), namespace);
    }

    public final T byId(@NotNull String namespace) {
        if (namespaces.containsKey(namespace)) {
            return namespaces.get(namespace);
        }
        return namespaces.get("global"); // default
    }

    public T byObject(@NotNull Object object) {
        return byClass(object.getClass());
    }

    public T byClass(@NotNull Class<?> clazz) {
        throw new UnsupportedOperationException("This operation is not available in the standard registry.");
    }

}
