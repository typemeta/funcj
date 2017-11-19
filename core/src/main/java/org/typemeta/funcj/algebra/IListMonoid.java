package org.typemeta.funcj.algebra;

import org.typemeta.funcj.data.IList;

public class IListMonoid<T> implements Monoid<IList<T>> {
    @Override
    public IList<T> zero() {
        return IList.of();
    }

    @Override
    public IList<T> combine(IList<T> x, IList<T> y) {
        return x.appendAll(y);
    }
}
