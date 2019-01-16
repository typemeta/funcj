package org.typemeta.funcj.algebra;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Folds;

import java.util.stream.Stream;

/**
 * A monoid has a combine operation and an identity value, a.k.a. zero.
 * @param <T>       the value type for which we're defining a monoid instance
 */
public interface Monoid<T> {
    /**
     * Create a monoid instance from the given values.
     * @param zero      the identity value
     * @param combine   the combine function
     * @param <T>       the monoid value type
     * @return          the monoid instance
     */
    static <T> Monoid<T> of(T zero, Functions.Op2<T> combine) {
        return new Monoid<T>() {

            @Override
            public T zero() {
                return zero;
            }

            @Override
            public T combine(T x, T y) {
                return combine.apply(x, y);
            }
        };
    }

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

    /**
     * Combine all elements in a stream.
     * @param str       the stream of values
     * @return          the result of combining all elements of the iterable
     */
    default T combineAll(Stream<T> str) {
        return Folds.foldLeft(this::combine, zero(), str);
    }
}
