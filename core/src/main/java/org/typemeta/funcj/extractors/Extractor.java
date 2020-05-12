package org.typemeta.funcj.extractors;

import org.typemeta.funcj.functions.Functions;

/**
 * A function to extract a value from an environment.
 * @param <ENV>     the environment type
 * @param <T>       the extracted value type
 */
@FunctionalInterface
public interface Extractor<ENV, T> {
    /**
     * Extract a value of type {@code T} from the given env.
     * @param env       the environment value
     * @return          the extracted value
     */
    T extract(ENV env);

    /**
     * Map a function over this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new extractor
     */
    default <U> Extractor<ENV, U> map(Functions.F<T, U> f) {
        return rs -> f.apply(extract(rs));
    }

    /**
     * Flatmap a function over this extractor.
     * @param f         the function
     * @param <U>       the extractor value type returned by the function
     * @return          the new extractor
     */
    default <U> Extractor<ENV, U> flatMap(Functions.F<T, Extractor<ENV, U>> f) {
        return rs -> f.apply(this.extract(rs)).extract(rs);
    }
}
