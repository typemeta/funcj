package org.typemeta.funcj.database;

import org.typemeta.funcj.extractors.*;
import org.typemeta.funcj.injectors.*;

import java.sql.Date;
import java.sql.*;
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

    static <T> T[] nullify(T[] values) {
        return (T[])Arrays.stream(values).map(t -> null).toArray(Object[]::new);
    }

    private static boolean optFloatEquals(Optional<Float> lhs, Optional<Float> rhs) {
        if (lhs.isPresent()) {
            if (rhs.isPresent()) {
                return Math.abs(lhs.get() - rhs.get()) < 0.01;
            } else {
                return false;
            }
        } else {
            return !rhs.isPresent();
        }
    }

    private static boolean optDoubleEquals(OptionalDouble lhs, OptionalDouble rhs) {
        if (lhs.isPresent()) {
            if (rhs.isPresent()) {
                return Math.abs(lhs.getAsDouble() - rhs.getAsDouble()) < 0.01;
            } else {
                return false;
            }
        } else {
            return !rhs.isPresent();
        }
    }

    public static class Record1 {
        public final boolean boolF;
        public final Date dateF;
        public final double decimalF;
        public final double doubleF;
        public final float floatF;
        public final int intF;
        public final String varcharF;
        public final Time timeF;
        public final Timestamp timestampF;

        Record1(
                boolean boolF,
                Date dateF,
                double decimalF,
                double doubleF,
                float floatF,
                int intF,
                String varcharF,
                Time timeF,
                Timestamp timestampF
        ) {
            this.boolF = boolF;
            this.dateF = dateF;
            this.decimalF = decimalF;
            this.doubleF = doubleF;
            this.floatF = floatF;
            this.intF = intF;
            this.varcharF = varcharF;
            this.timeF = timeF;
            this.timestampF = timestampF;
        }

        public boolean getBool() {
            return boolF;
        }

        public Date getDate() {
            return dateF;
        }

        public double getDecimal() {
            return decimalF;
        }

        public double getDouble() {
            return doubleF;
        }

        public float getFloat() {
            return floatF;
        }

        public int getInt() {
            return intF;
        }

        public String getVarchar() {
            return varcharF;
        }

        public Time getTime() {
            return timeF;
        }

        public Timestamp getTimestamp() {
            return timestampF;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Record1 record1 = (Record1) o;
            return boolF == record1.boolF &&
                    Double.compare(record1.decimalF, decimalF) == 0 &&
                    Double.compare(record1.doubleF, doubleF) == 0 &&
                    Float.compare(record1.floatF, floatF) == 0 &&
                    intF == record1.intF &&
                    dateF.equals(record1.dateF) &&
                    varcharF.equals(record1.varcharF) &&
                    timeF.equals(record1.timeF) &&
                    timestampF.equals(record1.timestampF);
        }

        @Override
        public int hashCode() {
            return Objects.hash(boolF, dateF, decimalF, doubleF, floatF, intF, varcharF, timeF, timestampF);
        }

        @Override
        public String toString() {
            return "Record1{" +
                    "boolF=" + boolF +
                    ", dateF=" + dateF +
                    ", decimalF=" + decimalF +
                    ", doubleF=" + doubleF +
                    ", floatF=" + floatF +
                    ", intF=" + intF +
                    ", varcharF='" + varcharF + '\'' +
                    ", timeF=" + timeF +
                    ", timestampF=" + timestampF +
                    '}';
        }
    }

    public static final Record1[] RECORD1_VALUES = new Record1[] {
            new Record1(
                    true,
                    new Date(2012, 3, 4),
                    123.45,
                    -123.456,
                    1234.5678f,
                    123456,
                    " AaBbCcDd1234!@£$ ",
                    new Time(12, 34, 56),
                    new Timestamp(2012, 3, 4, 5, 6, 7, 89)
            )
    };

    public static final Injector<PreparedStatement, Record1> RECORD1_INJECTOR =
            Injectors.combine(
                    DatabaseInjectors.BOOLEAN.bind(1).premap(Record1::getBool),
                    DatabaseInjectors.SQLDATE.bind(2).premap(Record1::getDate),
                    DatabaseInjectors.DOUBLE.bind(3).premap(Record1::getDecimal),
                    DatabaseInjectors.DOUBLE.bind(4).premap(Record1::getDouble),
                    DatabaseInjectors.FLOAT.bind(5).premap(Record1::getFloat),
                    DatabaseInjectors.INTEGER.bind(6).premap(Record1::getInt),
                    DatabaseInjectors.STRING.bind(7).premap(Record1::getVarchar),
                    DatabaseInjectors.SQLTIME.bind(8).premap(Record1::getTime),
                    DatabaseInjectors.SQLTIMESTAMP.bind(9).premap(Record1::getTimestamp)
            );
    
    public static final Extractor<ResultSet, Record1> RECORD1_EXTRACTOR =
            Extractors.combine(
                    (Object[] args) -> new Record1(
                            (Boolean)args[0],
                            (Date)args[1],
                            (Double)args[2],
                            (Double)args[3],
                            (Float)args[4],
                            (Integer)args[5],
                            (String)args[6],
                            (Time)args[7],
                            (Timestamp)args[8]
                    ),
                    DatabaseExtractors.BOOLEAN.bind(TABLE_COLUMNS[0].name),
                    DatabaseExtractors.SQLDATE.bind(TABLE_COLUMNS[1].name),
                    DatabaseExtractors.DOUBLE.bind(TABLE_COLUMNS[2].name),
                    DatabaseExtractors.DOUBLE.bind(TABLE_COLUMNS[3].name),
                    DatabaseExtractors.FLOAT.bind(TABLE_COLUMNS[4].name),
                    DatabaseExtractors.INTEGER.bind(TABLE_COLUMNS[5].name),
                    DatabaseExtractors.STRING.bind(TABLE_COLUMNS[6].name),
                    DatabaseExtractors.SQLTIME.bind(TABLE_COLUMNS[7].name),
                    DatabaseExtractors.SQLTIMESTAMP.bind(TABLE_COLUMNS[8].name)
            );

    public static class Record2 {
        public final Boolean boolF;
        public final LocalDate dateF;
        public final float decimalF;
        public final Double doubleF;
        public final Float floatF;
        public final Integer intF;
        public final String varcharF;
        public final LocalTime timeF;
        public final LocalDateTime timestampF;

        Record2(
                Boolean boolF,
                LocalDate dateF,
                float decimalF,
                Double doubleF,
                Float floatF,
                Integer intF,
                String varcharF,
                LocalTime timeF,
                LocalDateTime timestampF
        ) {
            this.boolF = boolF;
            this.dateF = dateF;
            this.decimalF = decimalF;
            this.doubleF = doubleF;
            this.floatF = floatF;
            this.intF = intF;
            this.varcharF = varcharF;
            this.timeF = timeF;
            this.timestampF = timestampF;
        }

        public Boolean getBool() {
            return boolF;
        }

        public LocalDate getDate() {
            return dateF;
        }

        public float getDecimal() {
            return decimalF;
        }

        public Double getDouble() {
            return doubleF;
        }

        public Float getFloat() {
            return floatF;
        }

        public Integer getInt() {
            return intF;
        }

        public String getVarchar() {
            return varcharF;
        }

        public LocalTime getTime() {
            return timeF;
        }

        public LocalDateTime getTimestamp() {
            return timestampF;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Record2 record2 = (Record2) o;
            return Math.abs(record2.decimalF - decimalF) < 0.01 &&
                    boolF.equals(record2.boolF) &&
                    dateF.equals(record2.dateF) &&
                    doubleF.equals(record2.doubleF) &&
                    floatF.equals(record2.floatF) &&
                    intF.equals(record2.intF) &&
                    varcharF.equals(record2.varcharF) &&
                    timeF.equals(record2.timeF) &&
                    timestampF.equals(record2.timestampF);
        }

        @Override
        public int hashCode() {
            return Objects.hash(boolF, dateF, decimalF, doubleF, floatF, intF, varcharF, timeF, timestampF);
        }

        @Override
        public String toString() {
            return "Record2{" +
                    "boolF=" + boolF +
                    ", dateF=" + dateF +
                    ", decimalF=" + decimalF +
                    ", doubleF=" + doubleF +
                    ", floatF=" + floatF +
                    ", intF=" + intF +
                    ", varcharF='" + varcharF + '\'' +
                    ", timeF=" + timeF +
                    ", timestampF=" + timestampF +
                    '}';
        }
    }

    public static final Record2[] RECORD2_VALUES = new Record2[] {
            new Record2(
                    true,
                    LocalDate.of(2012, 3, 4),
                    223.45f,
                    -223.456,
                    2234.5678f,
                    223456,
                    " AaBbCcDd1234!@£$ ",
                    LocalTime.of(12, 34, 56),
                    LocalDateTime.of(2012, 3, 4, 5, 6, 7, 89)
            )
    };

    public static final Injector<PreparedStatement, Record2> RECORD2_INJECTOR =
            Injectors.combine(
                    DatabaseInjectors.BOOLEAN.bind(1).premap(Record2::getBool),
                    DatabaseInjectors.LOCALDATE.bind(2).premap(Record2::getDate),
                    DatabaseInjectors.FLOAT.bind(3).premap(Record2::getDecimal),
                    DatabaseInjectors.DOUBLE.bind(4).premap(Record2::getDouble),
                    DatabaseInjectors.FLOAT.bind(5).premap(Record2::getFloat),
                    DatabaseInjectors.INTEGER.bind(6).premap(Record2::getInt),
                    DatabaseInjectors.STRING.bind(7).premap(Record2::getVarchar),
                    DatabaseInjectors.LOCALTIME.bind(8).premap(Record2::getTime),
                    DatabaseInjectors.LOCALDATETIME.bind(9).premap(Record2::getTimestamp)
            );

    public static final Extractor<ResultSet, Record2> RECORD2_EXTRACTOR =
            Extractors.combine(
                    (Object[] args) -> new Record2(
                            (Boolean)args[0],
                            (LocalDate)args[1],
                            (Float)args[2],
                            (Double)args[3],
                            (Float)args[4],
                            (Integer)args[5],
                            (String)args[6],
                            (LocalTime)args[7],
                            (LocalDateTime)args[8]
                    ),
                    DatabaseExtractors.BOOLEAN.bind(TABLE_COLUMNS[0].name),
                    DatabaseExtractors.LOCALDATE.bind(TABLE_COLUMNS[1].name),
                    DatabaseExtractors.FLOAT.bind(TABLE_COLUMNS[2].name),
                    DatabaseExtractors.DOUBLE.bind(TABLE_COLUMNS[3].name),
                    DatabaseExtractors.FLOAT.bind(TABLE_COLUMNS[4].name),
                    DatabaseExtractors.INTEGER.bind(TABLE_COLUMNS[5].name),
                    DatabaseExtractors.STRING.bind(TABLE_COLUMNS[6].name),
                    DatabaseExtractors.LOCALTIME.bind(TABLE_COLUMNS[7].name),
                    DatabaseExtractors.LOCALDATETIME.bind(TABLE_COLUMNS[8].name)
            );

    public static class OptRecord1 {
        public final Optional<Boolean> boolF;
        public final Optional<Date> dateF;
        public final OptionalDouble decimalF;
        public final OptionalDouble doubleF;
        public final Optional<Float> floatF;
        public final OptionalInt intF;
        public final Optional<String> varcharF;
        public final Optional<Time> timeF;
        public final Optional<Timestamp> timestampF;

        OptRecord1(
                Optional<Boolean> boolF,
                Optional<Date> dateF,
                OptionalDouble decimalF,
                OptionalDouble doubleF,
                Optional<Float> floatF,
                OptionalInt intF,
                Optional<String> varcharF,
                Optional<Time> timeF,
                Optional<Timestamp> timestampF
        ) {
            this.boolF = boolF;
            this.dateF = dateF;
            this.decimalF = decimalF;
            this.doubleF = doubleF;
            this.floatF = floatF;
            this.intF = intF;
            this.varcharF = varcharF;
            this.timeF = timeF;
            this.timestampF = timestampF;
        }

        public Optional<Boolean> getBool() {
            return boolF;
        }

        public Optional<Date> getDate() {
            return dateF;
        }

        public OptionalDouble getDecimal() {
            return decimalF;
        }

        public OptionalDouble getDouble() {
            return doubleF;
        }

        public Optional<Float> getFloat() {
            return floatF;
        }

        public OptionalInt getInt() {
            return intF;
        }

        public Optional<String> getVarchar() {
            return varcharF;
        }

        public Optional<Time> getTime() {
            return timeF;
        }

        public Optional<Timestamp> getTimestamp() {
            return timestampF;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OptRecord1 that = (OptRecord1) o;
            return boolF.equals(that.boolF) &&
                    dateF.equals(that.dateF) &&
                    optDoubleEquals(decimalF, that.decimalF) &&
                    doubleF.equals(that.doubleF) &&
                    floatF.equals(that.floatF) &&
                    intF.equals(that.intF) &&
                    varcharF.equals(that.varcharF) &&
                    timeF.equals(that.timeF) &&
                    timestampF.equals(that.timestampF);
        }

        @Override
        public int hashCode() {
            return Objects.hash(boolF, dateF, decimalF, doubleF, floatF, intF, varcharF, timeF, timestampF);
        }

        @Override
        public String toString() {
            return "OptRecord1{" +
                    "boolF=" + boolF +
                    ", dateF=" + dateF +
                    ", decimalF=" + decimalF +
                    ", doubleF=" + doubleF +
                    ", floatF=" + floatF +
                    ", intF=" + intF +
                    ", varcharF=" + varcharF +
                    ", timeF=" + timeF +
                    ", timestampF=" + timestampF +
                    '}';
        }
    }

    public static final OptRecord1[] OPTRECORD1_VALUES = new OptRecord1[] {
            new OptRecord1(
                    Optional.empty(),
                    Optional.empty(),
                    OptionalDouble.empty(),
                    OptionalDouble.empty(),
                    Optional.empty(),
                    OptionalInt.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            ),
            new OptRecord1(
                    Optional.of(true),
                    Optional.of(new Date(2012, 3, 4)),
                    OptionalDouble.of(323.45f),
                    OptionalDouble.of(-323.456),
                    Optional.of(3234.5678f),
                    OptionalInt.of(323456),
                    Optional.of(" AaBbCcDd1234!@£$ "),
                    Optional.of(new Time(12, 34, 56)),
                    Optional.of(new Timestamp(2012, 3, 4, 5, 6, 7, 89))
            )
    };

    public static final Injector<PreparedStatement, OptRecord1> OPTRECORD1_INJECTOR =
            Injectors.combine(
                    DatabaseInjectors.OPT_BOOLEAN.bind(1).premap(OptRecord1::getBool),
                    DatabaseInjectors.OPT_SQLDATE.bind(2).premap(OptRecord1::getDate),
                    DatabaseInjectors.OPT_DOUBLE.bind(3).premap(OptRecord1::getDecimal),
                    DatabaseInjectors.OPT_DOUBLE.bind(4).premap(OptRecord1::getDouble),
                    DatabaseInjectors.OPT_FLOAT.bind(5).premap(OptRecord1::getFloat),
                    DatabaseInjectors.OPT_INTEGER.bind(6).premap(OptRecord1::getInt),
                    DatabaseInjectors.OPT_STRING.bind(7).premap(OptRecord1::getVarchar),
                    DatabaseInjectors.OPT_SQLTIME.bind(8).premap(OptRecord1::getTime),
                    DatabaseInjectors.OPT_SQLTIMESTAMP.bind(9).premap(OptRecord1::getTimestamp)
            );

    public static final Extractor<ResultSet, OptRecord1> OPTRECORD1_EXTRACTOR =
            Extractors.combine(
                    (Object[] args) -> new OptRecord1(
                            (Optional<Boolean>)args[0],
                            (Optional<Date>)args[1],
                            (OptionalDouble)args[2],
                            (OptionalDouble)args[3],
                            (Optional<Float>)args[4],
                            (OptionalInt)args[5],
                            (Optional<String>)args[6],
                            (Optional<Time>)args[7],
                            (Optional<Timestamp>)args[8]
                    ),
                    DatabaseExtractors.OPT_BOOLEAN.bind(TABLE_COLUMNS[0].name),
                    DatabaseExtractors.OPT_SQLDATE.bind(TABLE_COLUMNS[1].name),
                    DatabaseExtractors.OPT_DOUBLE.bind(TABLE_COLUMNS[2].name),
                    DatabaseExtractors.OPT_DOUBLE.bind(TABLE_COLUMNS[3].name),
                    DatabaseExtractors.OPT_FLOAT.bind(TABLE_COLUMNS[4].name),
                    DatabaseExtractors.OPT_INTEGER.bind(TABLE_COLUMNS[5].name),
                    DatabaseExtractors.OPT_STRING.bind(TABLE_COLUMNS[6].name),
                    DatabaseExtractors.OPT_SQLTIME.bind(TABLE_COLUMNS[7].name),
                    DatabaseExtractors.OPT_SQLTIMESTAMP.bind(TABLE_COLUMNS[8].name)
            );

    public static class OptRecord2 {
        public final Optional<Boolean> boolF;
        public final Optional<LocalDate> dateF;
        public final Optional<Float> decimalF;
        public final OptionalDouble doubleF;
        public final Optional<Float> floatF;
        public final OptionalInt intF;
        public final Optional<String> varcharF;
        public final Optional<LocalTime> timeF;
        public final Optional<LocalDateTime> timestampF;

        OptRecord2(
                Optional<Boolean> boolF,
                Optional<LocalDate> dateF,
                Optional<Float> decimalF,
                OptionalDouble doubleF,
                Optional<Float> floatF,
                OptionalInt intF,
                Optional<String> varcharF,
                Optional<LocalTime> timeF,
                Optional<LocalDateTime> timestampF
        ) {
            this.boolF = boolF;
            this.dateF = dateF;
            this.decimalF = decimalF;
            this.doubleF = doubleF;
            this.floatF = floatF;
            this.intF = intF;
            this.varcharF = varcharF;
            this.timeF = timeF;
            this.timestampF = timestampF;
        }

        public Optional<Boolean> getBool() {
            return boolF;
        }

        public Optional<LocalDate> getDate() {
            return dateF;
        }

        public Optional<Float> getDecimal() {
            return decimalF;
        }

        public OptionalDouble getDouble() {
            return doubleF;
        }

        public Optional<Float> getFloat() {
            return floatF;
        }

        public OptionalInt getInt() {
            return intF;
        }

        public Optional<String> getVarchar() {
            return varcharF;
        }

        public Optional<LocalTime> getTime() {
            return timeF;
        }

        public Optional<LocalDateTime> getTimestamp() {
            return timestampF;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OptRecord2 that = (OptRecord2) o;
            return boolF.equals(that.boolF) &&
                    dateF.equals(that.dateF) &&
                    optFloatEquals(decimalF, that.decimalF) &&
                    doubleF.equals(that.doubleF) &&
                    floatF.equals(that.floatF) &&
                    intF.equals(that.intF) &&
                    varcharF.equals(that.varcharF) &&
                    timeF.equals(that.timeF) &&
                    timestampF.equals(that.timestampF);
        }

        @Override
        public int hashCode() {
            return Objects.hash(boolF, dateF, decimalF, doubleF, floatF, intF, varcharF, timeF, timestampF);
        }

        @Override
        public String toString() {
            return "OptRecord2{" +
                    "boolF=" + boolF +
                    ", dateF=" + dateF +
                    ", decimalF=" + decimalF +
                    ", doubleF=" + doubleF +
                    ", floatF=" + floatF +
                    ", intF=" + intF +
                    ", varcharF=" + varcharF +
                    ", timeF=" + timeF +
                    ", timestampF=" + timestampF +
                    '}';
        }
    }

    public static final OptRecord2[] OPTRECORD2_VALUES = new OptRecord2[] {
            new OptRecord2(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    OptionalDouble.empty(),
                    Optional.empty(),
                    OptionalInt.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            ),
            new OptRecord2(
                    Optional.of(true),
                    Optional.of(LocalDate.of(2012, 3, 4)),
                    Optional.of(423.45f),
                    OptionalDouble.of(-423.456),
                    Optional.of(4234.5678f),
                    OptionalInt.of(423456),
                    Optional.of(" AaBbCcDd1234!@£$ "),
                    Optional.of(LocalTime.of(12, 34, 56)),
                    Optional.of(LocalDateTime.of(2012, 3, 4, 5, 6, 7, 89))
            )
    };

    public static final Injector<PreparedStatement, OptRecord2> OPTRECORD2_INJECTOR =
            Injectors.combine(
                    DatabaseInjectors.OPT_BOOLEAN.bind(1).premap(OptRecord2::getBool),
                    DatabaseInjectors.OPT_LOCALDATE.bind(2).premap(OptRecord2::getDate),
                    DatabaseInjectors.OPT_FLOAT.bind(3).premap(OptRecord2::getDecimal),
                    DatabaseInjectors.OPT_DOUBLE.bind(4).premap(OptRecord2::getDouble),
                    DatabaseInjectors.OPT_FLOAT.bind(5).premap(OptRecord2::getFloat),
                    DatabaseInjectors.OPT_INTEGER.bind(6).premap(OptRecord2::getInt),
                    DatabaseInjectors.OPT_STRING.bind(7).premap(OptRecord2::getVarchar),
                    DatabaseInjectors.OPT_LOCALTIME.bind(8).premap(OptRecord2::getTime),
                    DatabaseInjectors.OPT_LOCALDATETIME.bind(9).premap(OptRecord2::getTimestamp)
            );

    public static final Extractor<ResultSet, OptRecord2> OPTRECORD2_EXTRACTOR =
            Extractors.combine(
                    (Object[] args) -> new OptRecord2(
                            (Optional<Boolean>)args[0],
                            (Optional<LocalDate>)args[1],
                            (Optional<Float>)args[2],
                            (OptionalDouble)args[3],
                            (Optional<Float>)args[4],
                            (OptionalInt)args[5],
                            (Optional<String>)args[6],
                            (Optional<LocalTime>)args[7],
                            (Optional<LocalDateTime>)args[8]
                    ),
                    DatabaseExtractors.OPT_BOOLEAN.bind(TABLE_COLUMNS[0].name),
                    DatabaseExtractors.OPT_LOCALDATE.bind(TABLE_COLUMNS[1].name),
                    DatabaseExtractors.OPT_FLOAT.bind(TABLE_COLUMNS[2].name),
                    DatabaseExtractors.OPT_DOUBLE.bind(TABLE_COLUMNS[3].name),
                    DatabaseExtractors.OPT_FLOAT.bind(TABLE_COLUMNS[4].name),
                    DatabaseExtractors.OPT_INTEGER.bind(TABLE_COLUMNS[5].name),
                    DatabaseExtractors.OPT_STRING.bind(TABLE_COLUMNS[6].name),
                    DatabaseExtractors.OPT_LOCALTIME.bind(TABLE_COLUMNS[7].name),
                    DatabaseExtractors.OPT_LOCALDATETIME.bind(TABLE_COLUMNS[8].name)
            );

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
