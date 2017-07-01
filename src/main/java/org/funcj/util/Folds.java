package org.funcj.util;

import org.funcj.util.Functions.*;

import java.util.List;

/**
 * Fold operations.
 */
public abstract class Folds {
    /**
     * Left-fold a function over an {@link java.lang.Iterable}.
     * @param f binary function to be applied for the fold
     * @param z starting value for the fold
     * @param iter Iterable to be folded over
     * @param <T> iterable element type
     * @param <R> result type of fold operation
     * @return the folded value
     */
    public static <T, R> R foldLeft(F2<R, T, R> f, R z, Iterable<T> iter) {
        R acc = z;
        for (T t : iter) {
            acc = f.apply(acc, t);
        }
        return acc;
    }

    /**
     * Left-fold a function over a non-empty {@link java.lang.Iterable}.
     * @param f binary operator to be applied for the fold
     * @param iter Iterable to be folded over
     * @param <T> iterable element type
     * @return the folded value
     * */
    public static <T> T foldLeft1(Op2<T> f, Iterable<T> iter) {
        T acc = null;
        for (T t : iter) {
            if (acc == null) {
                acc = t;
            } else {
                acc = f.apply(acc, t);
            }
        }
        return acc;
    }

    /**
     * Right-fold a function over an {@link java.util.List}.
     */
    public static <T, R> R foldRight(F2<T, R, R> f, R z, List<T> ts) {
        R acc = z;
        for (int i = ts.size() - 1; i >= 0; --i) {
            acc = f.apply(ts.get(i), acc);
        }
        return acc;
    }

    /**
     * Right-fold a function over an {@link java.util.List}.
     */
    public static <T> T foldRight1(Op2<T> f, List<T> ts) {
        final int i0 = ts.size() - 1;
        T acc = null;
        for (int i = i0; i >= 0; --i) {
            if (i == i0) {
                acc = ts.get(i);
            } else {
                acc = f.apply(ts.get(i), acc);
            }
        }
        return acc;
    }
}
