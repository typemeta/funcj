package org.typemeta.funcj.codec;

/**
 * Interface for classes which provide configuration information
 * for CodecCore implementations.
 */
public interface CodecConfig {

    /**
     * Convert a class to a name.
     * @param clazz     the class
     * @return          the name
     */
    String classToName(Class<?> clazz);

    /**
     * Map one or  more classes to a name.
     * @param clazz     the class
     * @param classes   the classes
     * @return          the name
     */
    String classToName(Class<?> clazz, Class<?>... classes);

    /**
     * Convert a name back to a class.
     * @param name      the name
     * @param <T>       the class type
     * @return          the class value
     */
    <T> Class<T> nameToClass(String name);

    /**
     * Return the default size for arrays.
     * @return          the default size for arrays
     */
    int defaultArrSize();
}
