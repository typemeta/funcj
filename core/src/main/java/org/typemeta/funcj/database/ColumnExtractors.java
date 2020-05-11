package org.typemeta.funcj.database;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

public abstract class ColumnExtractors {

    /**
     * A combinator function to convert a {@link ColumnExtractor} into one for {@link Optional} values.
     * @param f         the extractor function for the contained type
     * @param <T>       the contained (i.e. optional) type
     * @return          the extractor function
     */
    public static <T> ColumnExtractor<Optional<T>> optional(ColumnExtractor<T> extr) {
        return (rs, name) -> {
            final T value = extr.extract(rs, name);
            if (rs.wasNull()) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        };
    }

    /**
     * An {@code ColumnExtractor} for enum values.
     * @param enumType  the enum type class
     * @param <E>       the enum type
     * @return          the extractor function
     */
    public static <E extends Enum<E>> ColumnExtractor<E> enumExtractor(Class<E> enumType) {
        return (rs, name) -> Enum.valueOf(enumType, rs.getString(name).toUpperCase());
    }

    /**
     * An {@code ColumnExtractor} instance for {@link boolean} values.
     */
    public static final ColumnExtractor<Boolean> BOOLEAN_EX = ResultSet::getBoolean;

    /**
     * A {@code ColumnExtractor} instance for optional {@code boolean} values.
     */
    public static final ColumnExtractor<Optional<Boolean>> OPT_BOOLEAN_EX = optional(BOOLEAN_EX);

    /**
     * An {@code ColumnExtractor} instance for {@link byte} values.
     */
    public static final ColumnExtractor<Byte> BYTE_EX = ResultSet::getByte;

    /**
     * A {@code ColumnExtractor} instance for optional {@code byte} values.
     */
    public static final ColumnExtractor<Optional<Byte>> OPT_BYTE_EX = optional(BYTE_EX);

    /**
     * A {@code ColumnExtractor} for {@code double} values.
     */
    public interface DoubleColumnExtractor extends ColumnExtractor<Double> {
        double extractDbl(ResultSet rs, String name) throws SQLException;

        @Override
        default Double extract(ResultSet rs, String name) throws SQLException {
            return extractDbl(rs, name);
        }

        default <U> ColumnExtractor<U> map(DoubleFunction<U> f) {
            return (rs, name) -> f.apply(extractDbl(rs, name));
        }

        default Extractors.DoubleExtractor bindDouble(String name) {
            return rs -> extractDbl(rs, name);
        }
    }

    /**
     * A {@code ColumnExtractor} instance for {@code double} values.
     */
    public static final DoubleColumnExtractor DOUBLE_EX = ResultSet::getDouble;

    /**
     * A {@code ColumnExtractor} for optional {@code double} values.
     */
    public interface OptDoubleColumnExtractor extends ColumnExtractor<OptionalDouble> {

        default <U> ColumnExtractor<Optional<U>> map(DoubleFunction<U> f) {
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
     * A {@code ColumnExtractor} instance for optional {@code double} values.
     */
    public static final OptDoubleColumnExtractor OPT_DOUBLE_EX = (rs, name) -> {
        final double value = DOUBLE_EX.extractDbl(rs, name);
        if (rs.wasNull()) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(value);
        }
    };

    /**
     * An {@code ColumnExtractor} instance for {@link float} values.
     */
    public static final ColumnExtractor<Float> FLOAT_EX = ResultSet::getFloat;

    /**
     * A {@code ColumnExtractor} instance for optional {@code byte} values.
     */
    public static final ColumnExtractor<Optional<Float>> OPT_FLOAT_EX = optional(FLOAT_EX);

    /**
     * A {@code ColumnExtractor} for extracting {@code int} values.
     */
    public interface IntegerColumnExtractor extends ColumnExtractor<Integer> {
        int extractInt(ResultSet rs, String name) throws SQLException;

        @Override
        default Integer extract(ResultSet rs, String name) throws SQLException {
            return extractInt(rs, name);
        }

        default <U> ColumnExtractor<U> map(IntFunction<U> f) {
            return (rs, name) -> f.apply(extractInt(rs, name));
        }

        default Extractors.IntExtractor bindInteger(String name) {
            return rs -> extractInt(rs, name);
        }
    }

    /**
     * A {@link ColumnExtractor} instance for {@code int} values.
     */
    public static final IntegerColumnExtractor INTEGER_EX = ResultSet::getInt;

    /**
     * A {@code ColumnExtractor} for extracting {@code optional} {@code int} values.
     */
    public interface OptIntegerColumnExtractor extends ColumnExtractor<OptionalInt> {

