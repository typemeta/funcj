package org.typemeta.funcj.database;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

/**
 * Variant of {link NamedExtractor} where the extract method may throw an exception.
 * @param <ENV>     the environment type
 * @param <T>       the value type
 * @param <EX>      the exception type
 */
interface NamedExtractorEx<ENV, T, EX extends Exception> {

    /**
     * Extract a value of type {@code T} from the given environment,
     * for the given column name.
     * @param env       the environment
     * @param name      the name
     * @return          the extracted value
     */
    T extract(ENV env, String name) throws EX;

    /**
     * Convert this extractor into another that applies a function to the result of this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new extractor
     */
    default <U> NamedExtractorEx<ENV, U, EX> map(Functions.F<T, U> f) {
        return (rs, name) -> f.apply(extract(rs, name));
    }

    /**
     * Bind this extractor to a name, giving us an {@link Extractor}.
     * @param name      the column name
     * @return          the extractor
     */
    default ExtractorEx<ENV, T, EX> bind(String name) {
        return rs -> extract(rs, name);
    }

    /**
     * Convert this extractor to an unchecked extractor (one that doesn't throw).
     * @return          the unchecked extractor
     */
    default NamedExtractor<ENV, T> unchecked() {
        return (env, name) -> {
            try {
                return extract(env, name);
            } catch (Exception ex) {
                return Exceptions.throwUnchecked(ex);
            }
        };
    }
}
