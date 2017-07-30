package org.funcj.util;

import org.funcj.util.Functions.F;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Utility functions for mapping functions over container types.
 */
public abstract class Functors {
    /**
     * Map a function over a {@link java.util.Collection}.
     */
    public static <T, U> List<U> map(F<T, U> f, Collection<T> ts) {
        return ts.stream().map(f::apply).collect(toList());
    }

    /**
     * Map a function over a {@link java.lang.Iterable}.
     */
    public static <T, U> List<U> map(F<T, U> f, Iterable<T> iter) {
        return StreamSupport.stream(
                iter.spliterator(),
                false).map(f::apply).collect(toList()
        );
    }

    /**
     * Map a function over an {@link java.util.Optional}.
     */
    public static <T, U> Optional<U> map(F<T, U> f, Optional<T> ot) {
        return ot.map(f::apply);
    }

    /**
     * Map a function over an array.
     */
    public static <A, B> B[] map(A[] from, B[] to, F<A, B> f) {
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
