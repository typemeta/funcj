package org.javafp.control;

import org.javafp.data.Tuple2;
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

    Tuple2<S, A> runState(S state);

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

