package io.typemeta.funcj.util;

import io.typemeta.funcj.data.IList;
import io.typemeta.funcj.util.Functions.F;

/**
 * The {@code Reader} class adds utility methods for using the function monad {@link Functions.F}
 * as a Reader monad.
 */
public abstract class Reader {
    static <S> Functions.F<S, S> ask() {
        return s -> s;
    }

    /**
     * Standard applicative traversal.
     */
    static <S, A, B> Functions.F<S, IList<B>> traverse(IList<A> lt, Functions.F<A, F<S, B>> f) {
        return lt.foldRight(
                (a, flb) -> f.apply(a).app(flb.map(l -> l::add)),
                Functions.F.konst(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     */
    static <S, A> Functions.F<S, IList<A>> sequence(IList<? extends Functions.F<S, A>> lfsa) {
        return lfsa.foldRight(
                (sa, sla) -> sa.app(sla.map(l -> l::add)),
                Functions.F.konst(IList.nil())
        );
    }
}
