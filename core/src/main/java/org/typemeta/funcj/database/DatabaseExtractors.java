package org.typemeta.funcj.database;

import org.typemeta.funcj.extractors.*;
import org.typemeta.funcj.extractors.NamedExtractorExs.*;
import org.typemeta.funcj.extractors.NamedExtractors.*;
import org.typemeta.funcj.util.Exceptions;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

public abstract class DatabaseExtractors {

    /**
     * A combinator function to convert a {@link NamedExtractor} into one for {@link Optional} values.
     * @param extr      the extractor function for the value type
     * @param <T>       the value type
     * @return          the extractor function for the optional value
     */
    public static <T> NamedExtractor<ResultSet, Optional<T>> optional(NamedExtractor<ResultSet, T> extr) {
        return NamedExtractor.of((ResultSet rs, String name) -> {
            final T value = extr.extract(rs, name);
            if (rs.wasNull()) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        });
    }

    /**
     * An {@code NamedExtractor} for enum values.
     * @param enumType  the enum type class
     * @param <E>       the enum type
     * @return          the enum extractor
     */
    public static <E extends Enum<E>> NamedExtractor<ResultSet, E> enumExtractor(Class<E> enumType) {
        return NamedExtractor.of((rs, name) -> Enum.valueOf(enumType, rs.getString(name).toUpperCase()));
    }

    /**
     * An {@code NamedExtractor} instance for {@link boolean} values.
     */
    public static final NamedExtractor<ResultSet, Boolean> BOOLEAN_EX = NamedExtractor.of(ResultSet::getBoolean);

    /**
     * A {@code NamedExtractor} instance for optional {@code boolean} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Boolean>> OPT_BOOLEAN_EX = optional(BOOLEAN_EX);

    /**
     * An {@code NamedExtractor} instance for {@link byte} values.
     */
    public static final NamedExtractor<ResultSet, Byte> BYTE_EX = NamedExtractor.of(ResultSet::getByte);

