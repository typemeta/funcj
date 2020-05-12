package org.typemeta.funcj.extractors;

import org.typemeta.funcj.extractors.NamedExtractors.*;
import org.typemeta.funcj.util.Exceptions;

import java.util.function.*;

public class NamedExtractorExs {

    /**
     * A specialisation of {@link NamedExtractorEx} for {@code double} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    public interface DoubleNamedExtractorEx<ENV, EX extends Exception> extends NamedExtractorEx<ENV, Double, EX> {
        static <ENV, EX extends Exception> DoubleNamedExtractorEx<ENV, EX> of(DoubleNamedExtractorEx<ENV, EX> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code double} value.
         * @param env   the environment
         * @return      the extracted double value
         */
        double extractDouble(ENV env, String name) throws EX;

        @Override
        default Double extract(ENV env, String name) throws EX {
            return extractDouble(env, name);
        }

        default <U> NamedExtractorEx<ENV, U, EX> mapDouble(DoubleFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }

        /**
         * Convert this extractor to an unchecked extractor (one that doesn't throw).
         * @return          the unchecked extractor
         */
        default DoubleNamedExtractor<ENV> unchecked() {
            return (env, name) -> {
                try {
                    return extractDouble(env, name);
                } catch (Exception ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }
    }

    /**
     * A specialisation of {@link NamedExtractorEx} for {@code int} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    public interface IntNamedExtractorEx<ENV, EX extends Exception> extends NamedExtractorEx<ENV, Integer, EX> {
        static <ENV, EX extends Exception> IntNamedExtractorEx<ENV, EX> of(IntNamedExtractorEx<ENV, EX> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code int} value.
         * @param env   the environment
         * @return      the extracted value
         */
        int extractInt(ENV env, String name) throws EX;

        @Override
        default Integer extract(ENV env, String name) throws EX {
            return extractInt(env, name);
        }

        default <U> NamedExtractorEx<ENV, U, EX> mapInt(IntFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }

        /**
         * Convert this extractor to an unchecked extractor (one that doesn't throw).
         * @return          the unchecked extractor
         */
        default IntNamedExtractor<ENV> unchecked() {
            return (env, name) -> {
                try {
                    return extractInt(env, name);
                } catch (Exception ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }
    }

    /**
     * A specialisation of {@link NamedExtractorEx} for {@code long} values.
     * @param <ENV>     the environment type
     * @param <EX>      the exception type
     */
    public interface LongNamedExtractorEx<ENV, EX extends Exception> extends NamedExtractorEx<ENV, Long, EX> {
        static <ENV, EX extends Exception> LongNamedExtractorEx<ENV, EX> of(LongNamedExtractorEx<ENV, EX> extr) {
            return extr;
        }

        /**
         * The extraction method, specialised to return an unboxed {@code long} value.
         * @param env   the environment
         * @return      the extracted value
         */
        long extractLong(ENV env, String name) throws EX;

        @Override
        default Long extract(ENV env, String name) throws EX {
            return extractLong(env, name);
        }

        default <U> NamedExtractorEx<ENV, U, EX> mapLong(LongFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }

        /**
         * Convert this extractor to an unchecked extractor (one that doesn't throw).
         * @return          the unchecked extractor
         */
        default LongNamedExtractor<ENV> unchecked() {
            return (env, name) -> {
                try {
                    return extractLong(env, name);
                } catch (Exception ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }
    }
}
