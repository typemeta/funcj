package org.typemeta.funcj.database;

import org.typemeta.funcj.database.NamedExtractorExs.DoubleNamedExtractorEx;
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
     * An {@code ColumnExtractor} for enum values.
     * @param enumType  the enum type class
     * @param <E>       the enum type
     * @return          the extractor function
     */
    public static <E extends Enum<E>> NamedExtractor<ResultSet, E> enumExtractor(Class<E> enumType) {
        return NamedExtractor.of((rs, name) -> Enum.valueOf(enumType, rs.getString(name).toUpperCase()));
    }

    /**
     * An {@code ColumnExtractor} instance for {@link boolean} values.
     */
    public static final NamedExtractor<ResultSet, Boolean> BOOLEAN_EX = NamedExtractor.of(ResultSet::getBoolean);

    /**
     * A {@code ColumnExtractor} instance for optional {@code boolean} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Boolean>> OPT_BOOLEAN_EX = optional(BOOLEAN_EX);

    /**
     * An {@code ColumnExtractor} instance for {@link byte} values.
     */
    public static final NamedExtractor<ResultSet, Byte> BYTE_EX = NamedExtractor.of(ResultSet::getByte);

    /**
     * A {@code ColumnExtractor} instance for optional {@code byte} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Byte>> OPT_BYTE_EX = optional(BYTE_EX);

    /**
     * A {@code ColumnExtractor} for {@code double} values.
     */
    public interface DoubleNamedExtractor extends NamedExtractor<ResultSet, Double> {
        static DoubleNamedExtractor of(DoubleNamedExtractorEx<ResultSet, SQLException> extr) {
            return (rs, name) -> {
                try {
                    return extr.extractDouble(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }

        double extractDbl(ResultSet rs, String name);

        @Override
        default Double extract(ResultSet rs, String name) {
            return extractDbl(rs, name);
        }

        default <U> NamedExtractor<ResultSet, U> map(DoubleFunction<U> f) {
            return (rs, name) -> f.apply(extractDbl(rs, name));
        }

        default Extractors.DoubleExtractor<ResultSet> bindDouble(String name) {
            return rs -> extractDbl(rs, name);
        }
    }

    /**
     * A {@code NamedExtractor} instance for {@code double} values.
     */
    public static final DoubleNamedExtractor DOUBLE_EX = DoubleNamedExtractor.of(ResultSet::getDouble);

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
     * A {@code ColumnExtractor} for optional {@code double} values.
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

        default Extractor<ResultSet, OptionalDouble> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@code ColumnExtractor} instance for optional {@code double} values.
     */
    public static final OptDoubleNamedExtractor OPT_DOUBLE_EX =
            OptDoubleNamedExtractor.of((rs, name) -> {
                    final double value = DOUBLE_EX.extractDbl(rs, name);
                    if (rs.wasNull()) {
                        return OptionalDouble.empty();
                    } else {
                        return OptionalDouble.of(value);
                    }
                }
    );

    /**
     * An {@code ColumnExtractor} instance for {@link float} values.
     */
    public static final NamedExtractor<ResultSet, Float> FLOAT_EX = NamedExtractor.of(ResultSet::getFloat);

    /**
     * A {@code ColumnExtractor} instance for optional {@code byte} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Float>> OPT_FLOAT_EX = optional(FLOAT_EX);

    /**
     * A {@code ColumnExtractor} for extracting {@code int} values.
     */
    public interface IntNamedExtractor extends NamedExtractor<ResultSet, Integer> {
        static IntNamedExtractor of(ToDouble toDouble) {
            return (rs, name) -> {
                try {
                    return toDouble.extractDbl(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }

        int extractInt(ResultSet rs, String name);

        @Override
        default Integer extract(ResultSet rs, String name) {
            return extractInt(rs, name);
        }

        default <U> NamedExtractor<ResultSet, U> map(IntFunction<U> f) {
            return (rs, name) -> f.apply(extractInt(rs, name));
        }

        default Extractors.IntExtractor<ResultSet> bindInteger(String name) {
            return rs -> extractInt(rs, name);
        }
    }

    /**
     * A {@link NamedExtractor} instance for {@code int} values.
     */
    public static final IntNamedExtractor INTEGER_EX = ResultSet::getInt;

    /**
     * A {@code ColumnExtractor} for extracting {@code optional} {@code int} values.
     */
    public interface OptIntegerNamedExtractor extends NamedExtractor<ResultSet, OptionalInt> {
        static IntNamedExtractor of(NamedExtractorEx<ResultSet, OptionalInt, SQLException> extr) {
            return (rs, name) -> {
                try {
                    return extr.extract(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }

        default <U> NamedExtractor<ResultSet, Optional<U>> map(IntFunction<U> f) {
            return (rs, name) -> {
                final OptionalInt oi = extract(rs, name);
                if (oi.isPresent()) {
                    return Optional.of(f.apply(oi.getAsInt()));
                } else {
                    return Optional.empty();
                }
            };
        }

        default Extractor<ResultSet, OptionalInt> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@code ColumnExtractor} instance for optional {@code int} values.
     */
    public static final OptIntegerNamedExtractor OPT_INTEGER_EX = NamedExtractor.of((rs, name) -> {
        final int value = INTEGER_EX.extractInt(rs, name);
        return rs.wasNull() ? OptionalInt.empty() : OptionalInt.of(value);
    });

    /**
     * A {@code ColumnExtractor} for extracting {@code long} values.
     */
    public interface LongNamedExtractor extends NamedExtractor<ResultSet, Long> {
        long extractLong(ResultSet rs, String name) throws SQLException;

        @Override
        default Long extract(ResultSet rs, String name) throws SQLException {
            return extractLong(rs, name);
        }

        default <U> NamedExtractor<ResultSet, U> map(LongFunction<U> f) {
            return (rs, name) -> f.apply(extractLong(rs, name));
        }

        default Extractors.LongExtractor bindInteger(String name) {
            return rs -> extractLong(rs, name);
        }
    }

    /**
     * A {@code ColumnExtractor} instance for optional {@code long} values.
     */
    public static final LongNamedExtractor LONG_EX = ResultSet::getLong;

    /**
     * A {@code ColumnExtractor} for extracting optional {@code long} values.
     */
    public interface OptLongNamedExtractor extends NamedExtractor<ResultSet, OptionalLong> {

        default <U> NamedExtractor<ResultSet, Optional<U>> map(LongFunction<U> f) {
            return (rs, name) -> {
                final OptionalLong oi = extract(rs, name);
                if (oi.isPresent()) {
                    return Optional.of(f.apply(oi.getAsLong()));
                } else {
                    return Optional.empty();
                }
            };
        }

        default Extractor<ResultSet, OptionalLong> bind(String name) {
            return rs -> extract(rs, name);
        }
    }

    /**
     * A {@code ColumnExtractor} instance for optional {@code integer} values.
     */
    public static final OptLongNamedExtractor OPT_LONG_EX = (rs, name) -> {
        final long value = LONG_EX.extractLong(rs, name);
        return rs.wasNull() ? OptionalLong.empty() : OptionalLong.of(value);
    };

    /**
     * A {@code ColumnExtractor} instance for {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Short> SHORT_EX = ResultSet::getShort;

    /**
     * A {@code ColumnExtractor} instance for optional {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Short>> OPT_SHORT_EX = optional(SHORT_EX);

    /**
     * A {@code ColumnExtractor} instance for {@code string} values.
     */
    public static final NamedExtractor<ResultSet, String> STRING_EX = ResultSet::getString;

    /**
     * A {@code ColumnExtractor} instance for optional {@code string} values.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_STRING_EX = optional(STRING_EX);

    /**
     * A {@code ColumnExtractor} instance for optional {@code string} values.
     * This converter will convert empty strings to an empty optional value.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_NONEMPTY_STRING_EX =
            optional(STRING_EX)
                    .map(oi -> oi.flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(s)));

    /**
     * An extractor for {@link Date} values.
     */
    public static final NamedExtractor<ResultSet, Date> SQLDATE_EX = ResultSet::getDate;

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
    public static final NamedExtractor<ResultSet, Time> SQLTIME_EX = ResultSet::getTime;

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
    public static final NamedExtractor<ResultSet, Timestamp> SQLTIMESTAMP_EX = ResultSet::getTimestamp;

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
