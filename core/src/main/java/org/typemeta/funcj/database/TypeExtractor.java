package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;

import java.sql.*;

/**
 * A {@code TypeExtractor} is a function to extract a value from a {@link ResultSet},
 * given a column name.
 * @param <T>       the type of the extracted value
 */
public interface TypeExtractor<T> {
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
     * Compose this extractor with a function, by applying the function to the
     * result of this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the result of applying the function
     */
    default <U> TypeExtractor<U> map(Functions.F<T, U> f) {
        return (rs, name) -> f.apply(extract(rs, name));
    }

    /**
     * Bind this {@code TypeExtractor} to a name, giving us an {@link Extractor}.
     * @param name      the column name
     * @return          the extractor
     */
    default Extractor<T> bind(String name) {
        return rs -> extract(rs, name);
    }
}
