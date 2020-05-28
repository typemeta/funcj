package org.typemeta.funcj.injectors;

import java.util.Optional;

public abstract class Injectors {
    @FunctionalInterface
    public interface DoubleInjector<ENV> extends Injector<ENV, Double> {
        static <ENV> DoubleInjector<ENV> of(DoubleInjector<ENV> injr) {
            return injr;
        }

        ENV inject(ENV env, double value);

        @Override
        default ENV inject(ENV env, Double value) {
            return inject(env, value.doubleValue());
        }
    }

    @FunctionalInterface
    public interface IntInjector<ENV> extends Injector<ENV, Integer> {
        static <ENV> IntInjector<ENV> of(IntInjector<ENV> injr) {
            return injr;
        }

        ENV inject(ENV env, int value);

        @Override
        default ENV inject(ENV env, Integer value) {
            return inject(env, value.intValue());
        }
    }

    @FunctionalInterface
    public interface LongInjector<ENV> extends Injector<ENV, Long> {
        static <ENV> LongInjector<ENV> of(LongInjector<ENV> injr) {
            return injr;
        }

        ENV inject(ENV env, long value);

        @Override
        default ENV inject(ENV env, Long value) {
            return inject(env, value.longValue());
        }
    }

    public static <ENV, T> Injector<ENV, Optional<T>> optional(Injector<ENV, T> injr) {
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
    @SafeVarargs
    public static <ENV, T> Injector<ENV, T> combine(
            Injector<ENV, T> ... exs
    ) {
        return (env, value) -> {
            for(Injector<ENV, T> ex : exs) {
                env = ex.inject(env, value);
            }
            return env;
        };
    }
}
