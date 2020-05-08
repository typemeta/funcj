package org.typemeta.funcj.database;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

/**
 * Simple combinator framework for extracting values from JDBC {@link ResultSet}s.
 */
public abstract class ExtractorUtils {

    static final DateTimeFormatter YYYYMMDD =
            new DateTimeFormatterBuilder()
                    .appendValue(YEAR, 4)
                    .appendValue(MONTH_OF_YEAR, 2)
                    .appendValue(DAY_OF_MONTH, 2)
                    .toFormatter();

}
