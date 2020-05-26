package org.typemeta.funcj.injectors;

import java.util.*;

public abstract class NumberedInjectorsEx {

    @FunctionalInterface
    public interface DoubleNumberedInjectorEx<ENV, EX extends Exception> extends NumberedInjectorEx<ENV, Double, EX> {
        ENV injectDouble(ENV env, int n, double value) throws EX;
        
        default ENV inject(ENV env, int n, Double value) throws EX {
            return injectDouble(env, n, value);
        }
    }

    @FunctionalInterface
    public interface IntNumberedInjectorEx<ENV, EX extends Exception> extends NumberedInjectorEx<ENV, Integer, EX> {
        ENV injectInt(ENV env, int n, int value) throws EX;

        default ENV inject(ENV env, int n, Integer value) throws EX {
            return injectInt(env, n, value);
        }
    }

    @FunctionalInterface
    public interface LongNumberedInjectorEx<ENV, EX extends Exception> extends NumberedInjectorEx<ENV, Long, EX> {
        ENV injectLong(ENV env, int n, long value) throws EX;

        default ENV inject(ENV env, int n, Long value) throws EX {
            return injectLong(env, n, value);
        }
    }

    public static <ENV, T, EX extends Exception> NumberedInjectorEx<ENV, Optional<T>, EX> optional(
            NumberedInjectorEx<ENV, T, EX> injr
    ) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.get()) : env;
    }

    public static <ENV, EX extends Exception> NumberedInjectorEx<ENV, OptionalDouble, EX> optional(DoubleNumberedInjectorEx<ENV, EX> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.getAsDouble()) : env;
    }

    public static <ENV, EX extends Exception> NumberedInjectorEx<ENV, OptionalInt, EX> optional(IntNumberedInjectorEx<ENV, EX> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.getAsInt()) : env;
    }

    public static <ENV, EX extends Exception> NumberedInjectorEx<ENV, OptionalLong, EX> optional(LongNumberedInjectorEx<ENV, EX> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.getAsLong()) : env;
    }
}
