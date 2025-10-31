package org.edward.pandora.onion;

@FunctionalInterface
public interface ConvertFunction<T, E, V> {
    V apply(T target, E env) throws Exception;
}