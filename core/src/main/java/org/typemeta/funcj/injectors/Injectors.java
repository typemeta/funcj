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

    public static <ENV, T> Injector<ENV, Optional<T>> optional(Injector<ENV, T> injr) {
        return (env, optVal) ->
                optVal.isPresent() ? injr.inject(env, optVal.get()) : env;
    }
}
