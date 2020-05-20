package org.typemeta.funcj.database;

import java.sql.Time;
import java.time.*;
import java.util.*;

abstract class DatabaseMeta {

    static final String DATABASE_NAME = "testdb";
    static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    static final String JDBC_CONN_URL = "jdbc:derby:memory:" + DATABASE_NAME + ";create=true";

    static class Column {
        public final String name;
        public final String type;

        Column(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    static final Column[] TABLE_COLUMNS = new Column[] {
            new Column("col_boolean", "boolean"),
            new Column("col_date", "date"),
            new Column("col_decimal", "decimal"),
            new Column("col_double", "double"),
            new Column("col_float", "float"),
            new Column("col_int", "int"),
            new Column("col_varchar", "varchar"),
            new Column("col_time", "time"),
            new Column("col_timestamp", "timestamp"),
    };

    static final List<Class<?>> NUMERIC_TYPES =
            list(Double.class, Float.class, Long.class, Integer.class, Short.class, Byte.class);

    static final double[] NUMERIC_MAX = new double[] {
            Double.MAX_VALUE, Float.MAX_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Byte.MAX_VALUE
    };

    static List<Class<?>> list(Class<?>... values) {
        return Arrays.asList(values);
    }

    static final Map<String, List<Class<?>>> sqlTypeMap;

    static {
        sqlTypeMap = new TreeMap<>();
        sqlTypeMap.put("boolean", list(Boolean.class));
        sqlTypeMap.put("byte", NUMERIC_TYPES);
        sqlTypeMap.put("date", list(Date.class, LocalDate.class));
        sqlTypeMap.put("decimal", NUMERIC_TYPES);
        sqlTypeMap.put("double", NUMERIC_TYPES);
        sqlTypeMap.put("float", NUMERIC_TYPES);
        sqlTypeMap.put("int", NUMERIC_TYPES);
        sqlTypeMap.put("varchar", list(String.class));
        sqlTypeMap.put("time", list(LocalTime.class, Time.class));
        sqlTypeMap.put("timestamp", list(LocalDate.class, LocalDateTime.class, LocalTime.class));
    }

}
