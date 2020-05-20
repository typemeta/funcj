package org.typemeta.funcj.extractors;

import org.typemeta.funcj.functions.Functions;

import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.toList;

public abstract class Extractors {

    /**
     * A specialisation of {@link Extractor} for {@code double} values.
     * @param <ENV>     the environment type
     */
    @FunctionalInterface
    public interface DoubleExtractor<ENV> extends Extractor<ENV, Double> {
        /**
         * Static constructor method.
         * @param extr      the extractor
         * @param <ENV>     the environment type
         * @return          the extractor
         */
        static <ENV> DoubleExtractor<ENV> of(DoubleExtractor<ENV> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code double} value.
         * @param env   the environment
         * @return      the extracted value
         */
        double extractDouble(ENV env);

        @Override
        default Double extract(ENV env) {
            return extractDouble(env);
        }

        /**
         * A variant of the {@link Extractor#map} method specialised for {@code double} values.
         * @param f         the function
         * @param <U>       the return type of the function
         * @return          the mapped extractor
         */
        default <U> Extractor<ENV, U> mapDouble(DoubleFunction<U> f) {
            return env -> f.apply(extractDouble(env));
        }
    }

    /**
     * A specialisation of {@code Extractor} for {@code int} values.
     * @param <ENV>     the environment type
     */
    @FunctionalInterface
    public interface IntExtractor<ENV> extends Extractor<ENV, Integer> {
        /**
         * Static constructor method.
         * @param extr      the extractor
         * @param <ENV>     the environment type
         * @return          the extractor
         */
        static <ENV> IntExtractor<ENV> of(IntExtractor<ENV> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code int} value.
         * @param env   the environment
         * @return      the extracted value
         */
        int extractInt(ENV env);

        @Override
        default Integer extract(ENV env) {
            return extractInt(env);
        }

        /**
         * A variant of the {@link Extractor#map} method specialised for {@code int} values.
         * @param f         the function
         * @param <U>       the return type of the function
         * @return          the mapped extractor
         */
        default <U> Extractor<ENV, U> mapInt(IntFunction<U> f) {
            return env -> f.apply(extractInt(env));
        }
    }

    /**
     * A specialisation of {@code Extractor} for {@code long} values.
     * @param <ENV>     the environment type
     */
    @FunctionalInterface
    public interface LongExtractor<ENV> extends Extractor<ENV, Long> {
        /**
         * Static constructor method.
         * @param extr      the extractor
         * @param <ENV>     the environment type
         * @return          the extractor
         */
        static <ENV> LongExtractor<ENV> of(LongExtractor<ENV> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code long} value.
         * @param env       the environment
         * @return          the extracted value
         */
        long extractLong(ENV env);

        @Override
        default Long extract(ENV env) {
            return extractLong(env);
        }

        /**
         * A variant of the {@link Extractor#map} method specialised for {@code long} values.
         * @param f         the function
         * @param <U>       the return type of the function
         * @return          the mapped extractor
         */
        default <U> Extractor<ENV, U> mapLong(LongFunction<U> f) {
            return env -> f.apply(extractLong(env));
        }
    }
    /**
     * A combinator function to convert a {@link Extractor} into one for {@link Optional} values.
     * The option extractor converts null values to
     * @param extr      the extractor function for the value type
     * @param <ENV>     the environment type
     * @param <T>       the value type
     * @return          the extractor function for the optional value
     */
    public static <ENV, T> Extractor<ENV, Optional<T>> optional(Extractor<ENV, T> extr) {
        return extr.map(Optional::ofNullable);
    }

    /**
     * A combinator function to convert a collection of extractors into an extractor for a list.
     * @param extrs     the collection of extractors
     * @param <ENV>     the environment type
     * @param <T>       the extractor value type
     * @return          an extractor for a list of values
     */
    public static <ENV, T> Extractor<ENV, List<T>> list(Collection<Extractor<ENV, T>> extrs) {
        return env ->
                extrs.stream()
                        .map(ext -> ext.extract(env))
                        .collect(toList());
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param f         the value constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <R>       the value type
     * @return          the new extractor
     */
    public static <ENV, A, B, R> Extractor<ENV, R> combine(
            Extractor<ENV, A> exA,
            Extractor<ENV, B> exB,
            Functions.F2<A, B, R> f
    ) {
        return env -> f.apply(exA.extract(env), exB.extract(env));
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param exC       the third extractor
     * @param f         the value constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <R>       the value type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, R> Extractor<ENV, R> combine(
            Extractor<ENV, A> exA,
            Extractor<ENV, B> exB,
            Extractor<ENV, C> exC,
            Functions.F3<A, B, C, R> f
    ) {
        return env -> f.apply(exA.extract(env), exB.extract(env), exC.extract(env));
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param exC       the third extractor
     * @param exD       the fourth extractor
     * @param f         the value constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <D>       the type of value returned by the fourth extractor
     * @param <R>       the value type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, R> Extractor<ENV, R> combine(
            Extractor<ENV, A> exA,
            Extractor<ENV, B> exB,
            Extractor<ENV, C> exC,
            Extractor<ENV, D> exD,
            Functions.F4<A, B, C, D, R> f
    ) {
        return env -> f.apply(exA.extract(env), exB.extract(env), exC.extract(env), exD.extract(env));
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param exC       the third extractor
     * @param exD       the fourth extractor
     * @param exE       the fifth extractor
     * @param f         the value constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <D>       the type of value returned by the fourth extractor
     * @param <E>       the type of value returned by the fifth extractor
     * @param <R>       the value type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, E, R> Extractor<ENV, R> combine(
            Extractor<ENV, A> exA,
            Extractor<ENV, B> exB,
            Extractor<ENV, C> exC,
            Extractor<ENV, D> exD,
            Extractor<ENV, E> exE,
            Functions.F5<A, B, C, D, E, R> f
    ) {
        return env -> f.apply(
                exA.extract(env),
                exB.extract(env),
                exC.extract(env),
                exD.extract(env),
                exE.extract(env)
        );
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param exC       the third extractor
     * @param exD       the fourth extractor
     * @param exE       the fifth extractor
     * @param exF       the sixth extractor
     * @param f         the value constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <D>       the type of value returned by the fourth extractor
     * @param <E>       the type of value returned by the fifth extractor
     * @param <E>       the type of value returned by the sixth extractor
     * @param <R>       the value type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, E, F, R> Extractor<ENV, R> combine(
            Extractor<ENV, A> exA,
            Extractor<ENV, B> exB,
            Extractor<ENV, C> exC,
            Extractor<ENV, D> exD,
            Extractor<ENV, E> exE,
            Extractor<ENV, F> exF,
            Functions.F6<A, B, C, D, E, F, R> f
    ) {
        return env -> f.apply(
                exA.extract(env),
                exB.extract(env),
                exC.extract(env),
                exD.extract(env),
                exE.extract(env),
                exF.extract(env)
        );
    }
}
