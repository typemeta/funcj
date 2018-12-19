package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.CodecException;

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
     * @param clazz     the clas to be mapped
     * @param name      the proxy type
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

    /**
     * Register a collection implementation as a default for that collection type.
     * This prevents the implementation type name from being encoded.
     * @param intfClass the collection interface
     * @param implClass the collection implementation
     * @param <T>       the collection interface type
     */
    <T> void registerDefaultCollectionType(Class<T> intfClass, Class<? extends T> implClass);

    /**
     * Check whether a collection class is a default for that collection type.
     * @param intfClass the collection interface
     * @param implClass the collection implementation
     * @return          true if the collection implementation is registered as the default for that type
     */
    boolean isDefaultCollectionType(Class<?> intfClass, Class<?> implClass);

    /**
     * Return the primary default collection implementation for the given collection type
     * @param intfClass the collection interface
     * @param <T>       the collection interface type
     * @param <U>       the collection implementation type
     * @return          the primary default collection implementation for the given collection type
     */
    <T, U> Class<U> getDefaultCollectionType(Class<T> intfClass);

    /**
     * Check whether two types match or not.
     * @param stcClass  the static type
     * @param dynClass  the dynamic type
     * @param <T>       the type type
     * @return          true if the types match
     */
    <T> boolean dynamicTypeMatch(Class<T> stcClass, Class<? extends T> dynClass);

    /**
     * Specify whether dynamic type information should be encoded.
     * By default this is enabled.
     * Disabling it may prevent data from round-tripping successfully.
     * @param enable    specify whether dynamic type information should be encoded
     */
    void dynamicTypeTags(boolean enable);

    /**
     * Indicate whether dynamic type information should be encoded.
     * @return          whether dynamic type information should be encoded
     */
    boolean dynamicTypeTags();

    /**
     * Specify whether the codec should fail if no type constructor is found.
     * By default this is enabled.
     * Disabling it may prevent data from round-tripping successfully.
     * @param enable    specify whether the the codec should fail if no type constructor is found.
     */
    void failOnNoTypeConstructor(boolean enable);

    /**
     * Indicate whether the codec should fail if no type constructor is found.
     * @return          whether the the codec should fail if no type constructor is found
     */
    boolean failOnNoTypeConstructor();

    void failOnUnrecognisedFields(boolean enable);

    boolean failOnUnrecognisedFields();
}
