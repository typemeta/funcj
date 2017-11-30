package org.typemeta.funcj.algebra;

import org.typemeta.funcj.util.Folds;

/**
 * A monoid has a combine operation and an identity value, a.k.a. zero.
 * @param <T>       the type for which we're defining a monoid instance
 */
public interface Monoid<T> {
    /**
     * The identity value for the {@code combine} operation.
     * @return          the identity value
     */
    T zero();

    /**
     * An associative operation that combines two values to form a new value.
     * @param x         the first value
     * @param y         the second value
     * @return          the combined value
     */
    T combine(T x, T y);

    /**
     * Combine all elements in an iterable collection.
     * @param iter      the iterable collection
     * @return          the result of combining all elements of the iterable
     */
    default T combineAll(Iterable<T> iter) {
        return Folds.foldLeft(this::combine, zero(), iter);
    }
}
