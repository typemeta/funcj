package org.typemeta.funcj.extractors;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.toList;

public abstract class ExtractorsEx {
    /**
     * An extractor that always returns the given value.
     * @param value     the value
     * @param <ENV>     the value type
     * @param <T>       the exception type
     * @param <EX>      the exception type
     * @return          the extractor
     */
    public static <ENV, T, EX extends Exception> ExtractorEx<ENV, T, EX> konst(T value) {
        return env -> value;
    }

    /**
     * A specialisation of {@link ExtractorEx} for {@code double} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    @FunctionalInterface
    public interface DoubleExtractorEx<ENV, EX extends Exception> extends ExtractorEx<ENV, Double, EX> {
        /**
         * Static constructor method.
         * @param extr      the extractor
         * @param <ENV>     the environment type
         * @param <EX>      the exception type
         * @return          the extractor
         */
        static <ENV, EX extends Exception> DoubleExtractorEx<ENV, EX> of(DoubleExtractorEx<ENV, EX> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code double} value.
         * @param env   the environment
         * @return      the extracted value
         */
        double extractDouble(ENV env) throws EX;

        @Override
        default Double extract(ENV env) throws EX {
            return extractDouble(env);
        }

        /**
         * A variant of the {@link ExtractorEx#map} method specialised for {@code double} values.
         * @param f         the function
         * @param <U>       the return type of the function
         * @return          the mapped extractor
         */
        default <U> ExtractorEx<ENV, U, EX> mapDouble(DoubleFunction<U> f) {
            return env -> f.apply(extractDouble(env));
        }
    }

    /**
     * An extractor that always returns the given value.
     * @param value     the value
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     * @return          the extractor
     */
    public static <ENV, EX extends Exception> DoubleExtractorEx<ENV, EX> konst(double value) {
        return env -> value;
    }

    /**
     * A specialisation of {@code ExtractorEx} for {@code int} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    @FunctionalInterface
    public interface IntExtractorEx<ENV, EX extends Exception> extends ExtractorEx<ENV, Integer, EX> {
        /**
         * Static constructor method.
         * @param extr      the extractor
         * @param <ENV>     the environment type
         * @param <EX>      the exception type
         * @return          the extractor
         */
        static <ENV, EX extends Exception> IntExtractorEx<ENV, EX> of(IntExtractorEx<ENV, EX> extr) {
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
         * A variant of the {@link ExtractorEx#map} method specialised for {@code int} values.
         * @param f         the function
         * @param <U>       the return type of the function
         * @return          the mapped extractor
         */
        default <U> ExtractorEx<ENV, U, EX> mapInt(IntFunction<U> f) {
            return env -> f.apply(extractInt(env));
        }
    }

    /**
     * An extractor that always returns the given value.
     * @param value     the value
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     * @return          the extractor
     */
    public static <ENV, EX extends Exception> IntExtractorEx<ENV, EX> konst(int value) {
        return env -> value;
    }

    /**
     * A specialisation of {@code ExtractorEx} for {@code long} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    @FunctionalInterface
    public interface LongExtractorEx<ENV, EX extends Exception> extends ExtractorEx<ENV, Long, EX> {
        /**
         * Static constructor method.
         * @param extr      the extractor
         * @param <ENV>     the environment type
         * @param <EX>      the exception type
         * @return          the extractor
         */
        static <ENV, EX extends Exception> LongExtractorEx<ENV, EX> of(LongExtractorEx<ENV, EX> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code long} value.
         * @param env   the environment
         * @return      the extracted value
         */
        long extractLong(ENV env);

        @Override
        default Long extract(ENV env) {
            return extractLong(env);
        }

        /**
         * A variant of the {@link ExtractorEx#map} method specialised for {@code long} values.
         * @param f         the function
         * @param <U>       the return type of the function
         * @return          the mapped extractor
         */
        default <U> ExtractorEx<ENV, U, EX> mapLong(LongFunction<U> f) {
            return env -> f.apply(extractLong(env));
        }
    }

    /**
     * An extractor that always returns the given value.
     * @param value     the value
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     * @return          the extractor
     */
    public static <ENV, EX extends Exception> LongExtractorEx<ENV, EX> konst(long value) {
        return env -> value;
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
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    public static <ENV, A, B, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            ExtractorEx<ENV, A, EX> exA,
            ExtractorEx<ENV, B, EX> exB,
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
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            ExtractorEx<ENV, A, EX> exA,
            ExtractorEx<ENV, B, EX> exB,
            ExtractorEx<ENV, C, EX> exC,
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
     * @param exD       the third extractor
     * @param f         the fourth constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <D>       the type of value returned by the fourth extractor
     * @param <R>       the value type
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            ExtractorEx<ENV, A, EX> exA,
            ExtractorEx<ENV, B, EX> exB,
            ExtractorEx<ENV, C, EX> exC,
            ExtractorEx<ENV, D, EX> exD,
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
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, E, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            ExtractorEx<ENV, A, EX> exA,
            ExtractorEx<ENV, B, EX> exB,
            ExtractorEx<ENV, C, EX> exC,
            ExtractorEx<ENV, D, EX> exD,
            ExtractorEx<ENV, E, EX> exE,
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
     * @param <F>       the type of value returned by the sixth extractor
     * @param <R>       the value type
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, E, F, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            ExtractorEx<ENV, A, EX> exA,
            ExtractorEx<ENV, B, EX> exB,
            ExtractorEx<ENV, C, EX> exC,
            ExtractorEx<ENV, D, EX> exD,
            ExtractorEx<ENV, E, EX> exE,
            ExtractorEx<ENV, F, EX> exF,
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

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param exC       the third extractor
     * @param exD       the fourth extractor
     * @param exE       the fifth extractor
     * @param exF       the sixth extractor
     * @param exG       the seventh extractor
     * @param f         the value constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <D>       the type of value returned by the fourth extractor
     * @param <E>       the type of value returned by the fifth extractor
     * @param <F>       the type of value returned by the sixth extractor
     * @param <G>       the type of value returned by the seventh extractor
     * @param <R>       the value type
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, E, F, G, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            ExtractorEx<ENV, A, EX> exA,
            ExtractorEx<ENV, B, EX> exB,
            ExtractorEx<ENV, C, EX> exC,
            ExtractorEx<ENV, D, EX> exD,
            ExtractorEx<ENV, E, EX> exE,
            ExtractorEx<ENV, F, EX> exF,
            ExtractorEx<ENV, G, EX> exG,
            Functions.F7<A, B, C, D, E, F, G, R> f
    ) {
        return env -> f.apply(
                exA.extract(env),
                exB.extract(env),
                exC.extract(env),
                exD.extract(env),
                exE.extract(env),
                exF.extract(env),
                exG.extract(env)
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
     * @param exG       the seventh extractor
     * @param exH       the eighth extractor
     * @param f         the value constructor
     * @param <ENV>     the environment type
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <D>       the type of value returned by the fourth extractor
     * @param <E>       the type of value returned by the fifth extractor
     * @param <F>       the type of value returned by the sixth extractor
     * @param <G>       the type of value returned by the seventh extractor
     * @param <H>       the type of value returned by the eighth extractor
     * @param <R>       the value type
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    public static <ENV, A, B, C, D, E, F, G, H, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            ExtractorEx<ENV, A, EX> exA,
            ExtractorEx<ENV, B, EX> exB,
            ExtractorEx<ENV, C, EX> exC,
            ExtractorEx<ENV, D, EX> exD,
            ExtractorEx<ENV, E, EX> exE,
            ExtractorEx<ENV, F, EX> exF,
            ExtractorEx<ENV, G, EX> exG,
            ExtractorEx<ENV, H, EX> exH,
            Functions.F8<A, B, C, D, E, F, G, H, R> f
    ) {
        return env -> f.apply(
                exA.extract(env),
                exB.extract(env),
                exC.extract(env),
                exD.extract(env),
                exE.extract(env),
                exF.extract(env),
                exG.extract(env),
                exH.extract(env)
        );
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param f         the value constructor
     * @param exs       an array of the extractors
     * @param <ENV>     the environment type
     * @param <R>       the value type
     * @param <EX>      the exception type
     * @return          the new extractor
     */
    @SafeVarargs
    public static <ENV, R, EX extends Exception> ExtractorEx<ENV, R, EX> combine(
            Functions.F<Object[], R> f,
            ExtractorEx<ENV, ?, EX> ... exs
    ) {
        return env -> {
            final Object[] vals = new Object[exs.length];
            for (int i = 0; i < exs.length; ++i) {
                vals[i] = exs[i].extract(env);
            }
            return f.apply(vals);
        };
    }

    /**
     * A combinator function to convert a collection of extractors into an extractor for a list.
     * @param exs       the collection of extractors
     * @param <ENV>     the environment type
     * @param <T>       the extractor value type
     * @param <EX>      the exception type
     * @return          an extractor for a list of values
     */
    public static <ENV, T, EX extends Exception> ExtractorEx<ENV, List<T>, EX> list(
            Collection<ExtractorEx<ENV, T, EX>> exs
    ) {
        return env ->
                Exceptions.<List<T>, EX>unwrap(() ->
                        exs.stream()
                                .map(ext -> Exceptions.wrap(() -> ext.extract(env)))
                                .collect(toList())
                );
    }
}
