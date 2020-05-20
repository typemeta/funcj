package org.typemeta.funcj.database;

import org.typemeta.funcj.injectors.NumberedInjector;

import java.sql.*;
import java.util.Optional;

import static org.typemeta.funcj.database.DatabaseInjectorsEx.STRING_EX;
import static org.typemeta.funcj.injectors.NumberedInjectors.optional;

public abstract class DatabaseInjectors {

    public static final NumberedInjector<PreparedStatement, String> STRING = STRING_EX.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<String>> OPT_STRING = optional(STRING);
}
