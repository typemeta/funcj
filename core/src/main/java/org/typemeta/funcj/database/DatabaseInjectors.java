package org.typemeta.funcj.database;

import org.typemeta.funcj.injectors.*;
import org.typemeta.funcj.util.Exceptions;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;

/**
 * A set of database injection combinator functions.
 */
public abstract class DatabaseInjectors {

    public static <T> NumberedInjector<PreparedStatement, Optional<T>> optional(
            NumberedInjector<PreparedStatement, T> injr
    ) {
        return (ps, n, optValue) -> {
            if (optValue.isPresent()) {
                return injr.inject(ps, n, optValue.get());
            } else {
                try {
                    ps.setNull(n, ps.getParameterMetaData().getParameterType(n));
                    return ps;
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            }
        };
    }

    public static <T> NumberedInjector<PreparedStatement, OptionalDouble> optional(
            NumberedInjectors.DoubleNumberedInjector<PreparedStatement> injr
    ) {
        return (ps, n, optValue) -> {
            if (optValue.isPresent()) {
                return injr.inject(ps, n, optValue.getAsDouble());
            } else {
                try {
                    ps.setNull(n, Types.DOUBLE);
                    return ps;
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            }
        };
    }

    public static <T> NumberedInjector<PreparedStatement, OptionalInt> optional(
            NumberedInjectors.IntNumberedInjector<PreparedStatement> injr
    ) {
        return (ps, n, optValue) -> {
            if (optValue.isPresent()) {
                return injr.inject(ps, n, optValue.getAsInt());
            } else {
                try {
                    ps.setNull(n, Types.INTEGER);
                    return ps;
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            }
        };
    }

    public static <T> NumberedInjector<PreparedStatement, OptionalLong> optional(
            NumberedInjectors.LongNumberedInjector<PreparedStatement> injr
    ) {
        return (ps, n, optValue) -> {
            if (optValue.isPresent()) {
                return injr.inject(ps, n, optValue.getAsLong());
            } else {
                try {
                    ps.setNull(n, Types.BIGINT);
                    return ps;
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            }
        };
    }

    public static final NumberedInjector<PreparedStatement, Boolean> BOOLEAN =
            DatabaseInjectorsEx.BOOLEAN.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Boolean>> OPT_BOOLEAN =
            DatabaseInjectorsEx.OPT_BOOLEAN.unchecked();

    public static final NumberedInjector<PreparedStatement, Byte> BYTE =
            DatabaseInjectorsEx.BYTE.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Byte>> OPT_BYTE =
            DatabaseInjectorsEx.OPT_BYTE.unchecked();

    public static final NumberedInjector<PreparedStatement, Double> DOUBLE =
            DatabaseInjectorsEx.DOUBLE.unchecked();

    public static final NumberedInjector<PreparedStatement, OptionalDouble> OPT_DOUBLE =
            DatabaseInjectorsEx.OPT_DOUBLE.unchecked();

    public static final NumberedInjector<PreparedStatement, Float> FLOAT =
            DatabaseInjectorsEx.FLOAT.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Float>> OPT_FLOAT =
            DatabaseInjectorsEx.OPT_FLOAT.unchecked();

    public static final NumberedInjector<PreparedStatement, Integer> INTEGER =
            DatabaseInjectorsEx.INTEGER.unchecked();

    public static final NumberedInjector<PreparedStatement, OptionalInt> OPT_INTEGER =
            DatabaseInjectorsEx.OPT_INTEGER.unchecked();

    public static final NumberedInjector<PreparedStatement, Long> LONG =
            DatabaseInjectorsEx.LONG.unchecked();

    public static final NumberedInjector<PreparedStatement, OptionalLong> OPT_LONG =
            DatabaseInjectorsEx.OPT_LONG.unchecked();

    public static final NumberedInjector<PreparedStatement, Short> SHORT =
            DatabaseInjectorsEx.SHORT.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Short>> OPT_SHORT =
            DatabaseInjectorsEx.OPT_SHORT.unchecked();

    public static final NumberedInjector<PreparedStatement, String> STRING =
            DatabaseInjectorsEx.STRING.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<String>> OPT_STRING =
            DatabaseInjectorsEx.OPT_STRING.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<String>> OPT_EMPTY_STRING =
            (env, n, optVal) ->
                    optVal.map(String::isEmpty).orElse(false) ?
                            STRING.inject(env, n, optVal.get()) :
                            env;

    public static final NumberedInjector<PreparedStatement, Date> SQLDATE =
            DatabaseInjectorsEx.SQLDATE.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Date>> OPT_SQLDATE =
            DatabaseInjectorsEx.OPT_SQLDATE.unchecked();

    public static final NumberedInjector<PreparedStatement, LocalDate> LOCALDATE =
            DatabaseInjectorsEx.LOCALDATE.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<LocalDate>> OPT_LOCALDATE =
            DatabaseInjectorsEx.OPT_LOCALDATE.unchecked();

    public static final NumberedInjector<PreparedStatement, Time> SQLTIME =
            DatabaseInjectorsEx.SQLTIME.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Time>> OPT_SQLTIME =
            DatabaseInjectorsEx.OPT_SQLTIME.unchecked();

    public static final NumberedInjector<PreparedStatement, LocalTime> LOCALTIME =
            DatabaseInjectorsEx.LOCALTIME.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<LocalTime>> OPT_LOCALTIME =
            DatabaseInjectorsEx.OPT_LOCALTIME.unchecked();

    public static final NumberedInjector<PreparedStatement, Timestamp> SQLTIMESTAMP =
            DatabaseInjectorsEx.SQLTIMESTAMP.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Timestamp>> OPT_SQLTIMESTAMP =
            DatabaseInjectorsEx.OPT_SQLTIMESTAMP.unchecked();

    public static final NumberedInjector<PreparedStatement, LocalDateTime> LOCALDATETIME =
            DatabaseInjectorsEx.LOCALDATETIME.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<LocalDateTime>> OPT_LOCALDATETIME =
            DatabaseInjectorsEx.OPT_LOCALDATETIME.unchecked();

}
