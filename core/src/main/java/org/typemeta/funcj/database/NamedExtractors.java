package org.typemeta.funcj.database;

import java.util.function.*;

public class NamedExtractors {

    interface DoubleNamedExtractor<ENV> extends NamedExtractor<ENV, Double> {
        double extractDouble(ENV env, String name);

        @Override
        default Double extract(ENV env, String name) {
            return extractDouble(env, name);
        }

        default <U> NamedExtractor<ENV, U> mapDouble(DoubleFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }
    }

    interface IntNamedExtractor<ENV> extends NamedExtractor<ENV, Integer> {
        int extractInt(ENV env, String name);

        @Override
        default Integer extract(ENV env, String name) {
            return extractInt(env, name);
        }

        default <U> NamedExtractor<ENV, U> mapInt(IntFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }
    }

    interface LongNamedExtractor<ENV> extends NamedExtractor<ENV, Long> {
        long extractLong(ENV env, String name);

        @Override
        default Long extract(ENV env, String name) {
            return extractLong(env, name);
        }

        default <U> NamedExtractor<ENV, U> mapLong(LongFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }
    }
}
