package org.typemeta.funcj.util;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * The {@code Reader} class adds utility methods for using the function monad {@link Functions.F}
 * as a Reader monad.
 */
public abstract class Reader {
    static <T> Functions.F<T, T> ask() {
        return t -> t;
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la            the list of values
     * @param ffb           the function to be applied to each value in the list
     * @param <T>           the input type of the applicative function
     * @param <A>           the type of list elements
     * @param <B>           the return type of the {@code F} returned by the function
     * @return              an {@code F} which wraps an {@link IList} of values
     */
    static <T, A, B> Functions.F<T, IList<B>> traverse(IList<A> la, Functions.F<A, Functions.F<T, B>> ffb) {
        return la.foldRight(
                (a, flb) -> ffb.apply(a).app(flb.map(l -> l::add)),
                Functions.F.konst(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code F}s into a {@code F} of an {@code IList},
     * by composing each consecutive {@code F}s using the {@link Functions.F#app(Functions.F)} method.
     * @param lfu           the list of {@code F} values
     * @param <T>           the input type of the applicative function
     * @param <U>           the return type of the {@code F}s in the list
     * @return
     */
    static <T, U> Functions.F<T, IList<U>> sequence(IList<? extends Functions.F<T, U>> lfu) {
        return lfu.foldRight(
                (fu, flu) -> fu.app(flu.map(l -> l::add)),
                Functions.F.konst(IList.nil())
        );
    }


    /**
     * Variation of {@link Reader#sequence(IList)} for {@link Stream}.
     * @param sfu       the stream of {@code F} values
     * @param <T>       the error type
     * @param <U>       the return type of the {@code F}s in the stream
     * @return          a {@code F} which wraps an {@link Stream} of values
     */
    static <T, U> Functions.F<T, Stream<U>> sequence(Stream<Functions.F<T, U>> sfu) {
        final Iterator<Functions.F<T, U>> iter = sfu.iterator();
        Functions.F<T, IList<U>> flu = Functions.F.konst(IList.nil());
        while (iter.hasNext()) {
            final Functions.F<T, U> fu = iter.next();
            flu = fu.app(flu.map(lt -> lt::add));
        }
        return flu.map(IList::stream);
    }
}
