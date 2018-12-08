package org.typemeta.funcj.codec.bytes;

import java.util.Map;

/**
 * Interface for constructing an uninitialised value of type {@code T},
 * using a constructor which takes a map field name to argument values.
 * @param <T>       the type of value to be constructed
 */
public interface ArgMapTypeCtor<T> {

    /**
     * Construct a value of type {@code T}.
     * @param args      the map of arguments
     * @return          the newly constructed value
     */
    T construct(Map<String, Object> args);
}
