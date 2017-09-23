package io.typemeta.funcj.util;

import io.typemeta.funcj.util.Functions.F;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Utility functions for mapping functions over Functor types, such as Collections.
 */
public abstract class Functors {
    /**
     * Map a function over a {@link Collection}.
     * @param f         the function to apply to each element of the collection
     * @param ts        the collection
     * @param <T>       the element type of the collection
     * @param <U>       the return type of the function
     * @return          a collection consisting of the results of applying the function to
     *                  each element in the input collection
     */
    public static <T, U> List<U> map(F<T, U> f, Collection<T> ts) {
        return ts.stream().map(f::apply).collect(toList());
    }

    /**
     * Map a function over a {@link Iterable}.
     * @param f         the function to apply to each element provided by the {@code Iterable}
     * @param iter      the iterable
     * @param <T>       the element type of the iterable
     * @param <U>       the return type of the function
     * @return          a collection consisting of the results of applying the function to
     *                  each element in the input iterable
     */
    public static <T, U> List<U> map(F<T, U> f, Iterable<T> iter) {
        return StreamSupport.stream(
                iter.spliterator(),
                false).map(f::apply).collect(toList()
        );
    }

    /**
     * Map a function over an {@link Optional}.
     * @param f         the function to apply to the value within the {@code Optional}
     * @param ot        the optional value
     * @param <T>       the value type within the {@code Optional}
     * @param <U>       the return type of the function
     * @return          an {@code Optional} that contains the result of applying the function
     */
    public static <T, U> Optional<U> map(F<T, U> f, Optional<T> ot) {
        return ot.map(f::apply);
    }

    /**
     * Map a function over an array.
     * @param f         the function to apply to each element of the array
     * @param from      the input array
     * @param to        the output array
     * @param <A>       the element type of the input array
     * @param <B>       the element type of the output array
     * @return          the output array
     */
    public static <A, B> B[] map(F<A, B> f, A[] from, B[] to) {
        final int l = from.length;
        if (to.length != l) {
            final Class<?> type = to.getClass();
            to = (type == Object[].class)
                    ? (B[]) new Object[l]
                    : (B[]) Array.newInstance(type.getComponentType(), l);
        }

        for (int i = 0; i < from.length; ++i) {
            to[i] = f.apply(from[i]);
        }

        return to;
    }
}
