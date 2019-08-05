package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;

import java.sql.*;

/**
 * An {@code Extractor} is a function to extract a value from a {@link ResultSet}.
 * Note that, unlike {@link TypeExtractor}, an {@code Extractor} already knows the column name.
 * @param <T>       the type of the extracted value
 */
public interface Extractor<T> {
    /**
     * Extract a value of type {@code T} from the given {@link ResultSet}.
     * @param rs        the result set
     * @return          the extracted value
     * @throws SQLException in the event of an error
     */
    T extract(ResultSet rs) throws SQLException;

    default <U> Extractor<U> map(Functions.F<T, U> f) {
        return rs -> f.apply(extract(rs));
    }
}
