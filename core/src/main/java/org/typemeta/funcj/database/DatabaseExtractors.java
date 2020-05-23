package org.typemeta.funcj.database;

import org.typemeta.funcj.database.DatabaseExtractorsEx.*;
import org.typemeta.funcj.extractors.*;
import org.typemeta.funcj.extractors.NamedExtractors.*;
import org.typemeta.funcj.extractors.NamedExtractorsEx.*;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * A set of extraction functions and combinator funcions.
 */
public abstract class DatabaseExtractors {

    /**
     * A combinator function to convert a {@link NamedExtractor} into one for {@link Optional} values.
     * @param extr      the extractor function for the value type
     * @param <T>       the value type
     * @return          the extractor function for the optional value
     */
    public static <T> NamedExtractor<ResultSet, Optional<T>> optional(NamedExtractor<ResultSet, T> extr) {
        return NamedExtractor.ofEx((ResultSet rs, String name) -> {
            final T value = extr.extract(rs, name);
            if (rs.wasNull()) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        });
    }

    /**
     * An {@code NamedExtractor} instance for {@link boolean} values.
     */
    public static final NamedExtractor<ResultSet, Boolean> BOOLEAN = NamedExtractor.ofEx(ResultSet::getBoolean);

    /**
     * A {@code NamedExtractor} instance for optional {@code boolean} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Boolean>> OPT_BOOLEAN = optional(BOOLEAN);

    /**
     * An {@code NamedExtractor} instance for {@link byte} values.
     */
    public static final NamedExtractor<ResultSet, Byte> BYTE = NamedExtractor.ofEx(ResultSet::getByte);

    /**
     * A {@code NamedExtractor} instance for optional {@code byte} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Byte>> OPT_BYTE = optional(BYTE);

    /**
     * A {@code NamedExtractor} instance for {@code double} values.
     */
    public static final DoubleNamedExtractor<ResultSet> DOUBLE =
            DoubleNamedExtractorEx.<ResultSet, SQLException>of(ResultSet::getDouble).unchecked();

    /**
     * A {@code NamedExtractor} for optional {@code double} values.
     */
    public interface OptDoubleNamedExtractor extends NamedExtractor<ResultSet, OptionalDouble> {
        static OptDoubleNamedExtractor of(OptDoubleNamedExtractor extr) {
            return extr;
        }

        default <U> NamedExtractor<ResultSet, Optional<U>> map(DoubleFunction<U> f) {
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
        default Extractor<ResultSet, OptionalDouble> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@code NamedExtractor} instance for optional {@code double} values.
     */
    public static final OptDoubleNamedExtractor OPT_DOUBLE =
            OptDoubleNamedExtractorEx.of((rs, name) -> {
                    final double value = DOUBLE.extractDouble(rs, name);
                    if (rs.wasNull()) {
                        return OptionalDouble.empty();
                    } else {
                        return OptionalDouble.of(value);
                    }
                }
            ).unchecked();

    /**
     * An {@code NamedExtractor} instance for {@code float} values.
     */
    public static final NamedExtractor<ResultSet, Float> FLOAT = NamedExtractor.ofEx(ResultSet::getFloat);

    /**
     * A {@code NamedExtractor} instance for optional {@code float} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Float>> OPT_FLOAT = optional(FLOAT);

    /**
     * A {@code NamedExtractor} instance for {@code int} values.
     */
    public static final IntNamedExtractor<ResultSet> INTEGER =
            IntNamedExtractorEx.<ResultSet, SQLException>of(ResultSet::getInt).unchecked();

    /**
     * A {@code NamedExtractor} for optional {@code int} values.
     */
    public interface OptIntNamedExtractor extends NamedExtractor<ResultSet, OptionalInt> {
        static OptIntNamedExtractor of(OptIntNamedExtractor extr) {
            return extr;
        }

        default <U> NamedExtractor<ResultSet, Optional<U>> map(IntFunction<U> f) {
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
        default Extractor<ResultSet, OptionalInt> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@code NamedExtractor} instance for optional {@code int} values.
     */
    public static final OptIntNamedExtractor OPT_INTEGER =
            OptIntNamedExtractorEx.of((rs, name) -> {
                    final int value = INTEGER.extractInt(rs, name);
                    if (rs.wasNull()) {
                        return OptionalInt.empty();
                    } else {
                        return OptionalInt.of(value);
                    }
                }
            ).unchecked();

