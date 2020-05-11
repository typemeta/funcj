package org.typemeta.funcj.database;

import java.sql.ResultSet;
import java.time.format.*;

import static java.time.temporal.ChronoField.*;

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
