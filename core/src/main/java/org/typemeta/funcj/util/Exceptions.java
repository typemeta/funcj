package org.typemeta.funcj.util;

import org.typemeta.funcj.functions.*;
import org.typemeta.funcj.functions.Functions.F;

/**
 * Utility functions relating to exceptions.
 */
@SuppressWarnings("unchecked")
public abstract class Exceptions {

    /**
     * Rethrow a checked exception, hiding it at the type level using a Java quirk.
     * @param ex        the exception
     * @param <X>       the exception type
     * @param <T>       the return type
     * @return          never returns
     * @throws X        the hidden checked exception
     */
    private static <X extends Exception, T> T throwUnchecked(Exception ex) throws X {
        throw (X) ex;
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws a hidden checked exception.
     * The original exception can be rethrown by an enclosing
     * call to {@link #unwrap(SideEffectGenEx.F0)}.
     * @param thrower   the function that may throw
     */
    public static void wrap(SideEffectEx.F0 thrower) {
        try {
            thrower.apply();
        } catch(Exception ex) {
            throwUnchecked(ex);
        }
    }

    public static <T> SideEffect.F<T> wrap(SideEffectEx.F<T> thrower) {
        return t -> {
            try {
                thrower.apply(t);
            } catch(Exception ex) {
                throwUnchecked(ex);
            }
        };
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws a hidden checked exception.
     * The original exception can be rethrown by an enclosing call to {@link #unwrap(Functions.F0)}.
     * @param thrower   the function that may throw
     * @param <R>       the return type of function
     * @return          the result of function if it doesn't throw
     */
    public static <R> R wrap(FunctionsEx.F0<R> thrower) {
        try {
            return thrower.apply();
        } catch(Exception ex) {
            return throwUnchecked(ex);
        }
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws a hidden checked exception.
     * The original exception can be rethrown by an enclosing call to {@link #unwrap(Functions.F0)}.
     * @param thrower   the function that may throw
     * @param <T>       the input type of the function
     * @param <R>       the return type of function
     * @return          a function which throws an unchecked exception
     */
    public static <T, R> F<T, R> wrap(FunctionsEx.F<T, R> thrower) {
        return t -> {
            try {
                return thrower.apply(t);
            } catch(Exception ex) {
                return throwUnchecked(ex);
            }
        };
    }

    /**
     * Wrap a function which throws a checked exception
     * into one that that throws a hidden checked exception.
     * The original exception can be rethrown by an enclosing call to {@link #unwrap(Functions.F0)}.
     * @param thrower   the function that may throw
     * @param <A>       the type of the first argument of the function
     * @param <B>       the type of the second argument of the function
     * @param <R>       the return type of function
     * @return          a function which throws an unchecked exception
     */
    public static <A, B, R>
    Functions.F2<A, B, R> wrap(FunctionsEx.F2<A, B, R> thrower) {
        return (a, b) -> {
            try {
                return thrower.apply(a, b);
            } catch(Exception ex) {
                return throwUnchecked(ex);
            }
        };
    }

    /**
     * Undo the effect of {@code wrap} by catching the exception,
     * and rethrowing the original checked exception.
     * @param thrower   the function that may throw an exception
     * @param <X>       the original exception type
     * @throws X        the original exception
     */
    public static <X extends Exception> void unwrap(SideEffectGenEx.F0<X> thrower) throws X {
        try {
            thrower.apply();
        } catch (Exception ex) {
            throw (X)ex;
        }
    }

    /**
     * Undo the effect of {@code wrap} by catching the exception,
     * and rethrowing the original checked exception.
     * @param thrower   the function that may throw an exception
     * @param <R>       the return type of the function
     * @param <X>       the original exception type
     * @return          the function value, if it doesn't throw
     * @throws X        the original exception
     */
    public static <R, X extends Exception> R unwrap(Functions.F0<R> thrower) throws X {
        try {
            return thrower.apply();
        } catch (Exception ex) {
            throw (X)ex;
        }
    }

    /**
     * Used to mark incomplete pieces of code.
     * @return          an exception
     */
    public static RuntimeException TODO() {
        return new RuntimeException("TODO");
    }
}
