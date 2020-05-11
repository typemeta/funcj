package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

import java.sql.*;
import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.toList;

public abstract class Extractors {

    /**
     * A specialisation of {@link Extractor} for {@code double} values.
     */
    public interface DoubleExtractor extends Extractor<Double> {
        double extractDbl(ResultSet rs) throws SQLException;

        @Override
        default Double extract(ResultSet rs) throws SQLException {
            return extractDbl(rs);
        }

        default <U> Extractor<U> mapDbl(DoubleFunction<U> f) {
            return rs -> f.apply(extract(rs));
        }
    }

    /**
     * A specialisation of {@link Extractor} for {@code int} values.
     */
    public interface IntExtractor extends Extractor<Integer> {
        int extractInt(ResultSet rs) throws SQLException;

        @Override
        default Integer extract(ResultSet rs) throws SQLException {
            return extractInt(rs);
        }

        default <U> Extractor<U> mapInt(IntFunction<U> f) {
            return rs -> f.apply(extract(rs));
        }
    }

    /**
     * A specialisation of {@link Extractor} for {@code long} values.
     */
    public interface LongExtractor extends Extractor<Long> {
        long extractLong(ResultSet rs) throws SQLException;

        @Override
        default Long extract(ResultSet rs) throws SQLException {
            return extractLong(rs);
        }

        default <U> Extractor<U> mapLong(LongFunction<U> f) {
            return rs -> f.apply(extract(rs));
        }
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param f         the value constructor
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <R>       the value type
     * @return          the new value
     */
    public static <A, B, R> Extractor<R> combine(
            Extractor<A> exA,
            Extractor<B> exB,
            Functions.F2<A, B, R> f
    ) {
        return rs -> f.apply(exA.extract(rs), exB.extract(rs));
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param exC       the third extractor
     * @param f         the value constructor
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <R>       the value type
     * @return          the new value
     */
    public static <A, B, C, R> Extractor<R> combine(
            Extractor<A> exA,
            Extractor<B> exB,
            Extractor<C> exC,
            Functions.F3<A, B, C, R> f
    ) {
        return rs -> f.apply(exA.extract(rs), exB.extract(rs), exC.extract(rs));
    }

    /**
     * Combinator function for building an extractor for a class type,
     * using extractors for the fields that comprise the class type.
     * @param exA       the first extractor
     * @param exB       the second extractor
     * @param exC       the third extractor
     * @param exD       the third extractor
     * @param f         the fourth constructor
     * @param <A>       the type of value returned by the first extractor
     * @param <B>       the type of value returned by the second extractor
     * @param <C>       the type of value returned by the third extractor
     * @param <D>       the type of value returned by the fourth extractor
     * @param <R>       the value type
     * @return          the new value
     */
    public static <A, B, C, D, R> Extractor<R> combine(
            Extractor<A> exA,
            Extractor<B> exB,
            Extractor<C> exC,
            Extractor<D> exD,
            Functions.F4<A, B, C, D, R> f
    ) {
        return rs -> f.apply(exA.extract(rs), exB.extract(rs), exC.extract(rs), exD.extract(rs));
    }

    /**
     * A combinator function to convert a collection of extractors into an extractor for a list.
     * @param extrs     the collection of extractors
     * @param <T>       the extractor value type
     * @return          the list of extracted values
     */
    public static <T> Extractor<List<T>> list(Collection<Extractor<T>> extrs) {
        return rs ->
                Exceptions.<List<T>, SQLException>unwrap(
                        () -> extrs
                                .stream()
                                .map(ext -> Exceptions.wrap(
                                        () -> ext.extract(rs)
                                )).collect(toList())
                );
    }
}