        default <U> ColumnExtractor<Optional<U>> map(IntFunction<U> f) {
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
     * A {@code ColumnExtractor} instance for optional {@code int} values.
     */
    public static final OptIntegerColumnExtractor OPT_INTEGER_EX = (rs, name) -> {
        final int value = INTEGER_EX.extractInt(rs, name);
        return rs.wasNull() ? OptionalInt.empty() : OptionalInt.of(value);
    };

    /**
     * A {@code ColumnExtractor} for extracting {@code long} values.
     */
    public interface LongColumnExtractor extends ColumnExtractor<Long> {
        long extractLong(ResultSet rs, String name) throws SQLException;

        @Override
        default Long extract(ResultSet rs, String name) throws SQLException {
            return extractLong(rs, name);
        }

        default <U> ColumnExtractor<U> map(LongFunction<U> f) {
            return (rs, name) -> f.apply(extractLong(rs, name));
        }

        default Extractors.LongExtractor bindInteger(String name) {
            return rs -> extractLong(rs, name);
        }
    }

    /**
     * A {@code ColumnExtractor} instance for optional {@code long} values.
     */
    public static final LongColumnExtractor LONG_EX = ResultSet::getLong;

    /**
     * A {@code ColumnExtractor} for extracting optional {@code long} values.
     */
    public interface OptLongColumnExtractor extends ColumnExtractor<OptionalLong> {

        default <U> ColumnExtractor<Optional<U>> map(LongFunction<U> f) {
            return (rs, name) -> {
                final OptionalLong oi = extract(rs, name);
                if (oi.isPresent()) {
                    return Optional.of(f.apply(oi.getAsLong()));
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
     * A {@code ColumnExtractor} instance for optional {@code integer} values.
     */
    public static final OptLongColumnExtractor OPT_LONG_EX = (rs, name) -> {
        final long value = LONG_EX.extractLong(rs, name);
        return rs.wasNull() ? OptionalLong.empty() : OptionalLong.of(value);
    };

    /**
     * A {@code ColumnExtractor} instance for {@code short} values.
     */
    public static final ColumnExtractor<Short> SHORT_EX = ResultSet::getShort;

    /**
     * A {@code ColumnExtractor} instance for optional {@code short} values.
     */
    public static final ColumnExtractor<Optional<Short>> OPT_SHORT_EX = optional(SHORT_EX);

    /**
     * A {@code ColumnExtractor} instance for {@code string} values.
     */
    public static final ColumnExtractor<String> STRING_EX = ResultSet::getString;

    /**
     * A {@code ColumnExtractor} instance for optional {@code string} values.
     */
    public static final ColumnExtractor<Optional<String>> OPT_STRING_EX = optional(STRING_EX);

    /**
     * A {@code ColumnExtractor} instance for optional {@code string} values.
     * This converter will convert empty strings to an empty optional value.
     */
    public static final ColumnExtractor<Optional<String>> OPT_NONEMPTY_STRING_EX =
            optional(STRING_EX)
                    .map(oi -> oi.flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(s)));

    /**
     * An extractor for {@link Date} values.
     */
    public static final ColumnExtractor<Date> SQLDATE_EX = ResultSet::getDate;

    /**
     * An extractor for optional {@code Date} values.
     */
    public static final ColumnExtractor<Optional<Date>> OPT_SQLDATE_EX = optional(SQLDATE_EX);


    /**
     * An extractor for {@link LocalDate} values.
     */
    public static final ColumnExtractor<LocalDate> LOCALDATE_EX = SQLDATE_EX.map(Date::toLocalDate);

    /**
     * An extractor for optional {@code LocalDate} values.
     */
    public static final ColumnExtractor<Optional<LocalDate>> OPT_LOCALDATE_EX =
            optional(SQLDATE_EX)
                    .map(od -> od.map(Date::toLocalDate));

    /**
     * An extractor for {@link Time} values.
     */
    public static final ColumnExtractor<Time> SQLTIME_EX = ResultSet::getTime;

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final ColumnExtractor<Optional<Time>> OPT_SQLTIME_EX = optional(SQLTIME_EX);

    /**
     * An extractor for {@link LocalTime} values.
     */
    public static final ColumnExtractor<LocalTime> LOCALTIME_EX = SQLTIME_EX.map(Time::toLocalTime);

    /**
     * An extractor for optional {@code LocalTime} values.
     */
    public static final ColumnExtractor<Optional<LocalTime>> OPT_LOCALTIME_EX =
            optional(SQLTIME_EX)
                    .map(od -> od.map(Time::toLocalTime));

    /**
     * An extractor for {@link Timestamp} values.
     */
    public static final ColumnExtractor<Timestamp> SQLTIMESTAMP_EX = ResultSet::getTimestamp;

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final ColumnExtractor<Optional<Timestamp>> OPT_SQLTIMESTAMP_EX = optional(SQLTIMESTAMP_EX);

    /**
     * An extractor for {@link LocalDateTime} values.
     */
    public static final ColumnExtractor<LocalDateTime> LOCALDATETIME_EX =
            SQLTIMESTAMP_EX.map(Timestamp::toInstant)
                    .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()));

    /**
     * An extractor for optional {@code LocalDateTime} values.
     */
    public static final ColumnExtractor<Optional<LocalDateTime>> OPT_LOCALDATETIME_EX =
            optional(SQLTIMESTAMP_EX)
                    .map(ots -> ots.map(Timestamp::toInstant)
                            .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()))
                    );
}
