package org.typemeta.funcj.extractors;

import org.typemeta.funcj.extractors.Extractors.*;
import java.util.function.*;

public class NamedExtractors {

    /**
     * A specialisation of {@link NamedExtractor} for {@code double} values.
     * @param <ENV>     the environment type
     */
    @FunctionalInterface
    public interface DoubleNamedExtractor<ENV> extends NamedExtractor<ENV, Double> {

        /**
         * The extraction method, specialised to return an unboxed {@code double} value.
         * @param env   the environment
         * @return      the extracted value
         */
        double extractDouble(ENV env, String name);

        @Override
        default Double extract(ENV env, String name) {
            return extractDouble(env, name);
        }

        default <U> NamedExtractor<ENV, U> mapDouble(DoubleFunction<U> f) {
            return (env, name) -> f.apply(extractDouble(env, name));
        }

        @Override
        default DoubleExtractor<ENV> bind(String name) {
            return rs -> extractDouble(rs, name);
        }
    }

    /**
     * A specialisation of {@code NamedExtractor} for {@code int} values.
     * @param <ENV>     the environment type
     */
    @FunctionalInterface
    public interface IntNamedExtractor<ENV> extends NamedExtractor<ENV, Integer> {

        /**
         * The extraction method, specialised to return an unboxed {@code int} value.
         * @param env   the environment
         * @return      the extracted value
         */
        int extractInt(ENV env, String name);

        @Override
        default Integer extract(ENV env, String name) {
            return extractInt(env, name);
        }

        default <U> NamedExtractor<ENV, U> mapInt(IntFunction<U> f) {
            return (env, name) -> f.apply(extractInt(env, name));
        }

        @Override
        default IntExtractor<ENV> bind(String name) {
            return rs -> extractInt(rs, name);
        }
    }

    /**
     * A specialisation of {@code NamedExtractor} for {@code long} values.
     * @param <ENV>     the environment type
     */
    @FunctionalInterface
    public interface LongNamedExtractor<ENV> extends NamedExtractor<ENV, Long> {

        /**
         * The extraction method, specialised to return an unboxed {@code long} value.
         * @param env   the environment
         * @return      the extracted value
         */
        long extractLong(ENV env, String name);

        @Override
        default Long extract(ENV env, String name) {
            return extractLong(env, name);
        }

        default <U> NamedExtractor<ENV, U> mapLong(LongFunction<U> f) {
            return (env, name) -> f.apply(extractLong(env, name));
        }

        @Override
        default LongExtractor<ENV> bind(String name) {
            return rs -> extractLong(rs, name);
        }
    }
}