    /**
     * A {@code NamedExtractor} instance for {@code long} values.
     */
    public static final LongNamedExtractor<ResultSet> LONG =
            LongNamedExtractorEx.<ResultSet, SQLException>of(ResultSet::getLong).unchecked();

    /**
     * A {@code NamedExtractor} for optional {@code long} values.
     */
    public interface OptLongNamedExtractor extends NamedExtractor<ResultSet, OptionalLong> {
        static OptLongNamedExtractor of(OptLongNamedExtractor extr) {
            return extr;
        }

        default <U> NamedExtractor<ResultSet, Optional<U>> map(LongFunction<U> f) {
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
        default Extractor<ResultSet, OptionalLong> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@code NamedExtractor} instance for optional {@code long} values.
     */
    public static final OptLongNamedExtractor OPT_LONG =
            OptLongNamedExtractorEx.of((rs, name) -> {
                    final long value = LONG.extractLong(rs, name);
                    if (rs.wasNull()) {
                        return OptionalLong.empty();
                    } else {
                        return OptionalLong.of(value);
                    }
                }
            ).unchecked();

    /**
     * A {@code NamedExtractor} instance for {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Short> SHORT = NamedExtractor.ofEx(ResultSet::getShort);

    /**
     * A {@code NamedExtractor} instance for optional {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Short>> OPT_SHORT = optional(SHORT);

    /**
     * A {@code NamedExtractor} instance for {@code string} values.
     */
    public static final NamedExtractor<ResultSet, String> STRING = NamedExtractor.ofEx(ResultSet::getString);

    /**
     * A {@code NamedExtractor} instance for optional {@code string} values.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_STRING = optional(STRING);

    /**
     * A {@code NamedExtractor} instance for optional {@code string} values.
     * This converter will convert empty strings to an empty optional value.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_NONEMPTY_STRING =
            optional(STRING)
                    .map(oi -> oi.flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(s)));

    /**
     * An extractor for {@link Date} values.
     */
    public static final NamedExtractor<ResultSet, Date> SQLDATE = NamedExtractor.ofEx(ResultSet::getDate);

    /**
     * An extractor for optional {@code Date} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Date>> OPT_SQLDATE = optional(SQLDATE);


    /**
     * An extractor for {@link LocalDate} values.
     */
    public static final NamedExtractor<ResultSet, LocalDate> LOCALDATE = SQLDATE.map(Date::toLocalDate);

    /**
     * An extractor for optional {@code LocalDate} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalDate>> OPT_LOCALDATE =
            optional(SQLDATE)
                    .map(od -> od.map(Date::toLocalDate));

    /**
     * An extractor for {@link Time} values.
     */
    public static final NamedExtractor<ResultSet, Time> SQLTIME = NamedExtractor.ofEx(ResultSet::getTime);

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Time>> OPT_SQLTIME = optional(SQLTIME);

    /**
     * An extractor for {@link LocalTime} values.
     */
    public static final NamedExtractor<ResultSet, LocalTime> LOCALTIME = SQLTIME.map(Time::toLocalTime);

    /**
     * An extractor for optional {@code LocalTime} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalTime>> OPT_LOCALTIME =
            optional(SQLTIME)
                    .map(od -> od.map(Time::toLocalTime));

    /**
     * An extractor for {@link Timestamp} values.
     */
    public static final NamedExtractor<ResultSet, Timestamp> SQLTIMESTAMP =
            NamedExtractor.ofEx(ResultSet::getTimestamp);

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Timestamp>> OPT_SQLTIMESTAMP = optional(SQLTIMESTAMP);

    /**
     * An extractor for {@link LocalDateTime} values.
     */
    public static final NamedExtractor<ResultSet, LocalDateTime> LOCALDATETIME =
            SQLTIMESTAMP.map(Timestamp::toInstant)
                    .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()));

    /**
     * An extractor for optional {@code LocalDateTime} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalDateTime>> OPT_LOCALDATETIME =
            optional(SQLTIMESTAMP)
                    .map(ots -> ots.map(Timestamp::toInstant)
                            .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()))
                    );
}
