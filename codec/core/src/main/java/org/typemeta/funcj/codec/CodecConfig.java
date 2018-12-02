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
     * Classes in allowed packages can be constructed from their name.
     * @param pkg       the package
     */
    void registerAllowedPackage(Package pkg);

    /**
     * Add this class to the list of allowed classes.
     * Allowed classes can be constructed from their name.
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
     * Register a type proxy for a class.
     * A type proxy is used in place of a class when selecting its {@code Codec}.
     * @param clazz     the class to be mapped
     * @param proxy     the proxy type
     */
    void registerTypeProxy(Class<?> clazz, Class<?> proxy);

    /**
     * Map a class to its proxy if it has one.
     * @param clazz     the class
     * @param <T>       the class type
     * @return          the proxy if it has one, otherwise the original class
     */
    <T> Class<T> mapToProxy(Class<T> clazz);

    /**
     * Register a type alias.
     * A type alias is used in place of the class name in the encoded data.
     * @param clazz    the clas to be mapped
     * @param name     the proxy type
     */
    void registerTypeAlias(Class<?> clazz, String name);

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

    /**
     * Determine the name to use for a field.
     * @param field     the field
     * @param depth     the inheritance depth (increases as we go up the hierarchy)
     * @param existingNames the set of existing field names
     * @return          the field name
     */
    String getFieldName(Field field, int depth, Set<String> existingNames);

    /**
     * Return the default size for arrays created when decoding data.
     * @return          the default size for arrays
     */
    int defaultArraySize();

    /**
     * Return the new size for arrays that are full.
     * @param size      the current size of the array to be re-sized
     * @return          the new array size
     */
    int resizeArray(int size);

    <T> void registerDefaultSubType(Class<T> stcClass, Class<? extends T> dynClass);

    <T, U extends T> Class<U> getDefaultSubType(Class<T> stcClass);

    /**
     * Check whether two types match or not.
     * @param stcClass  the static type
     * @param dynClass  the dynamic type
     * @param <T>       the type type
     * @return          true if the types match
     */
    <T> boolean dynamicTypeMatch(Class<T> stcClass, Class<? extends T> dynClass);
}
