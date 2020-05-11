package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;

import java.sql.*;

/**
 * A function to extract a value from a {@link ResultSet}.
 * @param <T>       the extracted value type
 */
public interface Extractor<T> {
    /**
     * Extract a value of type {@code T} from the given {@link ResultSet}.
     * @param rs        the {@code ResultSet} object
     * @return          the extracted value
     * @throws SQLException if a database error occurs
     */
    T extract(ResultSet rs) throws SQLException;

    /**
     * Map a function over this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new extractor
     */
    default <U> Extractor<U> map(Functions.F<T, U> f) {
        return rs -> f.apply(extract(rs));
    }

    /**
     * Flatmap a function over this extractor.
     * @param f         the function
     * @param <U>       the extractor value type returned by the function
     * @return          the flatmapped extractor
     */
    default <U> Extractor<U> flatMap(Functions.F<T, Extractor<U>> f) {
        return rs -> f.apply(this.extract(rs)).extract(rs);
    }
}
