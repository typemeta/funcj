package org.typemeta.funcj.injectors;

import java.util.*;

public abstract class NumberedInjectors {

    @FunctionalInterface
    public interface DoubleNumberedInjector<ENV> extends NumberedInjector<ENV, Double> {
        ENV injectDouble(ENV env, int n, double value);

        default ENV inject(ENV env, int n, Double value) {
            return injectDouble(env, n, value);
        }
    }

    @FunctionalInterface
    public interface IntNumberedInjector<ENV> extends NumberedInjector<ENV, Integer> {
        ENV injectInt(ENV env, int n, double value);

        default ENV inject(ENV env, int n, Integer value) {
            return injectInt(env, n, value);
        }
    }

    @FunctionalInterface
    public interface LongNumberedInjector<ENV> extends NumberedInjector<ENV, Long> {
        ENV injectLong(ENV env, int n, double value);

        default ENV inject(ENV env, int n, Long value) {
            return injectLong(env, n, value);
        }
    }

    public static <ENV, T> NumberedInjector<ENV, Optional<T>> optional(NumberedInjector<ENV, T> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.get()) : env;
    }

    public static <ENV> NumberedInjector<ENV, OptionalDouble> optional(DoubleNumberedInjector<ENV> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.getAsDouble()) : env;
    }

    public static <ENV> NumberedInjector<ENV, OptionalInt> optional(IntNumberedInjector<ENV> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.getAsInt()) : env;
    }

    public static <ENV> NumberedInjector<ENV, OptionalLong> optional(LongNumberedInjector<ENV> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.getAsLong()) : env;
    }
}
