package org.javafp.control;

import org.javafp.data.*;
import org.javafp.util.*;
import org.javafp.util.Functions.F;

import static org.javafp.util.Unit.UNIT;

/**
 * State monad.
 * Each State instance is a state processor that takes a state of type S,
 * and produces a new state plus a result of type A.
 * @param <S> type of state
 * @param <A> type of result
 */
@FunctionalInterface
public interface State<S, A> {

    static <S, A> State<S, A> result(A x) {
        return st -> Tuple2.of(st, x);
    }

    static <S> State<S, Unit> put(S st) {
        return u -> Tuple2.of(st, UNIT);
    }

    static <S> State<S, S> get() {
        return s -> Tuple2.of(s, s);
    }

    static <S> State<S, Unit> modify(F<S, S> f) {
        return State.<S>get().flatMap(x ->
            put(f.apply(x))
        );
    }

    static <S, A> State<S, A> gets(F<S, A> f) {
        return State.<S>get().flatMap(x ->
            result(f.apply(x))
        );
    }

    /**
     * Standard applicative traversal.
     */
    static <S, A, B> State<S, IList<B>> traverse(IList<A> lt, F<A, State<S, B>> f) {
        return lt.foldr(
            (a, slb) -> f.apply(a).apply(slb.map(l -> l::add)),
            result(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     */
    static <S, A> State<S, IList<A>> sequence(IList<? extends State<S, A>> lsa) {
        return lsa.foldr(
            (sa, sla) -> sa.apply(sla.map(l -> l::add)),
            result(IList.nil())
        );
    }

    Tuple2<S, A> runState(S state);

    default <B> State<S, B> map(F<? super A, ? extends B> f) {
        return st -> {
            final Tuple2<S, A> t2 = runState(st);
            return t2.with2(f.apply(t2._2));
        };
    }

    default <B> State<S, B> apply(State<S, F<A, B>> sf) {
        return st -> {
            final Tuple2<S, F<A, B>> t2F = sf.runState(st);
            final Tuple2<S, A> t2A = this.runState(t2F._1);
            return Tuple2.of(t2A._1, t2F._2.apply(t2A._2));
        };
    }

    default <B> State<S, B> flatMap(F<A, State<S, B>> f) {
        return st -> {
            final Tuple2<S, A> t2 = runState(st);
            return f.apply(t2._2).runState(t2._1);
        };
    }

    default <B> State<S, B> then(State<S, B> sb) {
        return flatMap(u -> sb);
    }

    default S exec(S s) {
        return runState(s)._1;
    }

    default A eval(S s) {
        return runState(s)._2;
    }
}

