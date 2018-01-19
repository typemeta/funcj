package org.typemeta.funcj.util;

import org.typemeta.funcj.functions.FunctionsGenEx.*;

import java.util.*;

/**
 * Fold operations for lambdas with can throw.
 */
public abstract class FoldsGenEx {
    /**
     * Left-fold a function over an {@link Iterable}.
     * @param f         the binary function to be applied for the fold
     * @param z         the starting value for the fold
     * @param iter      the iterable to be folded over
     * @param <T>       the iterable element type
     * @param <R>       the result type of fold operation
     * @param <X>       the exception type
     * @return          the folded value
     * @throws X        the exception thrown by the function
     */
    public static <T, R, X extends Exception> R foldLeft(F2<R, T, R, X> f, R z, Iterable<T> iter) throws X {
        R acc = z;
        for (T t : iter) {
            acc = f.apply(acc, t);
        }
        return acc;
    }

    /**
     * Left-fold a function over a non-empty {@link Iterable}.
     * @param f         the binary operator to be applied for the fold
     * @param iter      the iterable to be folded over
     * @param <T>       the iterable element type
     * @param <X>       the exception type
     * @return          the folded value
     * @throws X        the exception thrown by the operator
     */
    public static <T, X extends Exception> T foldLeft1(Op2<T, X> f, Iterable<T> iter) throws X {
        T acc = null;
        for (T t : iter) {
            if (acc == null) {
                acc = t;
            } else {
                acc = f.apply(acc, t);
            }
        }

        if (acc == null) {
            throw new IllegalArgumentException("Supplied Iterable argument is empty");
        } else {
            return acc;
        }
    }

    /**
     *
     * Right-fold a function over an {@link List}}
     * @param f         the binary function to be applied for the fold
     * @param z         the starting value for the fold
     * @param l         the list to fold over
     * @param <T>       the list element type
     * @param <R>       the result type of fold operation
     * @param <X>       the exception type
     * @return          the folded value
     * @throws X        the exception thrown by the function
     */
    public static <T, R, X extends Exception> R foldRight(F2<T, R, R, X> f, R z, List<T> l) throws X {
        R acc = z;
        for (int i = l.size() - 1; i >= 0; --i) {
            acc = f.apply(l.get(i), acc);
        }
        return acc;
    }

    /**
     * Right-fold a function over a non-empty {@link List}.
     * @param f         the binary operator to be applied for the fold
     * @param l         the list to fold over
     * @param <T>       the list element type
     * @param <X>       the exception type
     * @return          the folded value
     * @throws X        the exception thrown by the operator
     */
    public static <T, X extends Exception> T foldRight1(Op2<T, X> f, List<T> l) throws X {
        final int i0 = l.size() - 1;
        T acc = null;
        for (int i = i0; i >= 0; --i) {
            if (i == i0) {
                acc = l.get(i);
            } else {
                acc = f.apply(l.get(i), acc);
            }
        }
        return acc;
    }

    /**
     * Right-fold a function over an {@link Set}}
     * @param f         the binary function to be applied for the fold
     * @param z         the starting value for the fold
     * @param s         the set to fold over
     * @param <T>       the set element type
     * @param <R>       the result type of fold operation
     * @param <X>       the exception type
     * @return          the folded value
     * @throws X        the exception thrown by the function
     */
    public static <T, R, X extends Exception> R foldRight(F2<T, R, R, X> f, R z, Set<T> s) throws X {
        return foldRight(f, z, new ArrayList<T>(s));
    }

    /**
     * Right-fold a function over a non-empty  {@link Set}}
     * @param f         the binary operator to be applied for the fold
     * @param s         the set to fold over
     * @param <T>       the set element type
     * @param <X>       the exception type
     * @return          the folded value
     * @throws X        the exception thrown by the operator
     */
    public static <T, X extends Exception> T foldRight1(Op2<T, X> f, Set<T> s) throws X {
        return foldRight1(f, new ArrayList<T>(s));
    }
}
