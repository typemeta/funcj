package org.typemeta.funcj.database;

import org.typemeta.funcj.injectors.NumberedInjectorEx;
import org.typemeta.funcj.injectors.NumberedInjectorsEx.*;

import java.sql.*;
import java.util.*;

import static org.typemeta.funcj.injectors.NumberedInjectorsEx.*;

public abstract class DatabaseInjectorsEx {

    public static final NumberedInjectorEx<PreparedStatement, Boolean, SQLException> BOOLEAN_EX =
            (PreparedStatement ps, int n, Boolean value) -> {
                ps.setBoolean(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, Byte, SQLException> BYTE_EX =
            (PreparedStatement ps, int n, Byte value) -> {
                ps.setByte(n, value);
                return ps;
            };

    public static final DoubleNumberedInjectorEx<PreparedStatement, SQLException> DOUBLE_EX =
            (PreparedStatement ps, int n, double value) -> {
                ps.setDouble(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, OptionalDouble, SQLException> OPT_DOUBLE_EX =
            optional(DOUBLE_EX);

    public static final IntNumberedInjectorEx<PreparedStatement, SQLException> INT_EX =
            (PreparedStatement ps, int n, int value) -> {
                ps.setDouble(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, OptionalInt, SQLException> OPT_INT_EX =
            optional(INT_EX);

    public static final LongNumberedInjectorEx<PreparedStatement, SQLException> LONG_EX =
            (PreparedStatement ps, int n, long value) -> {
                ps.setLong(n, value);
                return ps;
            };

    public static final NumberedInjectorEx<PreparedStatement, OptionalLong, SQLException> OPT_LONG_EX =
            optional(LONG_EX);

    public static final NumberedInjectorEx<PreparedStatement, String, SQLException> STRING_EX =
            (PreparedStatement ps, int n, String value) -> {
                ps.setString(n, value);
                return ps;
            };
}
