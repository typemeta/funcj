package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;

import java.sql.*;

/**
 * An {@code UnboundExtractor} is a function to extract a value from a {@link ResultSet},
 * given a column name.
 * @param <T>       the value type
 */
public interface ColumnExtractor<T> {
    /**
     * Extract a value of type {@code T} from the given {@link ResultSet},
     * for the given column name.
     * @param rs        the result set
     * @param name      the column name
     * @return          the extracted value
     * @throws SQLException in the event of an error
     */
    T extract(ResultSet rs, String name) throws SQLException;

    /**
     * Convert this extractor into another that applies a function to the result of this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new extractor
     */
    default <U> ColumnExtractor<U> map(Functions.F<T, U> f) {
        return (rs, name) -> f.apply(extract(rs, name));
    }

    /**
     * Bind this extractor to a name, giving us an {@link Extractor}.
     * @param name      the column name
     * @return          the extractor
     */
    default Extractor<T> bind(String name) {
        return rs -> extract(rs, name);
    }
}
