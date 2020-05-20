package org.typemeta.funcj.database;

import org.typemeta.funcj.database.DatabaseExtractors.*;
import org.typemeta.funcj.extractors.*;
import org.typemeta.funcj.extractors.NamedExtractorsEx.*;
import org.typemeta.funcj.util.Exceptions;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * A set of extraction functions and combinator funcions.
 */
public abstract class DatabaseExtractorsEx {

    /**
     * A combinator function to convert a {@link NamedExtractorEx} into one for {@link Optional} values.
     * @param extr      the extractor function for the value type
     * @param <T>       the value type
     * @return          the extractor function for the optional value
     */
    public static <T> NamedExtractorEx<ResultSet, Optional<T>, SQLException> optional(NamedExtractorEx<ResultSet, T, SQLException> extr) {
        return (ResultSet rs, String name) -> {
            final T value = extr.extract(rs, name);
            if (rs.wasNull()) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        };
    }

    /**
     * An {@code NamedExtractorEx} instance for {@link boolean} values.
     */
    public static final NamedExtractorEx<ResultSet, Boolean, SQLException> BOOLEAN = ResultSet::getBoolean;

    /**
     * A {@code NamedExtractorEx} instance for optional {@code boolean} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<Boolean>, SQLException> OPT_BOOLEAN = optional(BOOLEAN);

    /**
     * An {@code NamedExtractorEx} instance for {@link byte} values.
     */
    public static final NamedExtractorEx<ResultSet, Byte, SQLException> BYTE = ResultSet::getByte;

    /**
     * A {@code NamedExtractorEx} instance for optional {@code byte} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<Byte>, SQLException> OPT_BYTE = optional(BYTE);

    /**
     * A {@code NamedExtractorEx} instance for {@code double} values.
     */
    public static final DoubleNamedExtractorEx<ResultSet, SQLException> DOUBLE = ResultSet::getDouble;

    /**
     * A {@code NamedExtractorEx} for optional {@code double} values.
     */
    public interface OptDoubleNamedExtractorEx extends NamedExtractorEx<ResultSet, OptionalDouble, SQLException> {
        default <U> NamedExtractorEx<ResultSet, Optional<U>, SQLException> map(DoubleFunction<U> f) {
            return (rs, name) -> {
                final OptionalDouble od = extract(rs, name);
                if (od.isPresent()) {
                    return Optional.of(f.apply(od.getAsDouble()));
                } else {
                    return Optional.empty();
                }
            };
        }

        @Override
        default ExtractorEx<ResultSet, OptionalDouble, SQLException> bind(String name) {
            return rs -> extract(rs, name);
        }

