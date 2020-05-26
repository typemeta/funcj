package org.typemeta.funcj.database;

import org.junit.*;
import org.slf4j.*;
import org.typemeta.funcj.extractors.*;
import org.typemeta.funcj.util.Exceptions;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;

import static org.typemeta.funcj.database.DatabaseMeta.*;

public class DatabaseExtractorTest {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExtractorTest.class);

    private static final Map<Class<?>, NamedExtractor<ResultSet, ?>> extractors;

    static {
        extractors = new HashMap<>();
        extractors.put(Boolean.class, DatabaseExtractors.BOOLEAN);
        extractors.put(Byte.class, DatabaseExtractors.BYTE);
        extractors.put(Date.class, DatabaseExtractors.SQLDATE);
        extractors.put(Double.class, DatabaseExtractors.DOUBLE);
        extractors.put(Float.class, DatabaseExtractors.FLOAT);
        extractors.put(Integer.class, DatabaseExtractors.INTEGER);
        extractors.put(LocalDate.class, DatabaseExtractors.LOCALDATE);
        extractors.put(LocalDateTime.class, DatabaseExtractors.LOCALDATETIME);
        extractors.put(LocalTime.class, DatabaseExtractors.LOCALTIME);
        extractors.put(Long.class, DatabaseExtractors.LONG);
        extractors.put(Short.class, DatabaseExtractors.SHORT);
        extractors.put(String.class, DatabaseExtractors.STRING);
        extractors.put(Time.class, DatabaseExtractors.SQLTIME);
    }

    private static final Map<Class<?>, NamedExtractor<ResultSet, ?>>  optExtractors;

    static {
        optExtractors = new HashMap<>();
        optExtractors.put(Boolean.class, DatabaseExtractors.OPT_BOOLEAN);
        optExtractors.put(Byte.class, DatabaseExtractors.OPT_BYTE);
        optExtractors.put(Date.class, DatabaseExtractors.OPT_SQLDATE);
        optExtractors.put(Double.class, DatabaseExtractors.OPT_DOUBLE);
        optExtractors.put(Float.class, DatabaseExtractors.OPT_FLOAT);
        optExtractors.put(Integer.class, DatabaseExtractors.OPT_INTEGER);
        optExtractors.put(LocalDate.class, DatabaseExtractors.OPT_LOCALDATE);
        optExtractors.put(LocalDateTime.class, DatabaseExtractors.OPT_LOCALDATETIME);
        optExtractors.put(LocalTime.class, DatabaseExtractors.OPT_LOCALTIME);
        optExtractors.put(Long.class, DatabaseExtractors.OPT_LONG);
        optExtractors.put(Short.class, DatabaseExtractors.OPT_SHORT);
        optExtractors.put(String.class, DatabaseExtractors.OPT_STRING);
        optExtractors.put(Time.class, DatabaseExtractors.OPT_SQLTIME);
    }

    private static Connection testDbConn;

    static void loadScript(String path) throws SQLException {
        logger.info("Loading script " + path);
        Exceptions.<SQLException>unwrap(() -> SqlUtils.loadMultiResource(path)
                .forEach(sql -> Exceptions.wrap(() -> testDbConn.createStatement().execute(sql)))
        );
    }

    @BeforeClass
    public static void setupDatabase() throws Exception {
        Class.forName(DERBY_DRIVER).newInstance();

        logger.info("Connecting to embedded database");
        testDbConn = DriverManager.getConnection(JDBC_CONN_URL);

        loadScript("create.sql");
        loadScript("data.sql");
    }

    @AfterClass
    public static void shutdown() throws SQLException {
        if (testDbConn != null) {
            loadScript("cleanup.sql");
        }
    }

    private static Number getOptionalValue(Object optional) {
        if (optional instanceof Optional) {
            return (Number)((Optional<?>)optional).orElse(null);
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
                        final Class<?> javaType = javaTypes.get(i);
                        if (javaTypes.equals(NUMERIC_TYPES)) {
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
                            NamedExtractor<ResultSet, ?> a = optExtractors.get(javaType);
                            Extractor<ResultSet, ?> b = a.bind(column.name);
                            b.extract(rs);
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
                        final Class<?> javaType = javaTypes.get(i);
                        if (javaTypes.equals(NUMERIC_TYPES)) {
                            if (prevValues[nCol] == null) {
                                final Number num = (Number)extractors.get(javaType).bind(column.name).extract(rs);
                                if (num != null) {
                                    prevValues[nCol] = num.doubleValue();
                                }
                            } else {
                                if (prevValues[nCol] < NUMERIC_MAX[i]) {
                                    final Object value = extractors.get(javaType).bind(column.name).extract(rs);
                                }
                            }
                        } else {
                            final Object value = extractors.get(javaType).bind(column.name).extract(rs);
                        }
                    }
                }
            }
        }
    }
}

