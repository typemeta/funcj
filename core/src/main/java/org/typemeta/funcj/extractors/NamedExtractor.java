package org.typemeta.funcj.extractors;

import org.typemeta.funcj.functions.*;
import org.typemeta.funcj.util.Exceptions;

/**
 * A function to extract a value from an environment, given a name.
 * @param <ENV>     the environment type
 * @param <T>       the value type
 */
@FunctionalInterface
public interface NamedExtractor<ENV, T> {
    static <ENV, T> NamedExtractor<ENV, T> of(FunctionsEx.F2<ENV, String, T> thrower) {
        return (env, name) -> {
            try {
                return thrower.apply(env, name);
            } catch(Exception ex) {
                return Exceptions.throwUnchecked(ex);
            }
        };
    }

    /**
     * Extract a value of type {@code T} from the given environment,
     * for the given column name.
     * @param env       the environment
     * @param name      the name
     * @return          the extracted value
     */
    T extract(ENV env, String name);

    /**
     * Convert this extractor into another that applies a function to the result of this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new extractor
     */
    default <U> NamedExtractor<ENV, U> map(Functions.F<T, U> f) {
        return (rs, name) -> f.apply(extract(rs, name));
    }

    /**
     * Bind this extractor to a name, giving us an {@link Extractor}.
     * @param name      the column name
     * @return          the extractor
     */
    default Extractor<ENV, T> bind(String name) {
        return rs -> extract(rs, name);
    }
}
