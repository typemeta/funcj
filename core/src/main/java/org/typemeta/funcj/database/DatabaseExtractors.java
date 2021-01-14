package org.typemeta.funcj.database;

import org.typemeta.funcj.extractors.*;
import org.typemeta.funcj.extractors.NamedExtractors.*;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * A set of database extraction combinator functions.
 */
public abstract class DatabaseExtractors {

    /**
     * A combinator function to convert a {@link NamedExtractor} into one for {@link Optional} values.
     * @param extr      the extractor function for the value type
     * @param <T>       the value type
     * @return          the extractor function for the optional value
     */
    public static <T> NamedExtractor<ResultSet, Optional<T>> optional(NamedExtractor<ResultSet, T> extr) {
        return NamedExtractorEx.<ResultSet, Optional<T>, SQLException>of((ResultSet rs, String name) -> {
            final T value = extr.extract(rs, name);
            if (rs.wasNull()) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        }).unchecked();
    }

    /**
     * An {@code NamedExtractor} instance for {@link boolean} values.
     */
    public static final NamedExtractor<ResultSet, Boolean> BOOLEAN =
            DatabaseExtractorsEx.BOOLEAN.unchecked();

    /**
     * A {@code NamedExtractor} instance for optional {@code boolean} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Boolean>> OPT_BOOLEAN =
            DatabaseExtractorsEx.OPT_BOOLEAN.unchecked();

    /**
     * An {@code NamedExtractor} instance for {@link byte} values.
     */
    public static final NamedExtractor<ResultSet, Byte> BYTE =
            DatabaseExtractorsEx.BYTE.unchecked();


    /**
     * A {@code NamedExtractor} instance for optional {@code byte} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Byte>> OPT_BYTE =
            DatabaseExtractorsEx.OPT_BYTE.unchecked();

    /**
     * A {@code NamedExtractor} instance for {@code double} values.
     */
    public static final DoubleNamedExtractor<ResultSet> DOUBLE =
            DatabaseExtractorsEx.DOUBLE.unchecked();

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
            DatabaseExtractorsEx.OPT_DOUBLE.unchecked();

    /**
     * An {@code NamedExtractor} instance for {@code float} values.
     */
    public static final NamedExtractor<ResultSet, Float> FLOAT =
            DatabaseExtractorsEx.FLOAT.unchecked();

    /**
     * A {@code NamedExtractor} instance for optional {@code float} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Float>> OPT_FLOAT =
            DatabaseExtractorsEx.OPT_FLOAT.unchecked();

    /**
     * A {@code NamedExtractor} instance for {@code int} values.
     */
    public static final IntNamedExtractor<ResultSet> INTEGER =
            DatabaseExtractorsEx.INTEGER.unchecked();

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
            DatabaseExtractorsEx.OPT_INTEGER.unchecked();

    /**
     * A {@code NamedExtractor} instance for {@code long} values.
     */
    public static final LongNamedExtractor<ResultSet> LONG =
            DatabaseExtractorsEx.LONG.unchecked();

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
            DatabaseExtractorsEx.OPT_LONG.unchecked();

    /**
     * A {@code NamedExtractor} instance for {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Short> SHORT =
            DatabaseExtractorsEx.SHORT.unchecked();

    /**
     * A {@code NamedExtractor} instance for optional {@code short} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Short>> OPT_SHORT =
            DatabaseExtractorsEx.OPT_SHORT.unchecked();

    /**
     * A {@code NamedExtractor} instance for {@code string} values.
     */
    public static final NamedExtractor<ResultSet, String> STRING =
            DatabaseExtractorsEx.STRING.unchecked();

    /**
     * A {@code NamedExtractor} instance for optional {@code string} values.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_STRING =
            DatabaseExtractorsEx.OPT_STRING.unchecked();

    /**
     * A {@code NamedExtractor} instance for optional {@code string} values.
     * This converter will convert empty strings to an empty optional value.
     */
    public static final NamedExtractor<ResultSet, Optional<String>> OPT_NONEMPTY_STRING =
            DatabaseExtractorsEx.OPT_NONEMPTY_STRING.unchecked();

    /**
     * An extractor for {@link Date} values.
     */
    public static final NamedExtractor<ResultSet, Date> SQLDATE =
            DatabaseExtractorsEx.SQLDATE.unchecked();

    /**
     * An extractor for optional {@code Date} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Date>> OPT_SQLDATE =
            DatabaseExtractorsEx.OPT_SQLDATE.unchecked();


    /**
     * An extractor for {@link LocalDate} values.
     */
    public static final NamedExtractor<ResultSet, LocalDate> LOCALDATE =
            DatabaseExtractorsEx.LOCALDATE.unchecked();

    /**
     * An extractor for optional {@code LocalDate} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalDate>> OPT_LOCALDATE =
            DatabaseExtractorsEx.OPT_LOCALDATE.unchecked();

    /**
     * An extractor for {@link Time} values.
     */
    public static final NamedExtractor<ResultSet, Time> SQLTIME =
            DatabaseExtractorsEx.SQLTIME.unchecked();

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Time>> OPT_SQLTIME =
            DatabaseExtractorsEx.OPT_SQLTIME.unchecked();

    /**
     * An extractor for {@link LocalTime} values.
     */
    public static final NamedExtractor<ResultSet, LocalTime> LOCALTIME =
            DatabaseExtractorsEx.LOCALTIME.unchecked();

    /**
     * An extractor for optional {@code LocalTime} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalTime>> OPT_LOCALTIME =
            DatabaseExtractorsEx.OPT_LOCALTIME.unchecked();

    /**
     * An extractor for {@link Timestamp} values.
     */
    public static final NamedExtractor<ResultSet, Timestamp> SQLTIMESTAMP =
            DatabaseExtractorsEx.SQLTIMESTAMP.unchecked();

    /**
     * An extractor for optional {@code Time} values.
     */
    public static final NamedExtractor<ResultSet, Optional<Timestamp>> OPT_SQLTIMESTAMP =
            DatabaseExtractorsEx.OPT_SQLTIMESTAMP.unchecked();

    /**
     * An extractor for {@link LocalDateTime} values.
     */
    public static final NamedExtractor<ResultSet, LocalDateTime> LOCALDATETIME =
            DatabaseExtractorsEx.LOCALDATETIME.unchecked();

    /**
     * An extractor for optional {@code LocalDateTime} values.
     */
    public static final NamedExtractor<ResultSet, Optional<LocalDateTime>> OPT_LOCALDATETIME =
            DatabaseExtractorsEx.OPT_LOCALDATETIME.unchecked();
}
