package org.typemeta.funcj.database;

import org.typemeta.funcj.injectors.NumberedInjector;

import java.sql.PreparedStatement;
import java.util.Optional;

import static org.typemeta.funcj.database.DatabaseInjectorsEx.*;
import static org.typemeta.funcj.injectors.NumberedInjectors.optional;

public abstract class DatabaseInjectors {
    public static final NumberedInjector<PreparedStatement, Boolean> BOOLEAN = BOOLEAN_EX.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Boolean>> OPT_BOOLEAN = optional(BOOLEAN);

    public static final NumberedInjector<PreparedStatement, Byte> BYTE = BYTE_EX.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Byte>> OPT_BYTE = optional(BYTE);

    public static final NumberedInjector<PreparedStatement, Double> DOUBLE = DOUBLE_EX.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Double>> OPT_DOUBLE = optional(DOUBLE);

    public static final NumberedInjector<PreparedStatement, Integer> INT = INT_EX.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Integer>> OPT_INT = optional(INT);

    public static final NumberedInjector<PreparedStatement, Long> LONG = LONG_EX.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<Long>> OPT_LONG = optional(LONG);

    public static final NumberedInjector<PreparedStatement, String> STRING = STRING_EX.unchecked();

    public static final NumberedInjector<PreparedStatement, Optional<String>> OPT_STRING = optional(STRING);
}
