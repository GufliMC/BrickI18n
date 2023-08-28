package com.guflimc.brick.i18n.api.objectmapper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectMapper<T> {

    private static final List<ObjectMapper<?>> mappers = new ArrayList<>();

    public static Component map(Object object) {

        // first try exact match
        for ( ObjectMapper<?> mapper : mappers ) {
            if ( mapper.type().equals(object.getClass()) ) {
                return mapper.mapObject(object);
            }
        }

        // give higher priority to mappers added later
        List<ObjectMapper<?>> mappers = new ArrayList<>(ObjectMapper.mappers);
        Collections.reverse(mappers);

        // then try superclass match
        for ( ObjectMapper<?> mapper : mappers ) {
            if ( mapper.type().isAssignableFrom(object.getClass()) ) {
                return mapper.mapObject(object);
            }
        }

        return Component.text(object.toString());
    }

    public static <T> void register(Class<T> type, Function<T, Component> mapper) {
        mappers.add(new ObjectMapper<>(type, mapper));
    }

    //

    static {
        // component
        register(Component.class, Function.identity());

        // strings
        register(String.class, Component::text);

        // collections
        JoinConfiguration joinConfiguration = JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY));
        register(Collection.class, coll -> {
            if ( coll.isEmpty() ) {
                return Component.text("[]");
            }

            Collection<Component> components = ((Collection<?>) coll).stream()
                    .map(ObjectMapper::map)
                    .collect(Collectors.toList());

            return Component.join(joinConfiguration, components);
        });
    }

    //

    private final Class<T> type;
    private final Function<T, Component> mapper;

    private ObjectMapper(Class<T> type, Function<T, Component> mapper) {
        this.type = type;
        this.mapper = mapper;
    }

    public Class<T> type() {
        return type;
    }

    private Component mapObject(Object obj) {
        return ((Function<Object, Component>) mapper).apply(obj);
    }

}
