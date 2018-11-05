package org.typemeta.funcj.codec;

/**
 * Interface for constructing an uninitialised value of type {@code T},
 * using a constructor which takes an array of objects.
 * @param <T>       the type of value to be constructed
 */
public interface ArgArrayTypeCtor<T> {

    /**
     * Construct a value of type {@code T}.
     * @param args      the array of argument values
     * @return          the newly constructed value
     */
    T construct(Object[] args);
}
