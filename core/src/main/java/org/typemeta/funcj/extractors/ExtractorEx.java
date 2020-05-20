package org.typemeta.funcj.extractors;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

/**
 * A function to extract a value from an environment, which may throw a checked exception.
 * @param <ENV>     the environment type
 * @param <T>       the extracted value type
 * @param <EX>      the exception type
 */
@FunctionalInterface
public interface ExtractorEx<ENV, T, EX extends Exception> {
    /**
     * Static constructor method.
     * @param extr      the extractor
     * @param <ENV>     the environment type
     * @param <T>       the extracted value type
     * @param <EX>      the exception type
     * @return          the extractor
     */
    static <ENV, T, EX extends Exception> ExtractorEx<ENV, T, EX> of(ExtractorEx<ENV, T, EX> extr) {
        return extr;
    }

    /**
     * Extract a value of type {@code T} from the given env.
     * @param env       the environment value
     * @return          the extracted value
     */
    T extract(ENV env) throws EX;

    /**
     * Map a function over this extractor.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new extractor
     */
    default <U> ExtractorEx<ENV, U, EX> map(Functions.F<T, U> f) {
        return rs -> f.apply(extract(rs));
    }

    /**
     * Flatmap a function over this extractor.
     * @param f         the function
     * @param <U>       the extractor value type returned by the function
     * @return          the flatmapped extractor
     */
    default <U> ExtractorEx<ENV, U, EX> flatMap(Functions.F<T, ExtractorEx<ENV, U, EX>> f) {
        return rs -> f.apply(this.extract(rs)).extract(rs);
    }

    /**
     * Convert this extractor to an unchecked extractor (one that doesn't throw).
     * @return          the unchecked extractor
     */
    default Extractor<ENV, T> unchecked() {
        return env -> {
            try {
                return extract(env);
            } catch (Exception ex) {
                return Exceptions.throwUnchecked(ex);
            }
        };
    }
}