        @Override
        default OptDoubleNamedExtractor unchecked() {
            return (rs, name) -> {
                try {
                    return extract(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }
    }

    /**
     * A {@code NamedExtractorEx} instance for optional {@code double} values.
     */
    public static final OptDoubleNamedExtractorEx OPT_DOUBLE =
            (rs, name) -> {
                final double value = DOUBLE.extractDouble(rs, name);
                if (rs.wasNull()) {
                    return OptionalDouble.empty();
                } else {
                    return OptionalDouble.of(value);
                }
            };

    /**
     * An {@code NamedExtractorEx} instance for {@code float} values.
     */
    public static final NamedExtractorEx<ResultSet, Float, SQLException> FLOAT = ResultSet::getFloat;

    /**
     * A {@code NamedExtractorEx} instance for optional {@code float} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<Float>, SQLException> OPT_FLOAT = optional(FLOAT);

    /**
     * A {@code NamedExtractor} instance for {@code int} values.
     */
    public static final NamedExtractorsEx.IntNamedExtractorEx<ResultSet, SQLException> INTEGER = ResultSet::getInt;

    /**
     * A {@code NamedExtractorEx} for optional {@code int} values.
     */
    public interface OptIntNamedExtractorEx extends NamedExtractorEx<ResultSet, OptionalInt, SQLException> {
        default <U> NamedExtractorEx<ResultSet, Optional<U>, SQLException> map(IntFunction<U> f) {
            return (rs, name) -> {
                final OptionalInt od = extract(rs, name);
                if (od.isPresent()) {
                    return Optional.of(f.apply(od.getAsInt()));
                } else {
                    return Optional.empty();
                }
            };
        }

        @Override
        default ExtractorEx<ResultSet, OptionalInt, SQLException> bind(String name) {
            return rs -> extract(rs, name);
        }

        @Override
        default OptIntNamedExtractor unchecked() {
            return (rs, name) -> {
                try {
                    return extract(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }
    }

    /**
     * A {@code NamedExtractorEx} instance for optional {@code int} values.
     */
    public static final OptIntNamedExtractorEx OPT_INTEGER =
            (rs, name) -> {
                final int value = INTEGER.extractInt(rs, name);
                if (rs.wasNull()) {
                    return OptionalInt.empty();
                } else {
                    return OptionalInt.of(value);
                }
            };

    /**
     * A {@code NamedExtractorEx} instance for {@code long} values.
     */
    public static final LongNamedExtractorEx<ResultSet, SQLException> LONG = ResultSet::getLong;

    /**
     * A {@code NamedExtractorEx} for optional {@code long} values.
     */
    public interface OptLongNamedExtractorEx extends NamedExtractorEx<ResultSet, OptionalLong, SQLException> {
        default <U> NamedExtractorEx<ResultSet, Optional<U>, SQLException> map(LongFunction<U> f) {
            return (rs, name) -> {
                final OptionalLong od = extract(rs, name);
                if (od.isPresent()) {
                    return Optional.of(f.apply(od.getAsLong()));
                } else {
                    return Optional.empty();
                }
            };
        }

        @Override
        default ExtractorEx<ResultSet, OptionalLong, SQLException> bind(String name) {
            return rs -> extract(rs, name);
        }

        @Override
        default OptLongNamedExtractor unchecked() {
            return (rs, name) -> {
                try {
                    return extract(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }
    }

    /**
     * A {@code NamedExtractorEx} instance for optional {@code long} values.
     */
    public static final OptLongNamedExtractorEx OPT_LONG =
            (rs, name) -> {
                final long value = LONG.extractLong(rs, name);
                if (rs.wasNull()) {
                    return OptionalLong.empty();
                } else {
                    return OptionalLong.of(value);
                }
            };

    /**
     * A {@code NamedExtractorEx} instance for {@code short} values.
     */
    public static final NamedExtractorEx<ResultSet, Short, SQLException> SHORT = ResultSet::getShort;

    /**
     * A {@code NamedExtractorEx} instance for optional {@code short} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<Short>, SQLException> OPT_SHORT = optional(SHORT);

    /**
     * A {@code NamedExtractorEx} instance for {@code string} values.
     */
    public static final NamedExtractorEx<ResultSet, String, SQLException> STRING = ResultSet::getString;

    /**
     * A {@code NamedExtractorEx} instance for optional {@code string} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<String>, SQLException> OPT_STRING = optional(STRING);

    /**
     * A {@code NamedExtractorEx} instance for optional {@code string} values.
     * This converter will convert empty strings to an empty optional value.
     */
    public static final NamedExtractorEx<ResultSet, Optional<String>, SQLException> OPT_NONEMPTY_STRING =
            optional(STRING)
                    .map(oi -> oi.flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(s)));

    /**
     * An extractor for {@link Date} values.
     */
    public static final NamedExtractorEx<ResultSet, Date, SQLException> SQLDATE = ResultSet::getDate;

    /**
     * An extractor for optional {@code Date} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<Date>, SQLException> OPT_SQLDATE = optional(SQLDATE);


    /**
     * An extractor for {@link LocalDate} values.
     */
    public static final NamedExtractorEx<ResultSet, LocalDate, SQLException> LOCALDATE = SQLDATE.map(Date::toLocalDate);

    /**
     * An extractor for optional {@code LocalDate} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<LocalDate>, SQLException> OPT_LOCALDATE =
            optional(SQLDATE)
                    .map(od -> od.map(Date::toLocalDate));

    /**
     * An extractor for {@link Time} values.
     */
    public static final NamedExtractorEx<ResultSet, Time, SQLException> SQLTIME = ResultSet::getTime;

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<Time>, SQLException> OPT_SQLTIME = optional(SQLTIME);

    /**
     * An extractor for {@link LocalTime} values.
     */
    public static final NamedExtractorEx<ResultSet, LocalTime, SQLException> LOCALTIME = SQLTIME.map(Time::toLocalTime);

    /**
     * An extractor for optional {@code LocalTime} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<LocalTime>, SQLException> OPT_LOCALTIME =
            optional(SQLTIME)
                    .map(od -> od.map(Time::toLocalTime));

    /**
     * An extractor for {@link Timestamp} values.
     */
    public static final NamedExtractorEx<ResultSet, Timestamp, SQLException> SQLTIMESTAMP = ResultSet::getTimestamp;

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<Timestamp>, SQLException> OPT_SQLTIMESTAMP = optional(SQLTIMESTAMP);

    /**
     * An extractor for {@link LocalDateTime} values.
     */
    public static final NamedExtractorEx<ResultSet, LocalDateTime, SQLException> LOCALDATETIME =
            SQLTIMESTAMP.map(Timestamp::toInstant)
                    .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()));

    /**
     * An extractor for optional {@code LocalDateTime} values.
     */
    public static final NamedExtractorEx<ResultSet, Optional<LocalDateTime>, SQLException> OPT_LOCALDATETIME =
            optional(SQLTIMESTAMP)
                    .map(ots -> ots.map(Timestamp::toInstant)
                            .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()))
                    );
}
