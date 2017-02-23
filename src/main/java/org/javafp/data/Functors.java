package org.javafp.data;

import org.javafp.util.Functions;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Utility functions for mapping functions over collection types.
 */
public abstract class Functors {
    /**
     * Map a function over a map.
     */
    public static <A, B> B[] map(A[] from, Functions.F<A, B> f, B[] to) {
        final int l = from.length;
        if (l != from.length) {
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

    /**
     * Map a function over a list.
     */
    public static <A, B> List<B> map(List<A> l, Functions.F<A, B> f) {
        final int n = l.size();
        final List<B> r = new ArrayList<B>(n);
        for (int i = 0; i < n; ++i) {
            r.add(f.apply(l.get(i)));
        }

        return r;
    }
}
