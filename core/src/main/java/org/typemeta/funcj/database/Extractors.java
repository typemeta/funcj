package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.util.Exceptions;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static java.time.temporal.ChronoField.*;
import static java.util.stream.Collectors.toList;

/**
 * Simple combinator framework for extracting values from JDBC {@link ResultSet}s.
 */
public abstract class Extractors {

    private static final DateTimeFormatter YYYYMMDD =
            new DateTimeFormatterBuilder()
                    .appendValue(YEAR, 4)
                    .appendValue(MONTH_OF_YEAR, 2)
                    .appendValue(DAY_OF_MONTH, 2)
                    .toFormatter();

    /**
     * An {@code Extractor} is a function to extract a value from a {@link ResultSet}.
     * Note that, unlike {@link TypeExtractor} an {@code Extractor} already knows the column name.
     * @param <T>       the extracted value type
     */
    public interface Extractor<T> {
        T extract(ResultSet rs) throws SQLException;

        default <U> Extractor<U> map(F<T, U> f) {
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
    public static <A, B, C, D,R> Extractor<R> combine(
            Extractor<A> exA,
            Extractor<B> exB,
            Extractor<C> exC,
            Extractor<D> exD,
            Functions.F4<A, B, C, D, R> f
    ) {
        return rs -> f.apply(exA.extract(rs), exB.extract(rs), exC.extract(rs), exD.extract(rs));
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

        default <U> Extractor<U> map(IntFunction<U> f) {
            return rs -> f.apply(extract(rs));
        }
    }

    /**
     * A specialisation of {@link Extractor} for {@code double} values.
     */
    public interface DblExtractor extends Extractor<Double> {
        double extractDbl(ResultSet rs) throws SQLException;

        @Override
        default Double extract(ResultSet rs) throws SQLException {
            return extractDbl(rs);
        }

        default <U> Extractor<U> map(DoubleFunction<U> f) {
            return rs -> f.apply(extract(rs));
        }
    }

    /**
     * A {@code TypeExtractor} is a function to extract a value from a {@link ResultSet},
     * given a column name.
     * @param <T>       the value type
     */
    public interface TypeExtractor<T> {
        /**
         * Extract a value of type {@code T} from the given {@link ResultSet},
         * for the given column name.
         * @param rs        the result set
         * @param name      the column name
         * @return          the extracted value
         * @throws SQLException in the event of an error
         */
        T extract(ResultSet rs, String name) throws SQLException;

        /**
         * Compose this extractor with a function, by applying the function to the
         * result of this extractor.
         * @param f         the function
         * @param <U>       the function return type
         * @return          the result of applying the function
         */
        default <U> TypeExtractor<U> map(F<T, U> f) {
            return (rs, name) -> f.apply(extract(rs, name));
        }

        /**
         * Bind this {@code TypeExtractor} to a name, giving us an {@link Extractor}.
         * @param name      the column name
         * @return          the extractor
         */
        default Extractor<T> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@code TypeExtractor} for {@link String} values.
     */
    public static final TypeExtractor<String> STRING_TEX = ResultSet::getString;

    /**
     * A {@code TypeExtractor} for {@link String} values, which converts the string to upper case.
     */
    public static final TypeExtractor<String> UP_STRING_TEX =
            STRING_TEX.map(String::toUpperCase);

    /**
     * A {@code TypeExtractor} for {@link Date} values.
     */
    public static final TypeExtractor<Date> SQLDATE_TEX = ResultSet::getDate;

    /**
     * A {@code TypeExtractor} for dates, which converts the date into {@link LocalDate} values.
     */
    public static final TypeExtractor<LocalDate> LOCALDATE_TEX =
            SQLDATE_TEX.map(Date::toLocalDate);

    /**
     * A {@code TypeExtractor} for dates, which converts the date into YYYYMMDD strings.
     */
    public static final TypeExtractor<String> YYYYMMDD_TEX =
            LOCALDATE_TEX.map(YYYYMMDD::format);

    /**
     * A {@code TypeExtractor} for dates, which converts the date into YYYYMMDD strings.
     */
    public static final TypeExtractor<Optional<String>> OPT_YYYYMMDD_TEX =
            optional(SQLDATE_TEX)
                    .map(od -> od.map(Date::toLocalDate))
                    .map(od -> od.map(YYYYMMDD::format));

    /**
     * An {@code IntTypeExtractor} is a function to extract an {@code int} value from a {@link ResultSet},
     * given a column name.
     * It's essentially {@link TypeExtractor} specialised for the {@code int} type.
     */
    public interface IntTypeExtractor extends TypeExtractor<Integer> {
        int extractInt(ResultSet rs, String name) throws SQLException;

        @Override
        default Integer extract(ResultSet rs, String name) throws SQLException {
            return extractInt(rs, name);
        }

        default <U> TypeExtractor<U> map(IntFunction<U> f) {
            return (rs, name) -> f.apply(extractInt(rs, name));
        }

        default IntExtractor bindInt(String name) {
            return rs -> extractInt(rs, name);
        }
    }

    /**
     * An {@code IntTypeExtractor} instance.
     */
    public static final IntTypeExtractor INTEGER_TEX = ResultSet::getInt;

    /**
     * An {@code DblTypeExtractor} is a function to extract an {@code double} value from a {@link ResultSet},
     * given a column name.
     * It's essentially {@link TypeExtractor} specialised for the {@code double} type.
     */
    public interface DblTypeExtractor extends TypeExtractor<Double> {
        double extractDbl(ResultSet rs, String name) throws SQLException;

        @Override
        default Double extract(ResultSet rs, String name) throws SQLException {
            return extractDbl(rs, name);
        }

        default <U> TypeExtractor<U> map(DoubleFunction<U> f) {
            return (rs, name) -> f.apply(extractDbl(rs, name));
        }

        default DblExtractor bindDbl(String name) {
            return rs -> extractDbl(rs, name);
        }
    }

    /**
     * A {@code DblTypeExtractor} instance.
     */
    public static final DblTypeExtractor DOUBLE_TEX = ResultSet::getDouble;

    /**
     * A {@code TypeExtractor} for enum values.
     * @param enumType  the enum type class
     * @param <E>       the enum type
     * @return          the extractor function
     */
    public static <E extends Enum<E>> TypeExtractor<E> enumTypeExtractor(Class<E> enumType) {
        return (rs, name) -> Enum.valueOf(enumType, rs.getString(name).toUpperCase());
    }

    /**
     * A combinator function to convert a {@code TypeExtractor} into one specialised for {@link Optional} values.
     * @param f         the extractor function for the contained type
     * @param <T>       the contained (i.e. optional) type
     * @return          the extractor function
     */
    public static <T> TypeExtractor<Optional<T>> optional(TypeExtractor<T> f) {
        return (rs, name) -> {
            final T value = f.extract(rs, name);
            if (rs.wasNull()) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        };
    }

    /**
     * A {@code TypeExtractor} for {@code Optional} {@code String} values.
     */
    public static final TypeExtractor<Optional<String>> OPT_STRING_TEX = optional(STRING_TEX);

    /**
     * A {@code TypeExtractor} for {@code Optional} {@code String} values,
     * which comverts the string to upper case.
     * Note, we can't use {@code optional(UP_STRING_TEX)} here due to null values.
     */
    public static final TypeExtractor<Optional<String>> OPT_UP_STRING_TEX =
            OPT_STRING_TEX.map(os -> os.map(String::toUpperCase));

    /**
     * An {@code OptIntTypeExtractor} is essentially a {@code TypeExtractor<Optional<T>>} specialised for {@code int}
     * to avoid unnecessary boxing.
     */
    public interface OptIntTypeExtractor {
        OptionalInt extract(ResultSet rs, String name) throws SQLException;

        default <U> TypeExtractor<Optional<U>> map(IntFunction<U> f) {
            return (rs, name) -> {
                final OptionalInt oi = extract(rs, name);
                if (oi.isPresent()) {
                    return Optional.of(f.apply(oi.getAsInt()));
                } else {
                    return Optional.empty();
                }
            };
        }

        default Extractor<OptionalInt> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@link OptIntTypeExtractor} instance.
     */
    public static final OptIntTypeExtractor OPT_INTEGER_TEX = (rs, name) -> {
        final int value = INTEGER_TEX.extractInt(rs, name);
        if (rs.wasNull()) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(value);
        }
    };

