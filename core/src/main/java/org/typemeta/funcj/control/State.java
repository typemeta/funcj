package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.tuples.Tuple2;
import org.typemeta.funcj.util.Folds;
import org.typemeta.funcj.util.Functors;

import java.util.*;

import static org.typemeta.funcj.control.Trampoline.defer;
import static org.typemeta.funcj.control.Trampoline.done;
import static org.typemeta.funcj.data.Unit.UNIT;

/**
 * State monad.
 * <p>
 * Each {@code State} instance is a state processor that takes a state of type {@code S},
 * and produces a new state and a result of type {@code A}.
 * The state processor is represented by the {@link State#runState(Object)} SAM.
 * <p>
 * Note, this {@code State} type uses the {@link Trampoline} monad to
 * replace a recursive evaluation model into an iterative one,
 * thereby avoiding the issue of {@link StackOverflowError}s.
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
        return st -> defer(() -> done(Tuple2.of(st, a)));
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
        return u -> defer(() -> done(Tuple2.of(st, UNIT)));
    }

    /**
     * Construct a {@code State} which leaves the state value unchanged and sets the result to the given value.
     * @param <S>       the state type
     * @return          the new {@code State} instance
     */
    static <S> State<S, S> get() {
        return s -> defer(() -> done(Tuple2.of(s, s)));
    }

    /**
     * Construct a {@code State} which applies a function to the state,
     * and sets the result to {@link Unit#UNIT}.
     * @param f         the function
     * @param <S>       the state type
     * @return          the new {@code State} instance
     */
    static <S> State<S, Unit> modify(F<S, S> f) {
        return State.<S>get().flatMap(s ->
            put(f.apply(s))
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
    static <S, A> State<S, A> inspect(F<S, A> f) {
        return State.<S>get().map(f);
    }

    /**
     * Standard applicative traversal for {@link IList}.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la        list of values
     * @param f         function to be applied to each value in the list
     * @param <S>       the state type
     * @param <A>       type of list elements
     * @param <B>       the state result type
     * @return          a {@code State} which wraps an {@link IList} of values
     */
    static <S, A, B> State<S, IList<B>> traverse(IList<A> la, F<A, State<S, B>> f) {
        return la.foldRight(
                (a, slb) -> slb.apply(f.apply(a).map(b -> l -> l.add(b))),
                pure(IList.nil())
        );
    }

    /**
     * Standard applicative traversal for {@link List}.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la        list of values
     * @param f         function to be applied to each value in the list
     * @param <S>       the state type
     * @param <A>       type of list elements
     * @param <B>       the state result type
     * @return          a {@code State} which wraps an {@link List} of values
     */
    static <S, A, B> State<S, List<B>> traverse(List<A> la, F<A, State<S, B>> f) {
        return sequence(Functors.map(f, la));
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code State} into a {@code State} of an {@code IList},
     * by composing each consecutive {@code State} using the {@link State#apply(State)} method.
     * @param lsa       the list of {@code State} values
     * @param <S>       the state type
     * @param <A>       the result type of the {@code State}s in the list
     * @return          a {@code State} which wraps an {@link IList} of values
     */
    static <S, A> State<S, IList<A>> sequence(IList<? extends State<S, A>> lsa) {
        return lsa.foldRight(
                (sa, sla) -> sla.apply(sa.map(a -> l -> l.add(a))),
                pure(IList.nil())
        );
    }

    /**
     * Variation of {@link State#sequence(IList)} for {@link List}.
     * @param lst       the list of {@code State} values
     * @param <S>       the state type
     * @param <T>       the result type of the {@code State}s in the list
     * @return          a {@code State} which wraps an {@link List} of values
     */
    static <S, T> State<S, List<T>> sequence(List<State<S, T>> lst) {
        final State<S, List<T>> res = Folds.foldRight(
                (st, slt) -> slt.apply(st.map(t -> lt -> {lt.add(t); return lt;})),
                pure(new ArrayList<>(lst.size())),
                lst
        );
        return res.map(l -> {Collections.reverse(l); return l;});
    }

    /**
     * Repeatedly call the function {@code f} until it returns {@code Either.Right}.
     * <p>
     * Call the function {@code f} and if it returns a right value then return the wrapped value,
     * otherwise extract the value and call {@code f} again.
     * @param a         the starting value
     * @param f         the function
     * @param <S>       the state type
     * @param <A>       the starting value type
     * @param <B>       the final value type
     * @return          the final value
     */
    static <S, A, B> State<S, B> tailRecM(A a, F<A, State<S, Either<A, B>>> f) {
        while (true) {
            final State<S, Either<A, B>> s = f.apply(a);
            return s.flatMap(e -> {
                if (e instanceof Either.Left) {
                    final Either.Left<A, B> left = (Either.Left<A, B>) e;
                    return tailRecM(left.value, f);
                } else {
                    final Either.Right<A, B> right = (Either.Right<A, B>) e;
                    return State.pure(right.value);
                }
            });
        }
    }

    /**
     * {@code Kleisli} models composable operations that return a {@code State}.
     * @param <S>       the state type
     * @param <A>       the input type
     * @param <B>       the value type of the returned {@code State} type
     */
    @FunctionalInterface
    interface Kleisli<S, A, B> {
        /**
         * Construct a {@code Kleisli} value from a function.
         * @param f         the function
         * @param <S>       the state type
         * @param <T>       the input type
         * @param <U>       the value type of the returned {@code State} value
         * @return          the new {@code Kleisli}
         */
        static <S, T, U> Kleisli<S, T, U> of(F<T, State<S, U>> f) {
            return f::apply;
        }

        /**
         * Apply this {@code Kleisli} operation
         * @param a         the input value
         * @return          the result of the operation
         */
        State<S, B> apply(A a);

        /**
         * Compose this {@code Kleisli} with another by applying this one first,
         * then the other.
         * @param kUV       the {@code Kleisli} to be applied after this one
         * @param <C>       the second {@code Kleisli}'s return type
         * @return          the composed {@code Kleisli}
         */
        default <C> Kleisli<S, A, C> andThen(Kleisli<S, B, C> kUV) {
            return a -> this.apply(a).flatMap(kUV::apply);
        }

        /**
         * Compose this {@code Kleisli} with another by applying the other one first,
         * and then this one.
         * @param kST       the {@code Kleisli} to be applied after this one
         * @param <C>       the first {@code Kleisli}'s input type
         * @return          the composed {@code Kleisli}
         */
        default <C> Kleisli<S, C, B> compose(Kleisli<S, C, A> kST) {
            return s -> kST.apply(s).flatMap(this::apply);
        }

        /**
         * Compose this {@code Kleisli} with a function,
         * by applying this {@code Kleisli} first,
         * and then mapping the function over the result.
         * @param f         the function
         * @param <C>       the function return type
         * @return          the composed {@code Kleisli}
         */
        default <C> Kleisli<S, A, C> map(F<B, C> f) {
            return t -> this.apply(t).map(f);
        }
    }

    /**
     * The state processor.
     * <p>
     * State implementations must implement thus method, typically via a lambda,
     * {@code runState} is essentially a function that can be applied to a
     * state {@code S} to yield a new state and a result.
     * @param state     the input state.
     * @return          the new state and a result
     */
    Trampoline<Tuple2<S, A>> runState(S state);

    /**
     * Map a function over the state processor.
     * @param f         the function to be mapped
     * @param <B>       the return type of the function
     * @return          the new {@code State}
     */
    default <B> State<S, B> map(F<? super A, ? extends B> f) {
        return st -> runState(st).map(t2 -> t2.map2(f));
    }

    /**
     * Combine a {@code State} that wraps a function with a {@code State} that wraps a value,
     * to yield a {@code State} that wraps the result of applying the function to the value.
     * Applicative apply operation (with args reversed).
     * @param sf        the {@code State} that wraps a function
     * @param <B>       the {@code State} that wraps a value
     * @return          a {@code State} that wraps the result of applying the function to the value
     */
    default <B> State<S, B> apply(State<S, F<A, B>> sf) {
        return sf.flatMap(f ->
                this.flatMap(a ->
                        pure(f.apply(a)))
        );
    }

    /**
     * FlatMap a function over the state.
     * <p>
     * Compose a {@code State} with a function that yields another a {@code State},
     * to form a new a {@code State}.
     * A.k.a. monadic bind operation.
     * @param f         the function that yields a {@code State}
     * @param <B>       result value type
     * @return          a {@code State}
     */
    default <B> State<S, B> flatMap(F<A, State<S, B>> f) {
        return st ->
                defer(() -> runState(st)
                        .flatMap(t2 ->
                                defer(() -> f.apply(t2._2).runState(t2._1)))
                );
    }

    /**
     * Return the state value yielded by running this state processor.
     * @param s         a state value to supply to the state processor
     * @return          the state value yielded by running this state processor
     */
    default S exec(S s) {
            return runState(s).runT()._1;
    }

    /**
     * Return the state result yielded by running this state processor.
     * @param s         a state value to supply to the state processor
     * @return          the state result yielded by running this state processor
     */
    default A eval(S s) {
        return runState(s).runT()._2;
    }
}

