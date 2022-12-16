package com.guflimc.brick.i18n.api;

import com.guflimc.brick.i18n.api.namespace.StandardNamespace;
import org.jetbrains.annotations.NotNull;

public interface NamespaceRegistry<T extends StandardNamespace> {

    void register(@NotNull T namespace);

    T byId(@NotNull String namespace);

    T byObject(@NotNull Object object);

    T byClass(@NotNull Class<?> clazz);
}
