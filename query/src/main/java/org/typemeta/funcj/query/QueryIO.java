package org.typemeta.funcj.query;

import org.typemeta.funcj.functions.Functions.F;

public interface QueryIO<T> {
    static <T> QueryIO<T> pure(T t) {
        return null;
    }

    <U> QueryIO<U> flatMap(F<T, QueryIO<U>> f);
}
