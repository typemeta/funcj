package org.typemeta.funcj.database;

import org.typemeta.funcj.injectors.NumberedInjector;

import java.sql.*;
import java.time.*;
import java.util.Optional;

import static org.typemeta.funcj.injectors.NumberedInjectors.optional;

public abstract class DatabaseInjectors {
    public static final NumberedInjector<PreparedStatement, Boolean> BOOLEAN = DatabaseInjectorsEx.BOOLEAN.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Boolean>> OPT_BOOLEAN = optional(BOOLEAN);

    public static final NumberedInjector<PreparedStatement, Byte> BYTE = DatabaseInjectorsEx.BYTE.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Byte>> OPT_BYTE = optional(BYTE);

    public static final NumberedInjector<PreparedStatement, Double> DOUBLE = DatabaseInjectorsEx.DOUBLE.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Double>> OPT_DOUBLE = optional(DOUBLE);

    public static final NumberedInjector<PreparedStatement, Float> FLOAT = DatabaseInjectorsEx.FLOAT.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Float>> OPT_FLOAT = optional(FLOAT);

    public static final NumberedInjector<PreparedStatement, Integer> INTEGER = DatabaseInjectorsEx.INTEGER.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Integer>> OPT_INTEGER = optional(INTEGER);

    public static final NumberedInjector<PreparedStatement, Long> LONG = DatabaseInjectorsEx.LONG.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Long>> OPT_LONG = optional(LONG);

    public static final NumberedInjector<PreparedStatement, Short> SHORT = DatabaseInjectorsEx.SHORT.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Short>> OPT_SHORT = optional(SHORT);

    public static final NumberedInjector<PreparedStatement, String> STRING = DatabaseInjectorsEx.STRING.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<String>> OPT_STRING = optional(STRING);

    public static final NumberedInjector<PreparedStatement, Optional<String>> OPT_EMPTY_STRING =
            (env, n, optVal) ->
                    optVal.map(String::isEmpty).orElse(false) ?
                            STRING.inject(env, n, optVal.get()) :
                            env;

    public static final NumberedInjector<PreparedStatement, Date> SQLDATE = DatabaseInjectorsEx.SQLDATE.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Date>> OPT_SQLDATE = optional(SQLDATE);

    public static final NumberedInjector<PreparedStatement, LocalDate> LOCALDATE = DatabaseInjectorsEx.LOCALDATE.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<LocalDate>> OPT_LOCALDATE = optional(LOCALDATE);

    public static final NumberedInjector<PreparedStatement, Time> SQLTIME = DatabaseInjectorsEx.SQLTIME.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Time>> OPT_SQLTIME = optional(SQLTIME);

    public static final NumberedInjector<PreparedStatement, LocalTime> LOCALTIME = DatabaseInjectorsEx.LOCALTIME.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<LocalTime>> OPT_LOCALTIME = optional(LOCALTIME);

    public static final NumberedInjector<PreparedStatement, Timestamp> SQLTIMESTAMP = DatabaseInjectorsEx.SQLTIMESTAMP.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Timestamp>> OPT_SQLTIMESTAMP = optional(SQLTIMESTAMP);

    public static final NumberedInjector<PreparedStatement, LocalDateTime> LOCALDATETIME = DatabaseInjectorsEx.LOCALDATETIME.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<LocalDateTime>> OPT_LOCALDATETIME = optional(LOCALDATETIME);

}
