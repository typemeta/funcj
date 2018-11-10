package org.typemeta.funcj.codec;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Interface for classes which provide configuration information
 * for CodecCore implementations.
 */
public interface CodecConfig {

    /**
     * Add this package to the list of allowed packages.
     * @param pkg       the package
     */
    void registerAllowedPackage(Package pkg);

    /**
     * Add this class to the list of allowed classes.
     * @param clazz     the class
     */
    void registerAllowedClass(Class<?> clazz);

    /**
     * Check this class is allowed.
     * @param clazz     the class
     * @param <T>       the class type
     * @return          the class, if it is allowed, otherwisde throws a {@link CodecException}
     */
    <T> Class<T> checkClassIsAllowed(Class<T> clazz);

    /**
     * Register a type proxy.
     * A type proxy maps a type to its proxy before selecting its {@code Codec}.
     * @param clazz     the type to be mapped
     * @param proxy     the proxy type
     */
    void registerTypeProxy(Class<?> clazz, Class<?> proxy);

    /**
     * Map a class to its proxy if it has one.
     * @param clazz     the class
     * @param <T>       the class type
     * @return          the proxy if it has one, otherwise the original class
     */
    <T> Class<T> remapType(Class<T> clazz);

    /**
     * Convert a class to a name.
     * @param clazz     the class
     * @return          the name
     */
    String classToName(Class<?> clazz);

    /**
     * Convert a name back to a class.
     * @param name      the name
     * @param <T>       the class type
     * @return          the class value
     */
    <T> Class<T> nameToClass(String name);

    String getFieldName(Field field, int depth, Set<String> existingNames);

    /**
     * Return the default size for arrays created when decoding data.
     * @return          the default size for arrays
     */
    int defaultArraySize();
}
