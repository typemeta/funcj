package org.funcj.control;

import org.funcj.util.*;

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
    public static <R, T, E extends Exception>
    Functions.F<T, R> wrap(FunctionsGenEx.F<T, R, E> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
