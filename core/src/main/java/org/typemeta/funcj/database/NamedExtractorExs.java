package org.typemeta.funcj.database;

import java.util.function.*;

public class NamedExtractorExs {

    interface DoubleNamedExtractorEx<ENV, EX extends Exception> extends NamedExtractorEx<ENV, Double, EX> {
        double extractDouble(ENV env, String name) throws EX;

        @Override
        default Double extract(ENV env, String name) throws EX {
            return extractDouble(env, name);
        }

        default <U> NamedExtractorEx<ENV, U, EX> mapDouble(DoubleFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }
    }

    interface IntNamedExtractorEx<ENV, EX extends Exception> extends NamedExtractorEx<ENV, Integer, EX> {
        int extractInt(ENV env, String name) throws EX;

        @Override
        default Integer extract(ENV env, String name) throws EX {
            return extractInt(env, name);
        }

        default <U> NamedExtractorEx<ENV, U, EX> mapInt(IntFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }
    }

    interface LongNamedExtractorEx<ENV, EX extends Exception> extends NamedExtractorEx<ENV, Long, EX> {
        long extractLong(ENV env, String name) throws EX;

        @Override
        default Long extract(ENV env, String name) throws EX {
            return extractLong(env, name);
        }

        default <U> NamedExtractorEx<ENV, U, EX> mapLong(LongFunction<U> f) {
            return (env, name) -> f.apply(extract(env, name));
        }
    }
}
