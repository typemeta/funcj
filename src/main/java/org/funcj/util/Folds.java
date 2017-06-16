package org.funcj.util;

import org.funcj.util.Functions.*;

import java.util.List;

/**
 * Fold operations.
 */
public abstract class Folds {
    /**
     * Left-fold a function over an Iterable.
     */
    public static <T, R> R foldLeft(F2<R, T, R> f, R z, Iterable<T> ts) {
        R acc = z;
        for (T t : ts) {
            acc = f.apply(acc, t);
        }
        return acc;
    }

    /**
     * Left-fold a function over a non-empty Iterable.
     */
    public static <T> T foldLeft1(Op2<T> f, Iterable<T> ts) {
        T acc = null;
        for (T t : ts) {
            if (acc == null) {
                acc = t;
            } else {
                acc = f.apply(acc, t);
            }
        }
        return acc;
    }

    /**
     * Right-fold a function over an {@code List}.
     */
    public static <T, R> R foldRight(F2<T, R, R> f, R z, List<T> ts) {
        R acc = z;
        for (int i = ts.size() - 1; i >= 0; --i) {
            acc = f.apply(ts.get(i), acc);
        }
        return acc;
    }

    /**
     * Right-fold a function over an {@code List}.
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
