package org.typemeta.funcj.database;

import org.junit.*;
import org.slf4j.*;
import org.typemeta.funcj.util.Exceptions;

import java.io.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class ExtractorTest {
    private static final Logger logger = LoggerFactory.getLogger(ExtractorTest.class);

    private static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String JDBC_CONN_URL = "jdbc:derby:memory:testdb;create=true";

    private static class Column {
        public final String name;
        public final String type;

        private Column(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    private static final Column[] TABLE_COLUMNS = new Column[] {
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

    private static final List<Class<?>> NUMERIC_TYPES = list(Double.class, Float.class, Long.class, Integer.class, Short.class, Byte.class);

    private static final double[] NUMERIC_MAX = new double[] {
            Double.MAX_VALUE, Float.MAX_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Byte.MAX_VALUE
    };

    private static List<Class<?>> list(Class<?>... values) {
        return Arrays.asList(values);
    }

    private static final Map<String, List<Class<?>>> sqlTypeMap;

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

    private static final Map<Class<?>, ColumnExtractor<?>> extractors;

    static {
        extractors = new HashMap<>();
        extractors.put(Boolean.class, ColumnExtractors.BOOLEAN_EX);
        extractors.put(Byte.class, ColumnExtractors.BYTE_EX);
        extractors.put(Date.class, ColumnExtractors.SQLDATE_EX);
        extractors.put(Double.class, ColumnExtractors.DOUBLE_EX);
        extractors.put(Float.class, ColumnExtractors.FLOAT_EX);
        extractors.put(Integer.class, ColumnExtractors.INTEGER_EX);
        extractors.put(LocalDate.class, ColumnExtractors.LOCALDATE_EX);
        extractors.put(LocalDateTime.class, ColumnExtractors.LOCALDATETIME_EX);
        extractors.put(LocalTime.class, ColumnExtractors.LOCALTIME_EX);
        extractors.put(Long.class, ColumnExtractors.LONG_EX);
        extractors.put(Short.class, ColumnExtractors.SHORT_EX);
        extractors.put(String.class, ColumnExtractors.STRING_EX);
        extractors.put(Time.class, ColumnExtractors.SQLTIME_EX);
    }

    private static final Map<Class<?>, ColumnExtractor<?>>  optExtractors;

    static {
        optExtractors = new HashMap<>();
        optExtractors.put(Boolean.class, ColumnExtractors.OPT_BOOLEAN_EX);
        optExtractors.put(Byte.class, ColumnExtractors.OPT_BYTE_EX);
        optExtractors.put(Date.class, ColumnExtractors.OPT_SQLDATE_EX);
        optExtractors.put(Double.class, ColumnExtractors.OPT_DOUBLE_EX);
        optExtractors.put(Float.class, ColumnExtractors.OPT_FLOAT_EX);
        optExtractors.put(Integer.class, ColumnExtractors.OPT_INTEGER_EX);
        optExtractors.put(LocalDate.class, ColumnExtractors.OPT_LOCALDATE_EX);
        optExtractors.put(LocalDateTime.class, ColumnExtractors.OPT_LOCALDATETIME_EX);
        optExtractors.put(LocalTime.class, ColumnExtractors.OPT_LOCALTIME_EX);
        optExtractors.put(Long.class, ColumnExtractors.OPT_LONG_EX);
        optExtractors.put(Short.class, ColumnExtractors.OPT_SHORT_EX);
        optExtractors.put(String.class, ColumnExtractors.OPT_STRING_EX);
        optExtractors.put(Time.class, ColumnExtractors.OPT_SQLTIME_EX);
    }

    private static Connection testDbConn;

    static void loadScript(String path) throws SQLException {
        logger.info("Loading script " + path);
        Exceptions.<SQLException>unwrap(() -> SqlUtils.loadResource(path)
                .forEach(sql -> Exceptions.wrap(() -> testDbConn.createStatement().execute(sql)))
        );
    }

    @BeforeClass
    public static void setupDatabase() throws Exception {
        Class.forName(DERBY_DRIVER).newInstance();

        logger.info("Connecting to embedded database");
        testDbConn = DriverManager.getConnection(JDBC_CONN_URL);

        loadScript("/sql/create.sql");
        loadScript("/sql/data.sql");
    }

    private static Number getOptionalValue(Object optional) {
        if (optional instanceof Optional) {
            return (Number)((Optional)optional).get();
        } else if (optional instanceof OptionalInt) {
            final OptionalInt optInt = ((OptionalInt)optional);
            return optInt.isPresent() ? optInt.getAsInt() : null;
        } else if (optional instanceof OptionalDouble) {
            final OptionalDouble optDbl = ((OptionalDouble)optional);
            return optDbl.isPresent() ? optDbl.getAsDouble() : null;
        } else if (optional instanceof OptionalLong) {
            final OptionalLong optLng = ((OptionalLong)optional);
            return optLng.isPresent() ? optLng.getAsLong() : null;
        } else {
            throw new RuntimeException("Value is not an optional : " + optional);
        }
    }

    @Test
    public void testNullable() throws SQLException {
        final Double[] prevValues = new Double[TABLE_COLUMNS.length];

        for (int i = 0; i < 10; ++i) {
            final ResultSet rs = testDbConn.createStatement()
                    .executeQuery("SELECT * FROM test_null");

            while (rs.next()) {
                for (int nCol = 0; nCol < TABLE_COLUMNS.length; ++nCol) {
                    final Column column = TABLE_COLUMNS[nCol];
                    final List<Class<?>> javaTypes = sqlTypeMap.get(column.type);
                    if (i < javaTypes.size()) {
                        if (javaTypes.equals(NUMERIC_TYPES)) {
                            final Class<?> javaType = javaTypes.get(i);
                            if (prevValues[nCol] == null) {
                                final Object value = optExtractors.get(javaType).bind(column.name).extract(rs);
                                final Number num = getOptionalValue(value);
                                if (num != null) {
                                    prevValues[nCol] = num.doubleValue();
                                }
                            } else {
                                if ((prevValues[nCol] < NUMERIC_MAX[i])) {
                                    final Object value = optExtractors.get(javaType).bind(column.name).extract(rs);
                                }
                            }
                        } else {
                            final Class<?> javaType = javaTypes.get(i);
                            final Object value = optExtractors.get(javaType).bind(column.name).extract(rs);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testNonNullable() throws SQLException {
        final Double[] prevValues = new Double[TABLE_COLUMNS.length];

        for (int i = 0; i < 10; ++i) {
            final ResultSet rs = testDbConn.createStatement()
                    .executeQuery("SELECT * FROM test_notnull");

            while (rs.next()) {
                for (int nCol = 0; nCol < TABLE_COLUMNS.length; ++nCol) {
                    final Column column = TABLE_COLUMNS[nCol];
                    final List<Class<?>> javaTypes = sqlTypeMap.get(column.type);
                    if (i < javaTypes.size()) {
                        if (javaTypes.equals(NUMERIC_TYPES)) {
                            final Class<?> javaType = javaTypes.get(i);
                            if (prevValues[nCol] == null) {
                                final Number num = (Number)extractors.get(javaType).bind(column.name).extract(rs);
                                if (num != null) {
                                    prevValues[nCol] = num.doubleValue();
                                }
                            } else {
                                if ((prevValues[nCol] < NUMERIC_MAX[i])) {
                                    final Object value = extractors.get(javaType).bind(column.name).extract(rs);
                                }
                            }
                        } else {
                            final Class<?> javaType = javaTypes.get(i);
                            final Object value = extractors.get(javaType).bind(column.name).extract(rs);
                        }
                    }
                }
            }
        }
    }

    @AfterClass
    public static void shutdown() throws SQLException {
        if (testDbConn != null) {
            testDbConn.close();
        }
    }
}

class SqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(SqlUtils.class);

    static final String SQL_SEP_TOKEN = ";;";

    static Stream<String> loadResource(String path) {
        final String text =
                Optional.ofNullable(ExtractorTest.class.getResourceAsStream(path))
                        .map(InputStreamReader::new)
                        .map(is -> {
                            try (BufferedReader br = new BufferedReader(is)) {
                                return br.lines().collect(joining("\n"));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }).orElseThrow(() -> new RuntimeException("Resource '" + path + "' not found"));


        final Iterator<String> iter = new Iterator<String>() {
            int s = 0;
            int e = nextIndex();
            String next = text.substring(0, e);

            private int nextIndex() {
                int e = text.indexOf(SQL_SEP_TOKEN, s);
                return (e == -1) ? text.length() : e;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public String next() {
                final String result = next;
                s = e + SQL_SEP_TOKEN.length();
                if (s > text.length()) {
                    next = null;
                } else {
                    e = nextIndex();
                    next = (e == -1 ? text.substring(s) : text.substring(s, e)).trim();
                    if (next.isEmpty()) {
                        next = null;
                    }
                }
                return result;
            }
        };

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        iter,
                        Spliterator.ORDERED | Spliterator.NONNULL
                ), false
        );
    }
}