package org.typemeta.funcj.database;

import org.typemeta.funcj.injectors.NumberedInjectorEx;

import java.sql.*;

public abstract class DatabaseInjectorsEx {

    public static final NumberedInjectorEx<PreparedStatement, String, SQLException> STRING_EX =
            (PreparedStatement ps, int n, String value) -> {
                ps.setString(n, value);
                return ps;
            };
}