    /**
     * A {@code NamedExtractor} instance for optional {@code byte} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Byte>> OPT_BYTE_EX = optional(BYTE_EX);

    /**
     * A {@code NamedExtractor} instance for {@code double} values.
     */
    public static final DoubleNamedExtractor<ResultSet> DOUBLE_EX =
            DoubleNamedExtractorEx.<ResultSet, SQLException>of(ResultSet::getDouble).unchecked();

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
     * A {@code NamedExtractor} for optional {@code double} values.
     */
    public interface OptDoubleNamedExtractor extends NamedExtractor<ResultSet, OptionalDouble> {
        static OptDoubleNamedExtractor of(OptDoubleNamedExtractorEx extr) {
            return extr.unchecked();
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
    public static final OptDoubleNamedExtractor OPT_DOUBLE_EX =
            OptDoubleNamedExtractor.of((rs, name) -> {
                    final double value = DOUBLE_EX.extractDouble(rs, name);
                    if (rs.wasNull()) {
                        return OptionalDouble.empty();
                    } else {
                        return OptionalDouble.of(value);
                    }
                }
    );

    /**
     * An {@code NamedExtractor} instance for {@code float} values.
     */
    public static final NamedExtractor<ResultSet, Float> FLOAT_EX = NamedExtractor.of(ResultSet::getFloat);

    /**
     * A {@code NamedExtractor} instance for optional {@code float} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Float>> OPT_FLOAT_EX = optional(FLOAT_EX);

    /**
     * A {@code NamedExtractor} instance for {@code int} values.
     */
    public static final IntNamedExtractor<ResultSet> INTEGER_EX =
            IntNamedExtractorEx.<ResultSet, SQLException>of(ResultSet::getInt).unchecked();

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
     * A {@code NamedExtractor} for optional {@code int} values.
     */
    public interface OptIntNamedExtractor extends NamedExtractor<ResultSet, OptionalInt> {
        static OptIntNamedExtractor of(OptIntNamedExtractorEx extr) {
            return extr.unchecked();
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
    public static final OptIntNamedExtractor OPT_INTEGER_EX =
            OptIntNamedExtractor.of((rs, name) -> {
                    final int value = INTEGER_EX.extractInt(rs, name);
                    if (rs.wasNull()) {
                        return OptionalInt.empty();
                    } else {
                        return OptionalInt.of(value);
                    }
                }
            );

    /**
     * A {@code NamedExtractor} instance for {@code long} values.
     */
    public static final LongNamedExtractor<ResultSet> LONG_EX =
            LongNamedExtractorEx.<ResultSet, SQLException>of(ResultSet::getLong).unchecked();

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
     * A {@code NamedExtractor} for optional {@code long} values.
     */
    public interface OptLongNamedExtractor extends NamedExtractor<ResultSet, OptionalLong> {
        static OptLongNamedExtractor of(OptLongNamedExtractorEx extr) {
            return extr.unchecked();
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
    public static final OptLongNamedExtractor OPT_LONG_EX =
            OptLongNamedExtractor.of((rs, name) -> {
                    final long value = LONG_EX.extractLong(rs, name);
                    if (rs.wasNull()) {
                        return OptionalLong.empty();
                    } else {
                        return OptionalLong.of(value);
                    }
                }
            );

    /**
     * A {@code NamedExtractor} instance for {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Short> SHORT_EX = NamedExtractor.of(ResultSet::getShort);

    /**
     * A {@code NamedExtractor} instance for optional {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Short>> OPT_SHORT_EX = optional(SHORT_EX);

    /**
     * A {@code NamedExtractor} instance for {@code string} values.
     */
    public static final NamedExtractor<ResultSet, String> STRING_EX = NamedExtractor.of(ResultSet::getString);

    /**
     * A {@code NamedExtractor} instance for optional {@code string} values.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_STRING_EX = optional(STRING_EX);

    /**
     * A {@code NamedExtractor} instance for optional {@code string} values.
     * This converter will convert empty strings to an empty optional value.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_NONEMPTY_STRING_EX =
            optional(STRING_EX)
                    .map(oi -> oi.flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(s)));

    /**
     * An extractor for {@link Date} values.
     */
    public static final NamedExtractor<ResultSet, Date> SQLDATE_EX = NamedExtractor.of(ResultSet::getDate);

    /**
     * An extractor for optional {@code Date} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Date>> OPT_SQLDATE_EX = optional(SQLDATE_EX);


    /**
     * An extractor for {@link LocalDate} values.
     */
    public static final NamedExtractor<ResultSet, LocalDate> LOCALDATE_EX = SQLDATE_EX.map(Date::toLocalDate);

    /**
     * An extractor for optional {@code LocalDate} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalDate>> OPT_LOCALDATE_EX =
            optional(SQLDATE_EX)
                    .map(od -> od.map(Date::toLocalDate));

    /**
     * An extractor for {@link Time} values.
     */
    public static final NamedExtractor<ResultSet, Time> SQLTIME_EX = NamedExtractor.of(ResultSet::getTime);

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Time>> OPT_SQLTIME_EX = optional(SQLTIME_EX);

    /**
     * An extractor for {@link LocalTime} values.
     */
    public static final NamedExtractor<ResultSet, LocalTime> LOCALTIME_EX = SQLTIME_EX.map(Time::toLocalTime);

    /**
     * An extractor for optional {@code LocalTime} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalTime>> OPT_LOCALTIME_EX =
            optional(SQLTIME_EX)
                    .map(od -> od.map(Time::toLocalTime));

    /**
     * An extractor for {@link Timestamp} values.
     */
    public static final NamedExtractor<ResultSet, Timestamp> SQLTIMESTAMP_EX = NamedExtractor.of(ResultSet::getTimestamp);

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Timestamp>> OPT_SQLTIMESTAMP_EX = optional(SQLTIMESTAMP_EX);

    /**
     * An extractor for {@link LocalDateTime} values.
     */
    public static final NamedExtractor<ResultSet, LocalDateTime> LOCALDATETIME_EX =
            SQLTIMESTAMP_EX.map(Timestamp::toInstant)
                    .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()));

    /**
     * An extractor for optional {@code LocalDateTime} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalDateTime>> OPT_LOCALDATETIME_EX =
            optional(SQLTIMESTAMP_EX)
                    .map(ots -> ots.map(Timestamp::toInstant)
                            .map(inst -> LocalDateTime.ofInstant(inst, ZoneId.systemDefault()))
                    );
}
