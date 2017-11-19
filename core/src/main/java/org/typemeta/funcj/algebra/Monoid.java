package org.typemeta.funcj.algebra;

import org.typemeta.funcj.util.Folds;

public interface Monoid<T> {
    T zero();
    T combine(T x, T y);

    default T fold(Iterable<T> iter) {
        return Folds.foldLeft(this::combine, zero(), iter);
    }
}
