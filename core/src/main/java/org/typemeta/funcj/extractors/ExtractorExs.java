package org.typemeta.funcj.extractors;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.toList;

public abstract class ExtractorExs {

    /**
     * A specialisation of {@link ExtractorEx} for {@code double} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    @FunctionalInterface
    public interface DoubleExtractorEx<ENV, EX extends Exception> extends ExtractorEx<ENV, Double, EX> {
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

        default <U> ExtractorEx<ENV, U, EX> mapDbl(DoubleFunction<U> f) {
            return env -> f.apply(extractDouble(env));
        }
    }

    /**
     * A specialisation of {@code ExtractorEx} for {@code int} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    @FunctionalInterface
    public interface IntExtractorEx<ENV, EX extends Exception> extends ExtractorEx<ENV, Integer, EX> {
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

        default <U> ExtractorEx<ENV, U, EX> mapInt(IntFunction<U> f) {
            return env -> f.apply(extractInt(env));
        }
    }

    /**
     * A specialisation of {@code ExtractorEx} for {@code long} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    @FunctionalInterface
    public interface LongExtractorEx<ENV, EX extends Exception> extends ExtractorEx<ENV, Long, EX> {
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

        default <U> ExtractorEx<ENV, U, EX> mapLong(LongFunction<U> f) {
            return env -> f.apply(extractLong(env));
        }
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
     * A combinator function to convert a collection of extractors into an extractor for a list.
     * @param extrs     the collection of extractors
     * @param <ENV>     the environment type
     * @param <T>       the extractor value type
     * @param <EX>      the exception type
     * @return          an extractor for a list of values
     */
    public static <ENV, T, EX extends Exception> ExtractorEx<ENV, List<T>, EX> list(Collection<ExtractorEx<ENV, T, EX>> extrs) {
        return env ->
                Exceptions.<List<T>, EX>unwrap(() ->
                        extrs.stream()
                                .map(ext -> Exceptions.wrap(() -> ext.extract(env)))
                                .collect(toList())
                );
    }
}
