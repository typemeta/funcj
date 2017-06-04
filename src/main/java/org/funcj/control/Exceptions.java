package org.funcj.control;

import org.funcj.util.Functions.F;
import org.funcj.util.FunctionsGenEx;

/**
 * Utility functions relating to exceptions.
 */
public abstract class Exceptions {

    @FunctionalInterface
    public interface ThrowsVoid<E extends Exception> {
        void apply() throws E;
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     */
    public static <E extends Exception>
    void wrap(ThrowsVoid<E> f) {
        try {
            f.apply();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     */
    public static <E extends Exception, E2 extends RuntimeException>
    void wrap(ThrowsVoid<E> f, F<Exception, E2> exSupp) {
        try {
            f.apply();
        } catch(Exception ex) {
            throw exSupp.apply(ex);
        }
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     */
    public static <R, E extends Exception>
    R wrap(FunctionsGenEx.F0<R, E> f) {
        try {
            return f.apply();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     */
    public static <R, E extends Exception, E2 extends RuntimeException>
    R wrap(FunctionsGenEx.F0<R, E> f, F<Exception, E2> exSupp) {
        try {
            return f.apply();
        } catch(Exception ex) {
            throw exSupp.apply(ex);
        }
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     */
    public static <R, T, E extends Exception>
    F<T, R> wrap(FunctionsGenEx.F<T, R, E> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws an unchecked exception.
     */
    public static <R, T, E extends Exception, E2 extends RuntimeException>
    F<T, R> wrap(FunctionsGenEx.F<T, R, E> f, F<Exception, E2> exSupp) {
        return t -> {
            try {
                return f.apply(t);
            } catch(Exception ex) {
                throw exSupp.apply(ex);
            }
        };
    }

    public static RuntimeException TODO() {
        return new RuntimeException("TODO");
    }
}
