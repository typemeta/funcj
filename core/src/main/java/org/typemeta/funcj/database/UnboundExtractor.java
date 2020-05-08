package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

/**
 * An {@code UnboundExtractor} is a function to extract a value from a {@link ResultSet},
 * given a column name.
 * @param <T>       the value type
 */
public interface UnboundExtractor<T> {
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
     * Convert this extractor into another that applies a function to the result of this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new extractor
     */
    default <U> UnboundExtractor<U> map(Functions.F<T, U> f) {
        return (rs, name) -> f.apply(extract(rs, name));
    }

    /**
     * Bind this extractor to a name, giving us an {@link Extractor}.
     * @param name      the column name
     * @return          the extractor
     */
    default Extractor<T> bind(String name) {
        return rs -> extract(rs, name);
    }

    /**
     * A {@code UnboundExtractor} for {@link String} values.
     */
    UnboundExtractor<String> STRING_EX = ResultSet::getString;

    /**
     * A {@code UnboundExtractor} for {@link String} values, which converts the string to upper case.
     */
    UnboundExtractor<String> UP_STRING_EX =
            STRING_EX.map(String::toUpperCase);

    /**
     * A {@code UnboundExtractor} for {@link Date} values.
     */
    UnboundExtractor<Date> SQLDATE_EX = ResultSet::getDate;

    /**
     * A {@code UnboundExtractor} for dates, which converts the date into {@link LocalDate} values.
     */
    UnboundExtractor<LocalDate> LOCALDATE_EX =
            SQLDATE_EX.map(Date::toLocalDate);

    /**
     * A {@code UnboundExtractor} for dates, which converts the date into YYYYMMDD strings.
     */
    UnboundExtractor<String> YYYYMMDD_EX =
            LOCALDATE_EX.map(ExtractorUtils.YYYYMMDD::format);

    /**
     * A {@code UnboundExtractor} for dates, which converts the date into YYYYMMDD strings.
     */
    UnboundExtractor<Optional<String>> OPT_YYYYMMDD_EX =
            optional(SQLDATE_EX)
                    .map(od -> od.map(Date::toLocalDate))
                    .map(od -> od.map(ExtractorUtils.YYYYMMDD::format));

    /**
     * An {@code IntUnboundExtractor} is a function to extract an {@code int} value from a {@link ResultSet},
     * given a column name.
     * It's essentially {@link UnboundExtractor} specialised for the {@code int} type.
     */
    interface IntUnboundExtractor extends UnboundExtractor<Integer> {
        int extractInt(ResultSet rs, String name) throws SQLException;

        @Override
        default Integer extract(ResultSet rs, String name) throws SQLException {
            return extractInt(rs, name);
        }

        default <U> UnboundExtractor<U> map(IntFunction<U> f) {
            return (rs, name) -> f.apply(extractInt(rs, name));
        }

        default Extractor.IntExtractor bindInt(String name) {
            return rs -> extractInt(rs, name);
        }
    }

    /**
     * An {@code IntTypeExtractor} instance.
     */
    IntUnboundExtractor INTEGER_EX = ResultSet::getInt;

    /**
     * An {@code DblUnboundExtractor} is a function to extract an {@code double} value from a {@link ResultSet},
     * given a column name.
     * It's essentially {@link UnboundExtractor} specialised for the {@code double} type.
     */
    interface DblUnboundExtractor extends UnboundExtractor<Double> {
        double extractDbl(ResultSet rs, String name) throws SQLException;

        @Override
        default Double extract(ResultSet rs, String name) throws SQLException {
            return extractDbl(rs, name);
        }

        default <U> UnboundExtractor<U> map(DoubleFunction<U> f) {
            return (rs, name) -> f.apply(extractDbl(rs, name));
        }

        default Extractor.DblExtractor bindDbl(String name) {
            return rs -> extractDbl(rs, name);
        }
    }

    /**
     * A {@code DblTypeExtractor} instance.
     */
    DblUnboundExtractor DOUBLE_EX = ResultSet::getDouble;

    /**
     * An {@code UnboundExtractor} for enum values.
     * @param enumType  the enum type class
     * @param <E>       the enum type
     * @return          the extractor function
     */
    static <E extends Enum<E>> UnboundExtractor<E> enumTypeExtractor(Class<E> enumType) {
        return (rs, name) -> Enum.valueOf(enumType, rs.getString(name).toUpperCase());
    }

    /**
     * A combinator function to convert a {@code TypeExtractor} into one specialised for {@link Optional} values.
     * @param f         the extractor function for the contained type
     * @param <T>       the contained (i.e. optional) type
     * @return          the extractor function
     */
    static <T> UnboundExtractor<Optional<T>> optional(UnboundExtractor<T> f) {
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
    UnboundExtractor<Optional<String>> OPT_STRING_EX = optional(STRING_EX);

    /**
     * A {@code TypeExtractor} for {@code Optional} {@code String} values.
     * This converter will convert empty strings to an empty optional value.
     */
    UnboundExtractor<Optional<String>> OPT_NONEMPTY_STRING_EX =
            optional(STRING_EX)
                    .map(oi -> oi.flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(s)));

    /**
     * A {@code TypeExtractor} for {@code Optional} {@code String} values,
     * which converts the string to upper case.
     * Note, we can't use {@code optional(UP_STRING_EX)} here due to null values.
     */
    UnboundExtractor<Optional<String>> OPT_UP_STRING_EX =
            OPT_STRING_EX.map(os -> os.map(String::toUpperCase));

    /**
     * An {@code OptIntTypeExtractor} is essentially a {@code TypeExtractor<Optional<T>>} specialised for {@code int}
     * to avoid unnecessary boxing.
     */
    interface OptIntUnboundExtractor {
        OptionalInt extract(ResultSet rs, String name) throws SQLException;

        default <U> UnboundExtractor<Optional<U>> map(IntFunction<U> f) {
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
     * A {@link OptIntUnboundExtractor} instance.
     */
    OptIntUnboundExtractor OPT_INTEGER_EX = (rs, name) -> {
        final int value = INTEGER_EX.extractInt(rs, name);
        if (rs.wasNull()) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(value);
        }
    };

    /**
     * An {@code OptDblUnboundExtractor} is essentially a {@code UnboundExtractor<Optional<T>>} specialised for {@code double}
     * to avoid unnecessary boxing.
     */
    interface OptDblUnboundExtractor {
        OptionalDouble extract(ResultSet rs, String name) throws SQLException;

        default <U> UnboundExtractor<Optional<U>> map(DoubleFunction<U> f) {
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
     * A {@link OptDblUnboundExtractor} instance.
     */
    OptDblUnboundExtractor OPT_DOUBLE_EX = (rs, name) -> {
        final double value = DOUBLE_EX.extractDbl(rs, name);
        if (rs.wasNull()) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(value);
        }
    };
}