    /**
     * An {@code OptDblTypeExtractor} is essentially a {@code TypeExtractor<Optional<T>>} specialised for {@code double}
     * to avoid unnecessary boxing.
     */
    public interface OptDblTypeExtractor {
        OptionalDouble extract(ResultSet rs, String name) throws SQLException;

        default <U> TypeExtractor<Optional<U>> map(DoubleFunction<U> f) {
            return (rs, name) -> {
                final OptionalDouble od = extract(rs, name);
                if (od.isPresent()) {
                    return Optional.of(f.apply(od.getAsDouble()));
                } else {
                    return Optional.empty();
                }
            };
        }

        default Extractor<OptionalDouble> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@link OptDblTypeExtractor} instance.
     */
    public static final OptDblTypeExtractor OPT_DOUBLE_TEX = (rs, name) -> {
        final double value = DOUBLE_TEX.extractDbl(rs, name);
        if (rs.wasNull()) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(value);
        }
    };

    /**
     * A combinator function to convert a stream of extractors into an extractor for a list.
     * @param extrs     the stream of extractors
     * @param <T>       the extractor value type
     * @return          the list of extracted values
     */
    public static <T> Extractor<List<T>> list(Stream<Extractor<T>> extrs) {
        return rs ->
                extrs.map(ex -> Exceptions.wrap(() -> ex.extract(rs)))
                        .collect(toList());
    }
}

