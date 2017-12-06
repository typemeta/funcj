package org.typemeta.funcj.util;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.tuples.Tuple2;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

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
     * Map a function over an {@link OptionalInt}, to yield a generic {@link Optional}.
     * @param f         the function to apply to the value within the {@code OptionalInt}
     * @param oi        the optional value
     * @param <R>       the return type of the function
     * @return          an {@code Optional} that contains the result of applying the function
     */
    public static <R> Optional<R> map(IntFunction<R> f, OptionalInt oi) {
        return oi.isPresent() ?  Optional.of(f.apply(oi.getAsInt())) : Optional.empty();
    }

    /**
     * Map a function over an {@link OptionalInt}, to yield a {@link OptionalInt}.
     * @param f         the function to apply to the value within the {@code OptionalInt}
     * @param oi        the optional value
     * @return          an {@code OptionalInt} that contains the result of applying the function
     */
    public static OptionalInt mapToInt(IntUnaryOperator f, OptionalInt oi) {
        return oi.isPresent() ?  OptionalInt.of(f.applyAsInt(oi.getAsInt())) : OptionalInt.empty();
    }

    /**
     * Map a function over an {@link OptionalInt}, to yield a {@link OptionalLong}.
     * @param f         the function to apply to the value within the {@code OptionalInt}
     * @param oi        the optional value
     * @return          an {@code OptionalLong} that contains the result of applying the function
     */
    public static OptionalLong mapToLong(IntToLongFunction f, OptionalInt oi) {
        return oi.isPresent() ?  OptionalLong.of(f.applyAsLong(oi.getAsInt())) : OptionalLong.empty();
    }

    /**
     * Map a function over an {@link OptionalInt}, to yield a {@link OptionalDouble}.
     * @param f         the function to apply to the value within the {@code OptionalInt}
     * @param oi        the optional value
     * @return          an {@code OptionalDouble} that contains the result of applying the function
     */
    public static OptionalDouble mapToDouble(IntToDoubleFunction f, OptionalInt oi) {
        return oi.isPresent() ?  OptionalDouble.of(f.applyAsDouble(oi.getAsInt())) : OptionalDouble.empty();
    }

    /**
     * Map a function over an {@link OptionalLong}, to yield a generic {@link Optional}.
     * @param f         the function to apply to the value within the {@code OptionalLong}
     * @param oi        the optional value
     * @param <R>       the return type of the function
     * @return          an {@code Optional} that contains the result of applying the function
     */
    public static <R> Optional<R> map(LongFunction<R> f, OptionalLong oi) {
        return oi.isPresent() ?  Optional.of(f.apply(oi.getAsLong())) : Optional.empty();
    }

    /**
     * Map a function over an {@link OptionalLong}, to yield a {@link OptionalInt}.
     * @param f         the function to apply to the value within the {@code OptionalLong}
     * @param ol        the optional value
     * @return          an {@code OptionalInt} that contains the result of applying the function
     */
    public static OptionalInt mapToInt(LongToIntFunction f, OptionalLong ol) {
        return ol.isPresent() ?  OptionalInt.of(f.applyAsInt(ol.getAsLong())) : OptionalInt.empty();
    }

    /**
     * Map a function over an {@link OptionalLong}, to yield a {@link OptionalLong}.
     * @param f         the function to apply to the value within the {@code OptionalLong}
     * @param ol        the optional value
     * @return          an {@code OptionalLong} that contains the result of applying the function
     */
    public static OptionalLong mapToLong(LongUnaryOperator f, OptionalLong ol) {
        return ol.isPresent() ?  OptionalLong.of(f.applyAsLong(ol.getAsLong())) : OptionalLong.empty();
    }

    /**
     * Map a function over an {@link OptionalLong}, to yield a {@link OptionalDouble}.
     * @param f         the function to apply to the value within the {@code OptionalLong}
     * @param ol        the optional value
     * @return          an {@code OptionalDouble} that contains the result of applying the function
     */
    public static OptionalDouble mapToDouble(LongToDoubleFunction f, OptionalLong ol) {
        return ol.isPresent() ?  OptionalDouble.of(f.applyAsDouble(ol.getAsLong())) : OptionalDouble.empty();
    }

    /**
     * Map a function over an {@link OptionalDouble}, to yield a generic {@link Optional}.
     * @param f         the function to apply to the value within the {@code OptionalDouble}
     * @param od        the optional value
     * @param <R>       the return type of the function
     * @return          an {@code Optional} that contains the result of applying the function
     */
    public static <R> Optional<R> map(DoubleFunction<R> f, OptionalDouble od) {
        return od.isPresent() ?  Optional.of(f.apply(od.getAsDouble())) : Optional.empty();
    }

    /**
     * Map a function over an {@link OptionalDouble}, to yield a {@link OptionalInt}.
     * @param f         the function to apply to the value within the {@code OptionalDouble}
     * @param od        the optional value
     * @return          an {@code OptionalInt} that contains the result of applying the function
     */
    public static OptionalInt mapToInt(DoubleToIntFunction f, OptionalDouble od) {
        return od.isPresent() ?  OptionalInt.of(f.applyAsInt(od.getAsDouble())) : OptionalInt.empty();
    }

    /**
     * Map a function over an {@link OptionalDouble}, to yield a {@link OptionalLong}.
     * @param f         the function to apply to the value within the {@code OptionalDouble}
     * @param od        the optional value
     * @return          an {@code OptionalLong} that contains the result of applying the function
     */
    public static OptionalLong mapToLong(DoubleToLongFunction f, OptionalDouble od) {
        return od.isPresent() ?  OptionalLong.of(f.applyAsLong(od.getAsDouble())) : OptionalLong.empty();
    }

    /**
     * Map a function over an {@link OptionalDouble}, to yield a {@link OptionalDouble}.
     * @param f         the function to apply to the value within the {@code OptionalDouble}
     * @param od        the optional value
     * @return          an {@code OptionalDouble} that contains the result of applying the function
     */
    public static OptionalDouble mapToDouble(DoubleUnaryOperator f, OptionalDouble od) {
        return od.isPresent() ?  OptionalDouble.of(f.applyAsDouble(od.getAsDouble())) : OptionalDouble.empty();
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
    @SuppressWarnings("unchecked")
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

    /**
     * Map a function over a {@link Map}.
     * Apply a binary function to each entry in a {@code Map} to obtain the new {@code Map} value.
     * The keys are unchanged.
     * @param f         the function
     * @param m         the map
     * @param <K>       the map key type
     * @param <V>       the map value type
     * @param <W>       the new map value type
     * @return          the new map
     */
    public static <K, V, W> Map<K, W> map(Functions.F2<K, V, W> f, Map<K, V> m) {
        return Streams.tupleStream(m)
                .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(f)))
                .collect(toMap(Tuple2::get1, Tuple2::get2));
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
     * @return          the new map
     */
    public static <K, V, W> SortedMap<K, W> map(Functions.F2<K, V, W> f, SortedMap<K, V> m) {
        return Streams.tupleStream(m)
                .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(f)))
                .collect(toMap(Tuple2::get1, Tuple2::get2, throwingMerger(), TreeMap::new));
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
     * @return          the new map
     */
    public static <K, V, W> ConcurrentMap<K, W> map(Functions.F2<K, V, W> f, ConcurrentMap<K, V> m) {
        return Streams.tupleStream(m)
                .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(f)))
                .collect(toConcurrentMap(Tuple2::get1, Tuple2::get2));
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
     * @return          the new map
     */
    public static <K, V, W> LinkedHashMap<K, W> map(Functions.F2<K, V, W> f, LinkedHashMap<K, V> m) {
        return Streams.tupleStream(m)
                .map(t2 -> Tuple2.of(t2._1, t2.applyFrom(f)))
                .collect(toMap(Tuple2::get1, Tuple2::get2, throwingMerger(), LinkedHashMap::new));
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }
}
