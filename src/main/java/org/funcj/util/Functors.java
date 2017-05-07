package org.funcj.util;

import org.funcj.util.Functions.F;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Utility functions for mapping functions over container types.
 */
public abstract class Functors {
    /**
     * Map a function over a collection.
     */
    public static <T, U> List<U> map(F<T, U> f, Collection<T> ts) {
        return ts.stream().map(f::apply).collect(toList());
    }

    /**
     * Map a function over an optional.
     */
    public static <T, U> Optional<U> map(F<T, U> f, Optional<T> ot) {
        return ot.map(f::apply);
    }
}
