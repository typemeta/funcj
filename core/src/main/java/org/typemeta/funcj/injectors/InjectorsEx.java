package org.typemeta.funcj.injectors;

import java.util.Optional;

public abstract class InjectorsEx {
    @FunctionalInterface
    public interface DoubleInjectorEx<ENV, EX extends Exception> extends InjectorEx<ENV, Double, EX> {
        static <ENV, EX extends Exception> DoubleInjectorEx<ENV, EX> of(DoubleInjectorEx<ENV, EX> injr) {
            return injr;
        }

        ENV inject(ENV env, double value) throws EX;

        @Override
        default ENV inject(ENV env, Double value) throws EX {
            return inject(env, value.doubleValue());
        }
    }

    @FunctionalInterface
    public interface IntInjectorEx<ENV, EX extends Exception> extends InjectorEx<ENV, Integer, EX> {
        static <ENV, EX extends Exception> IntInjectorEx<ENV, EX> of(IntInjectorEx<ENV, EX> injr) {
            return injr;
        }

        ENV inject(ENV env, int value) throws EX;

        @Override
        default ENV inject(ENV env, Integer value) throws EX {
            return inject(env, value.intValue());
        }
    }

    @FunctionalInterface
    public interface LongInjectorEx<ENV, EX extends Exception> extends InjectorEx<ENV, Long, EX> {
        static <ENV, EX extends Exception> LongInjectorEx<ENV, EX> of(LongInjectorEx<ENV, EX> injr) {
            return injr;
        }

        ENV inject(ENV env, long value) throws EX;

        @Override
        default ENV inject(ENV env, Long value) throws EX {
            return inject(env, value.longValue());
        }
    }

    public static <ENV, T, EX extends Exception> InjectorEx<ENV, Optional<T>, EX> optional(InjectorEx<ENV, T, EX> injr) {
        return (env, optVal) ->
                optVal.isPresent() ? injr.inject(env, optVal.get()) : env;
    }

    /**
     * Combinator function for building an injector from an array of injectors.
     * @param exs       the array of the extractors
     * @param <ENV>     the environment type
     * @param <T>       the injector value type
     * @return          the new injector
     */
    public static <ENV, T, EX extends Exception> InjectorEx<ENV, T, EX> combine(
            InjectorEx<ENV, T, EX> ... exs
    ) {
        return (env, value) -> {
            for(InjectorEx<ENV, T, EX> ex : exs) {
                env = ex.inject(env, value);
            }
            return env;
        };
    }
}
