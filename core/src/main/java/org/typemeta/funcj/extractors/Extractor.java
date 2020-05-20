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
     * An extractor that always returns the same value.
     * @param t         the value
     * @param <ENV>     environment type
     * @param <T>       the extracted value type
     * @return          the extractor
     */
    static <ENV, T> Extractor<ENV, T> pure(T t) {
        return env -> t;
    }

    /**
     * Static constructor method.
     * @param extr      the extractor
     * @param <ENV>     the environment type
     * @param <T>       the extracted value type
     * @return          the extractor
     */
    static <ENV, T> Extractor<ENV, T> of(Extractor<ENV, T> extr) {
        return extr;
    }

    /**
     * Extract a value of type {@code T} from the given environment.
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
