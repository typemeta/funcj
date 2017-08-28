package org.funcj.control;

import org.funcj.data.*;
import org.funcj.util.Functions.F;

import static org.funcj.data.Unit.UNIT;

/**
 * State monad.
 * <p>
 * Each {@code State} instance is a state processor that takes a state of type {@code S},
 * and produces a new state and a result of type {@code A}.
 * @param <S>       the state type
 * @param <A>       the result type
 */
@FunctionalInterface
public interface State<S, A> {

    /**
     * A construct a {@code State} instance which leaves the input state unchanged and sets the result to a.
     * @param a         the state result value
     * @param <S>       the state type
     * @param <A>       the result type
     * @return          the new {@code State} instance
     */
    static <S, A> State<S, A> pure(A a) {
        return st -> Tuple2.of(st, a);
    }

    /**
     * Take a function provided by a {@code State} and apply it to a value in another {@code State}.
     * @param sf        the state that wraps a function type
     * @param sa        the state that wraps the function argument type
     * @param <S>       the state type
     * @param <A>       the function argument type
     * @param <B>       the function return type
     * @return          a new {@code State} instance which contains the result of applying the function
     */
    static <S, A, B> State<S, B> ap(State<S, F<A, B>> sf, State<S, A> sa) {
        return sa.apply(sf);
    }

    /**
     * Construct a {@code State} which sets the state to {@code st} and sets the result to {@link Unit#UNIT}.
     * @param st        the state value
     * @param <S>       the state type
     * @return          the new {@code State} instance
     */
    static <S> State<S, Unit> put(S st) {
        return u -> Tuple2.of(st, UNIT);
    }

    /**
     * Construct a {@code State} which leaves the state value unchanged and sets the result to the supplied value.
     * @param <S>       the state type
     * @return          the new {@code State} instance
     */
    static <S> State<S, S> get() {
        return s -> Tuple2.of(s, s);
    }

    /**
     * Construct a {@code State} which applies a function to the state,
     * and sets the result to {@link org.funcj.data.Unit#UNIT}.
     * @param f         the function
     * @param <S>       the state type
     * @return          the new {@code State} instance
     */
    static <S> State<S, Unit> modify(F<S, S> f) {
        return State.<S>get().flatMap(x ->
            put(f.apply(x))
        );
    }

    /**
     * A state which leaves the state unchanged,
     * and sets the result to the function f applied to the state.
     * @param f         the function
     * @param <S>       the state type
     * @param <A>       the result type
     * @return          the new {@code State} instance
     */
    static <S, A> State<S, A> gets(F<S, A> f) {
        return State.<S>get().map(f);
    }

    /**
     * Standard applicative traversal.
     * @param lt        list of values
     * @param f         function to be applied to each value in the list
     * @param <S>       the state type
     * @param <A>       type of list elements
     * @param <B>       the state result type
     * @return          a {@code State} which wraps an {@link org.funcj.data.IList} of values
     */
    static <S, A, B> State<S, IList<B>> traverse(IList<A> lt, F<A, State<S, B>> f) {
        return lt.foldRight(
            (a, slb) -> f.apply(a).apply(slb.map(l -> l::add)),
            pure(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * @param lsa       the list of {@code State} values
     * @param <S>       the state type
     * @param <A>       the state result type
     * @return          a {@code State} which wraps an {@link org.funcj.data.IList} of values
     */
    static <S, A> State<S, IList<A>> sequence(IList<? extends State<S, A>> lsa) {
        return lsa.foldRight(
            (sa, sla) -> sa.apply(sla.map(l -> l::add)),
            pure(IList.nil())
        );
    }

    /**
     * The state processor.
     */
    Tuple2<S, A> runState(S state);

    /**
     * Map a function over the state processor.
     */
    default <B> State<S, B> map(F<? super A, ? extends B> f) {
        return st -> runState(st).map2(f::apply);
    }

    /**
     * Apply a function provided by a state to this state.
     * Applicative apply operation (with args reversed).
     */
    default <B> State<S, B> apply(State<S, F<A, B>> sf) {
        return st -> {
            final Tuple2<S, F<A, B>> t2F = sf.runState(st);
            return this.runState(t2F._1).map2(t2F._2);
        };
    }

    /**
     * FlatMap a function over the state.
     * Monadic bind operation.
     */
    default <B> State<S, B> flatMap(F<A, State<S, B>> f) {
        return st -> {
            final Tuple2<S, A> t2 = runState(st);
            return f.apply(t2._2).runState(t2._1);
        };
    }

    /**
     * FlatMap a const function over the state.
     * Monadic bind operation.
     */
    default <B> State<S, B> then(State<S, B> sb) {
        return flatMap(u -> sb);
    }

    /**
     * @return the state yielded by the processor
     */
    default S exec(S s) {
        return runState(s)._1;
    }

    /**
     * @return the result yielded by the processor
     */
    default A eval(S s) {
        return runState(s)._2;
    }
}

