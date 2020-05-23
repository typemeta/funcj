package org.typemeta.funcj.util;

import org.typemeta.funcj.functions.FunctionsGenEx.*;
import org.typemeta.funcj.tuples.Tuple2;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;
import static org.typemeta.funcj.util.Exceptions.*;

/**
 * Utility functions for mapping functions over Functor types, such as Collections.
 * Specialised for lambdas that may throw.
 */
public abstract class FunctorsGenEx {
    /**
     * Map a function over a {@link Collection}.
     * @param f         the function to apply to each element of the collection
     * @param ts        the collection
     * @param <T>       the element type of the collection
     * @param <U>       the return type of the function
     * @param <X>       the exception type
     * @return          a collection consisting of the results of applying the function to
     *                  each element in the input collection
     * @throws X        the exception thrown by the function
     */
    public static <T, U, X extends Exception> List<U> map(F<T, U, X> f, Collection<T> ts) throws X {
        return unwrap(() -> ts.stream().map(wrap(f::apply)::apply).collect(toList()));
    }

    /**
     * Map a function over a {@link Iterable}.
     * @param f         the function to apply to each element provided by the {@code Iterable}
     * @param iter      the iterable
     * @param <T>       the element type of the iterable
     * @param <U>       the return type of the function
     * @param <X>       the exception type
     * @return          a collection consisting of the results of applying the function to
     *                  each element in the input iterable
     * @throws X        the exception thrown by the function
     */
    public static <T, U, X extends Exception> List<U> map(F<T, U, X> f, Iterable<T> iter) throws X {
        return unwrap(() -> StreamSupport.stream(
                iter.spliterator(),
                false).map(wrap(f::apply)::apply).collect(toList()
        ));
    }

    /**
     * Map a function over an {@link Optional}.
     * @param f         the function to apply to the value within the {@code Optional}
     * @param ot        the optional value
     * @param <T>       the value type within the {@code Optional}
     * @param <U>       the return type of the function
     * @param <X>       the exception type
     * @return          an {@code Optional} that contains the result of applying the function
     * @throws X        the exception thrown by the function
     */
    public static <T, U, X extends Exception> Optional<U> map(F<T, U, X> f, Optional<T> ot) throws X {
        return unwrap(() -> ot.map(wrap(f::apply)::apply));
    }

    /**
     * Map a function over an array.
     * @param f         the function to apply to each element of the array
     * @param from      the input array
     * @param to        the output array
     * @param <A>       the element type of the input array
     * @param <B>       the element type of the output array
     * @param <X>       the exception type
     * @return          the output array
     * @throws X        the exception thrown by the function
     */
    @SuppressWarnings("unchecked")
    public static <A, B, X extends Exception> B[] map(F<A, B, X> f, A[] from, B[] to) throws X {
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

    /**
     * Map a function over a {@link Map}.
     * Apply a binary function to each entry in a {@code Map} to obtain the new {@code Map} value.
     * The keys are unchanged.
     * @param f         the function
     * @param m         the map
     * @param <K>       the map key type
     * @param <V>       the map value type
     * @param <W>       the new map value type
     * @param <X>       the exception type
     * @return          the new map
     * @throws X        the exception thrown by the function
     */
    public static <K, V, W, X extends Exception> Map<K, W> map(F2<K, V, W, X> f, Map<K, V> m) throws X {
        return Exceptions.<Map<K, W>, X>unwrap(() ->
                Streams.tupleStream(m)
                        .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(wrap(f::apply))))
                        .collect(toMap(Tuple2::get1, Tuple2::get2))
        );
    }

    /**
     * Map a function over a {@link SortedMap}.
     * Apply a binary function to each entry in a {@code SortedMap} to obtain the new {@code SortedMap} value.
     * The keys are unchanged.
     * @param f         the function
     * @param m         the map
     * @param <K>       the map key type
     * @param <V>       the map value type
     * @param <W>       the new map value type
     * @param <X>       the exception type
     * @return          the new map
     * @throws X        the exception thrown by the function
     */
    public static <K, V, W, X extends Exception> SortedMap<K, W> map(F2<K, V, W, X> f, SortedMap<K, V> m) throws X {
        return Exceptions.<SortedMap<K, W>, X>unwrap(() ->
                Streams.tupleStream(m)
                        .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(wrap(f::apply))))
                        .collect(toMap(Tuple2::get1, Tuple2::get2, FunctorsGenEx::throwingMerger, TreeMap::new))
        );
    }

    /**
     * Map a function over a {@link ConcurrentMap}.
     * Apply a binary function to each entry in a {@code ConcurrentMap} to obtain the new {@code ConcurrentMap} value.
     * The keys are unchanged.
     * @param f         the function
     * @param m         the map
     * @param <K>       the map key type
     * @param <V>       the map value type
     * @param <W>       the new map value type
     * @param <X>       the exception type
     * @return          the new map
     * @throws X        the exception thrown by the function
     */
    public static <K, V, W, X extends Exception> ConcurrentMap<K, W> map(F2<K, V, W, X> f, ConcurrentMap<K, V> m) throws X {
        return Exceptions.<ConcurrentMap<K, W>, X>unwrap(() ->
                Streams.tupleStream(m)
                        .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(wrap(f::apply))))
                        .collect(toConcurrentMap(Tuple2::get1, Tuple2::get2))
        );
    }

    /**
     * Map a function over a {@link LinkedHashMap}.
     * Apply a binary function to each entry in a {@code LinkedHashMap} to obtain the new {@code LinkedHashMap} value.
     * The keys are unchanged.
     * @param f         the function
     * @param m         the map
     * @param <K>       the map key type
     * @param <V>       the map value type
     * @param <W>       the new map value type
     * @param <X>       the exception type
     * @return          the new map
     * @throws X        the exception thrown by the function
     */
    public static <K, V, W, X extends Exception> LinkedHashMap<K, W> map(F2<K, V, W, X> f, LinkedHashMap<K, V> m) throws X {
        return Exceptions.<LinkedHashMap<K, W>, X>unwrap(() ->
                Streams.tupleStream(m)
                        .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(wrap(f::apply))))
                        .collect(toMap(Tuple2::get1, Tuple2::get2, FunctorsGenEx::throwingMerger, LinkedHashMap::new))
        );
    }

    private static <T> T throwingMerger(T u, T v) {
        throw new IllegalStateException(String.format("Duplicate key %s", u));
    }
}
