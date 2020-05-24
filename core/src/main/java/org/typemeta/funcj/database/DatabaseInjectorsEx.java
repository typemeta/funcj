package org.typemeta.funcj.database;

import org.typemeta.funcj.injectors.*;
import org.typemeta.funcj.injectors.NumberedInjectorsEx.*;

import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;

import static org.typemeta.funcj.injectors.NumberedInjectorsEx.*;

public abstract class DatabaseInjectorsEx {

    public static final NumberedInjectorEx<PreparedStatement, Boolean, SQLException> BOOLEAN =
            (PreparedStatement ps, int n, Boolean value) -> {
                ps.setBoolean(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Byte, SQLException> BYTE =
            (PreparedStatement ps, int n, Byte value) -> {
                ps.setByte(n, value);
                return ps;
            };

    public static final DoubleNumberedInjectorEx<PreparedStatement, SQLException> DOUBLE =
            (PreparedStatement ps, int n, double value) -> {
                ps.setDouble(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, OptionalDouble, SQLException> OPT_DOUBLE =
            optional(DOUBLE);

    public static final NumberedInjectorEx<PreparedStatement, Float, SQLException> FLOAT =
            (PreparedStatement ps, int n, Float value) -> {
                ps.setFloat(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Optional<Float>, SQLException> OPT_FLOAT =
            optional(FLOAT);

    public static final IntNumberedInjectorEx<PreparedStatement, SQLException> INTEGER =
            (PreparedStatement ps, int n, int value) -> {
                ps.setDouble(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, OptionalInt, SQLException> OPT_INTEGER =
            optional(INTEGER);

    public static final LongNumberedInjectorEx<PreparedStatement, SQLException> LONG =
            (PreparedStatement ps, int n, long value) -> {
                ps.setLong(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, OptionalLong, SQLException> OPT_LONG =
            optional(LONG);

    public static final NumberedInjectorEx<PreparedStatement, Short, SQLException> SHORT =
            (PreparedStatement ps, int n, Short value) -> {
                ps.setShort(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Optional<Short>, SQLException> OPT_SHORT =
            optional(SHORT);

    public static final NumberedInjectorEx<PreparedStatement, String, SQLException> STRING =
            (PreparedStatement ps, int n, String value) -> {
                ps.setString(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Optional<String>, SQLException> OPT_STRING =
            optional(STRING);

    public static final NumberedInjectorEx<PreparedStatement, Date, SQLException> SQLDATE =
            (PreparedStatement ps, int n, Date value) -> {
                ps.setDate(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Optional<Date>, SQLException> OPT_SQLDATE =
            optional(SQLDATE);

    public static final NumberedInjectorEx<PreparedStatement, LocalDate, SQLException> LOCALDATE =
            SQLDATE.premap(Date::valueOf);

    public static final NumberedInjectorEx<PreparedStatement, Optional<LocalDate>, SQLException> OPT_LOCALDATE =
            optional(LOCALDATE);

    public static final NumberedInjectorEx<PreparedStatement, Time, SQLException> SQLTIME =
            (PreparedStatement ps, int n, Time value) -> {
                ps.setTime(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Optional<Time>, SQLException> OPT_SQLTIME =
            optional(SQLTIME);

    public static final NumberedInjectorEx<PreparedStatement, LocalTime, SQLException> LOCALTIME =
            SQLTIME.premap(Time::valueOf);

    public static final NumberedInjectorEx<PreparedStatement, Optional<LocalTime>, SQLException> OPT_LOCALTIME =
            optional(LOCALTIME);

    public static final NumberedInjectorEx<PreparedStatement, Timestamp, SQLException> SQLTIMESTAMP =
            (PreparedStatement ps, int n, Timestamp value) -> {
                ps.setTimestamp(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Optional<Timestamp>, SQLException> OPT_SQLTIMESTAMP =
            optional(SQLTIMESTAMP);

    public static final NumberedInjectorEx<PreparedStatement, LocalDateTime, SQLException> LOCALDATETIME =
            SQLTIMESTAMP.premap(Timestamp::valueOf);

    public static final NumberedInjectorEx<PreparedStatement, Optional<LocalDateTime>, SQLException> OPT_LOCALDATETIME =
            optional(LOCALDATETIME);
}
