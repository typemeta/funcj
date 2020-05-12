package org.typemeta.funcj.extractors;

import org.typemeta.funcj.extractors.Extractors.*;
import org.typemeta.funcj.extractors.NamedExtractorExs.*;
import org.typemeta.funcj.util.Exceptions;

import java.sql.*;
import java.util.function.*;

public class NamedExtractors {

    @FunctionalInterface
    public interface DoubleNamedExtractor<ENV> extends NamedExtractor<ENV, Double> {

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

    @FunctionalInterface
    public interface IntNamedExtractor<ENV> extends NamedExtractor<ENV, Integer> {
        static <ENV> IntNamedExtractor<ENV> of(IntNamedExtractorEx<ENV, SQLException> extr) {
            return (rs, name) -> {
                try {
                    return extr.extractInt(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }

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

    @FunctionalInterface
    public interface LongNamedExtractor<ENV> extends NamedExtractor<ENV, Long> {
        static <ENV> LongNamedExtractor<ENV> of(LongNamedExtractorEx<ENV, SQLException> extr) {
            return (rs, name) -> {
                try {
                    return extr.extractLong(rs, name);
                } catch (SQLException ex) {
                    return Exceptions.throwUnchecked(ex);
                }
            };
        }

        long extractLong(ENV env, String name);

        @Override
        default Long extract(ENV env, String name) {
            return extractLong(env, name);
        }

        default <U> NamedExtractor<ENV, U> map(LongFunction<U> f) {
            return (env, name) -> f.apply(extractLong(env, name));
        }

        @Override
        default LongExtractor<ENV> bind(String name) {
            return rs -> extractLong(rs, name);
        }
    }
}
