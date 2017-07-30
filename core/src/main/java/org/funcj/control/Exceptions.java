package org.funcj.control;

import org.funcj.util.Functions.F;

/**
 * Utility functions relating to exceptions.
 */
public abstract class Exceptions {

    @FunctionalInterface
    public interface Throws0 {
        void apply() throws Exception;
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws a {@link java.lang.RuntimeException}.
     * @param thrower function that may throw
     */
    public static void wrap(Throws0 thrower) {
        try {
            thrower.apply();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     * @param thrower function that may throw
     * @param trans function to translate thrown exception into another, unchecked, exception
     */
    public static void wrap(Throws0 thrower, F<? super Exception, ? extends RuntimeException> trans) {
        try {
            thrower.apply();
        } catch(Exception ex) {
            throw trans.apply(ex);
        }
    }

    @FunctionalInterface
    public interface Throws1<R> {
        R apply() throws Exception;
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws a {@link java.lang.RuntimeException}.
     * @param thrower function that may throw
     * @param <R> return type of function
     * @return result of function if it doesn't throw
     */
    public static <R> R wrap(Throws1<R> thrower) {
        try {
            return thrower.apply();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     * @param thrower function that may throw
     * @param trans function to translate thrown exception into another, unchecked, exception.
     * @param <R> return type of function
     * @return result of function if it doesn't throw
     */
    public static <R>
    R wrap(Throws1<R> thrower, F<? super Exception, ? extends RuntimeException> trans) {
        try {
            return thrower.apply();
        } catch(Exception ex) {
            throw trans.apply(ex);
        }
    }

    @FunctionalInterface
    public interface Throws2<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     * @param thrower function that may throw
     * @param <T> input type of the function
     * @param <R> return type of function
     * @return a function which throws an unchecked exception
     */
    public static <T, R>
    F<T, R> wrap(Throws2<T, R> thrower) {
        return t -> {
            try {
                return thrower.apply(t);
            } catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     * @param thrower function that may throw
     * @param trans function to translate thrown exception into another, unchecked, exception.
     * @param <T> input type of the function
     * @param <R> return type of function
     * @return a function which throws an unchecked exception
     */
    public static <T, R>
    F<T, R> wrap(Throws2<T, R> thrower, F<? super Exception, ? extends RuntimeException> trans) {
        return t -> {
            try {
                return thrower.apply(t);
            } catch(Exception ex) {
                throw trans.apply(ex);
            }
        };
    }

    /**
     * Used to mark incomplete pieces of code.
     * @return an exception
     */
    public static RuntimeException TODO() {
        return new RuntimeException("TODO");
    }
}
