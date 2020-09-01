package org.typemeta.funcj.database;

import org.junit.*;
import org.slf4j.*;
import org.typemeta.funcj.extractors.Extractor;
import org.typemeta.funcj.injectors.*;
import org.typemeta.funcj.util.Exceptions;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.database.DatabaseMeta.*;

public class DatabaseInjectorTest {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInjectorTest.class);

    private static final Map<Class<?>, NumberedInjector<PreparedStatement, ?>> injectors;

    static {
        injectors = new HashMap<>();
        injectors.put(Boolean.class, DatabaseInjectors.BOOLEAN);
        injectors.put(Byte.class, DatabaseInjectors.BYTE);
        injectors.put(Date.class, DatabaseInjectors.SQLDATE);
        injectors.put(Double.class, DatabaseInjectors.DOUBLE);
        injectors.put(Float.class, DatabaseInjectors.FLOAT);
        injectors.put(Integer.class, DatabaseInjectors.INTEGER);
        injectors.put(LocalDate.class, DatabaseInjectors.LOCALDATE);
        injectors.put(LocalDateTime.class, DatabaseInjectors.LOCALDATETIME);
        injectors.put(LocalTime.class, DatabaseInjectors.LOCALTIME);
        injectors.put(Long.class, DatabaseInjectors.LONG);
        injectors.put(Short.class, DatabaseInjectors.SHORT);
        injectors.put(String.class, DatabaseInjectors.STRING);
        injectors.put(Time.class, DatabaseInjectors.SQLTIME);
    }

    private static final Map<Class<?>, NumberedInjector<PreparedStatement, ?>> optInjectors;

    static {
        optInjectors = new HashMap<>();
        optInjectors.put(Boolean.class, DatabaseInjectors.OPT_BOOLEAN);
        optInjectors.put(Byte.class, DatabaseInjectors.OPT_BYTE);
        optInjectors.put(Date.class, DatabaseInjectors.OPT_SQLDATE);
        optInjectors.put(Double.class, DatabaseInjectors.OPT_DOUBLE);
        optInjectors.put(Float.class, DatabaseInjectors.OPT_FLOAT);
        optInjectors.put(Integer.class, DatabaseInjectors.OPT_INTEGER);
        optInjectors.put(LocalDate.class, DatabaseInjectors.OPT_LOCALDATE);
        optInjectors.put(LocalDateTime.class, DatabaseInjectors.OPT_LOCALDATETIME);
        optInjectors.put(LocalTime.class, DatabaseInjectors.OPT_LOCALTIME);
        optInjectors.put(Long.class, DatabaseInjectors.OPT_LONG);
        optInjectors.put(Short.class, DatabaseInjectors.OPT_SHORT);
        optInjectors.put(String.class, DatabaseInjectors.OPT_STRING);
        optInjectors.put(Time.class, DatabaseInjectors.OPT_SQLTIME);
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
        Class.forName(DERBY_DRIVER).getDeclaredConstructor().newInstance();

        logger.info("Connecting to embedded database");
        testDbConn = DriverManager.getConnection(JDBC_CONN_URL);

        loadScript("create.sql");
    }

    @AfterClass
    public static void shutdown() throws SQLException {
        if (testDbConn != null) {
            loadScript("cleanup.sql");
        }
    }

    @Test
    public void testNullable() throws SQLException {
        roundTrip(TableType.NULLABLE, RECORD1_VALUES, RECORD1_INJECTOR, RECORD1_EXTRACTOR);
        roundTrip(TableType.NULLABLE, RECORD2_VALUES, RECORD2_INJECTOR, RECORD2_EXTRACTOR);
        roundTrip(TableType.NULLABLE, OPTRECORD1_VALUES, OPTRECORD1_INJECTOR, OPTRECORD1_EXTRACTOR);
        roundTrip(TableType.NULLABLE, OPTRECORD2_VALUES, OPTRECORD2_INJECTOR, OPTRECORD2_EXTRACTOR);
    }

    @Test
    public void testNonNullable() throws SQLException {
        roundTrip(TableType.NOTNULLABLE, RECORD1_VALUES, RECORD1_INJECTOR, RECORD1_EXTRACTOR);
        roundTrip(TableType.NOTNULLABLE, RECORD2_VALUES, RECORD2_INJECTOR, RECORD2_EXTRACTOR);
        roundTrip(TableType.NOTNULLABLE, dropFirst(OPTRECORD1_VALUES), OPTRECORD1_INJECTOR, OPTRECORD1_EXTRACTOR);
        roundTrip(TableType.NOTNULLABLE, dropFirst(OPTRECORD2_VALUES), OPTRECORD2_INJECTOR, OPTRECORD2_EXTRACTOR);
    }

    private <T> T[] dropFirst(T[] values) {
        return Arrays.copyOfRange(values, 1, values.length);
    }

    private enum TableType {
        NULLABLE {
            @Override
            String insertScript() {
                return "insert_null.sql";
            }

            @Override
            String tableName() {
                return "test_null";
            }
        },
        NOTNULLABLE {
            @Override
            String insertScript() {
                return "insert_notnull.sql";
            }

            @Override
            String tableName() {
                return "test_notnull";
            }
        };

        abstract String insertScript();
        abstract String tableName();
    }

    private <T> void roundTrip(
            TableType tableType,
            T[] values,
            Injector<PreparedStatement, T> injector,
            Extractor<ResultSet, T> extractor
    ) throws SQLException {
        try (final PreparedStatement ps = testDbConn.prepareStatement(SqlUtils.loadSingleResource(tableType.insertScript()))) {
            for(T rec : values) {
                injector.inject(ps, rec);
                ps.execute();
            }

            final ResultSet rs = testDbConn.createStatement()
                    .executeQuery("SELECT * FROM " + tableType.tableName());

            for(T rec : values) {
                rs.next();
                final T dbRec = extractor.extract(rs);
                assertEquals(rec, dbRec);
            }
        }

        testDbConn.createStatement().execute("DELETE FROM " + tableType.tableName());
    }
}

