package org.typemeta.funcj.database;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.*;
import java.util.*;
import java.util.function.*;

import static java.time.temporal.ChronoField.*;

/**
 * Simple combinator framework for extracting values from JDBC {@link ResultSet}s.
 */
public abstract class TypeExtractors {

    private static final DateTimeFormatter YYYYMMDD =
            new DateTimeFormatterBuilder()
                    .appendValue(YEAR, 4)
                    .appendValue(MONTH_OF_YEAR, 2)
                    .appendValue(DAY_OF_MONTH, 2)
                    .toFormatter();

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

        default Extractors.IntExtractor bindInt(String name) {
            return rs -> extractInt(rs, name);
        }
    }

    /**
     * An {@code IntTypeExtractor} instance.
     */
    public static final IntTypeExtractor INTEGER_TEX = ResultSet::getInt;

    /**
     * An {@code LongTypeExtractor} is a function to extract an {@code long} value from a {@link ResultSet},
     * given a column name.
     * It's essentially {@link TypeExtractor} specialised for the {@code long} type.
     */
    public interface LongTypeExtractor extends TypeExtractor<Long> {
        long extractLong(ResultSet rs, String name) throws SQLException;

        @Override
        default Long extract(ResultSet rs, String name) throws SQLException {
            return extractLong(rs, name);
        }

        default <U> TypeExtractor<U> map(LongFunction<U> f) {
            return (rs, name) -> f.apply(extractLong(rs, name));
        }

        default Extractors.LongExtractor bindLong(String name) {
            return rs -> extractLong(rs, name);
        }
    }

    /**
     * A  {@code LongTypeExtractor} instance.
     */
    public static final LongTypeExtractor LONG_TEX = ResultSet::getLong;

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

        default Extractors.DblExtractor bindDbl(String name) {
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
     * which converts the string to upper case.
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
     * An {@code OptLongTypeExtractor} is essentially a {@code TypeExtractor<Optional<T>>} specialised for
     * {@code long} to avoid unnecessary boxing.
     */
    public interface OptLongTypeExtractor {
        OptionalLong extract(ResultSet rs, String name) throws SQLException;

        default <U> TypeExtractor<Optional<U>> map(LongFunction<U> f) {
            return (rs, name) -> {
                final OptionalLong ol = extract(rs, name);
                if (ol.isPresent()) {
                    return Optional.of(f.apply(ol.getAsLong()));
                } else {
                    return Optional.empty();
                }
            };
        }

        default Extractor<OptionalLong> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@link OptLongTypeExtractor} instance.
     */
    public static final OptLongTypeExtractor OPT_LONG_TEX = (rs, name) -> {
        final int value = INTEGER_TEX.extractInt(rs, name);
        if (rs.wasNull()) {
            return OptionalLong.empty();
        } else {
            return OptionalLong.of(value);
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
}
