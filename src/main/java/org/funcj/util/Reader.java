package org.funcj.util;

import org.funcj.data.IList;
import org.funcj.util.Functions.F;

/**
 * The {@code Reader} class adds utility methods for using the function monad {@link org.funcj.util.Functions.F}
 * as a Reader monad.
 */
public abstract class Reader {
    static <S> F<S, S> ask() {
        return F.of(s -> s);
    }

    /**
     * Standard applicative traversal.
     */
    static <S, A, B> F<S, IList<B>> traverse(IList<A> lt, F<A, F<S, B>> f) {
        return lt.foldRight(
                (a, flb) -> f.apply(a).app(flb.map(l -> l::add)),
                F.konst(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     */
    static <S, A> F<S, IList<A>> sequence(IList<? extends F<S, A>> lfsa) {
        return lfsa.foldRight(
                (sa, sla) -> sa.app(sla.map(l -> l::add)),
                F.konst(IList.nil())
        );
    }
}
