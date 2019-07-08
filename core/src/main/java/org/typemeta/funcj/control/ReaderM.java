package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.util.Folds;
import org.typemeta.funcj.util.Functors;

import java.util.*;

/**
 * {@code ReaderM} is an implementation of the Reader monad.
 * It is essentially an enriched {@link Functions.F}.
 */
public interface ReaderM<ENV, A> {

    static <ENV> ReaderM<ENV, ENV> ask() {
        return e -> e;
    }

    static <ENV, A> ReaderM<ENV, A> asks(F<ENV, A> f) {
        return f::apply;
    }

    static <ENV, A, T> ReaderM<ENV, A> local(F<ENV, T> f, ReaderM<T, A> r) {
        //return ReaderM.<ENV>ask().map(env -> r.run(f.apply(env)));
        return a -> r.run(f.apply(a));
    }

    /**
     * A {@code Reader} that always returns the same value, regardless of its argument
     * @param a         the value the {@code Reader} will return
     * @param <ENV>       the input type of the {@code Reader}
     * @param <A>       the type of the constant value {@code r}
     * @return          the constant {@code Reader}
     */
    static <ENV, A> ReaderM<ENV, A> pure(A a) {
        return e -> a;
    }

    /**
     * Applicative function application.
     * @param rf        the function wrapped in a {@code Reader}
     * @param ra        the function argument wrapped in a {@code Reader}
     * @param <ENV>     the function argument type
     * @param <A>       the function input type
     * @param <B>       the function return type
     * @return          the result of applying the function to the argument, wrapped in a {@code Try}
     */
    static <ENV, A, B> ReaderM<ENV, B> ap(ReaderM<ENV, F<A, B>> rf, ReaderM<ENV, A> ra) {
        return ra.app(rf);
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la        the list of values
     * @param frb       the function to be applied to each value in the list
     * @param <ENV>     the input type of the applicative function
     * @param <A>       the type of list elements
     * @param <B>       the return type of the {@code F} returned by the function
     * @return          an {@code F} which wraps an {@link IList} of values
     */
    static <ENV, A, B> ReaderM<ENV, IList<B>> traverse(IList<A> la, F<A, ReaderM<ENV, B>> frb) {
        return la.foldRight(
                (a, rlb) -> rlb.app(frb.apply(a).map(b -> lb -> lb.add(b))),
                pure(IList.empty())
        );
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la        the list of values
     * @param frb       the function to be applied to each value in the list
     * @param <ENV>     the input type of the applicative function
     * @param <A>       the type of list elements
     * @param <B>       the return type of the {@code F} returned by the function
     * @return          an {@code F} which wraps an {@link List} of values
     */
    static <ENV, A, B> ReaderM<ENV, List<B>> traverse(List<A> la, F<A, ReaderM<ENV, B>> frb) {
        return sequence(Functors.map(frb, la));
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code F}s into a {@code F} of an {@code IList},
     * by composing each consecutive {@code F}s using the {@link F#app} method.
     * @param lrb       the list of {@code F} values
     * @param <ENV>     the input type of the applicative function
     * @param <A>       the return type of the {@code F}s in the list
     * @return          a {@code Reader} which wraps an {@link IList} of values
     */
    static <ENV, A> ReaderM<ENV, IList<A>> sequence(IList<ReaderM<ENV, A>> lrb) {
        return lrb.foldRight(
                (ra, rla) -> rla.app(ra.map(a -> la -> la.add(a))),
                pure(IList.empty())
        );
    }

    /**
     * Variation of {@link ReaderM#sequence(IList)} for {@link List}.
     * @param lra       the list of {@code F} values
     * @param <ENV>     the error type
     * @param <A>       the return type of the {@code F}s in the stream
     * @return          a {@code F} which wraps a list of values
     */
    static <ENV, A> ReaderM<ENV, List<A>> sequence(List<ReaderM<ENV, A>> lra) {
        final ReaderM<ENV, List<A>> res = Folds.foldRight(
                (ra, rla) -> rla.app(ra.map(a -> la -> {la.add(a); return la;})),
                pure(new ArrayList<A>(lra.size())),
                lra
        );
        return res.map(l -> {Collections.reverse(l); return l;});
    }

    /**
     * Run this reader.
     * @param a         the function argument
     * @return          the result of applying this function
     */
    A run(ENV a);

    /**
     * Map a function over this {@code Reader}.
     * @param f         the function to compose with
     * @param <B>       the argument type to {@code f}
     * @return          a function that first applies {@code f} and then applies this function to the result.
     */
    default <B> ReaderM<ENV, B> map(F<A, B> f) {
        return a -> f.apply(this.run(a));
    }

    /**
     * Applicative {@code Reader} composition.
     * @param rf        the {@code Reader} to compose with
     * @param <B>       the return type of the function returned by {@code f}
     * @return          the composed function
     */
    default <B> ReaderM<ENV, B> app(ReaderM<ENV, F<A, B>> rf) {
        return a -> rf.run(a).apply(this.run(a));
    }

    /**
     * Monadic function composition.
     * @param f         the function to compose with
     * @param <B>       the return type of the function returned by {@code f}
     * @return          the composed function
     */
    default <B> ReaderM<ENV, B> flatMap(F<A, ReaderM<ENV, B>> f) {
        return a -> f.apply(this.run(a)).run(a);
    }
}
