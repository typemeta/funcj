package org.typemeta.funcj.control;

import org.typemeta.funcj.data.*;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.tuples.Tuple2;

import java.util.Iterator;
import java.util.stream.Stream;

import static org.typemeta.funcj.control.Trampoline.*;
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
     * Construct a {@code State} which leaves the state value unchanged and sets the result to the supplied value.
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
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param lt        list of values
     * @param f         function to be applied to each value in the list
     * @param <S>       the state type
     * @param <A>       type of list elements
     * @param <B>       the state result type
     * @return          a {@code State} which wraps an {@link IList} of values
     */
    static <S, A, B> State<S, IList<B>> traverse(IList<A> lt, F<A, State<S, B>> f) {
        return lt.foldRight(
            (a, slb) -> f.apply(a).apply(slb.map(l -> l::add)),
            pure(IList.nil())
        );
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
            (sa, sla) -> sa.apply(sla.map(l -> l::add)),
            pure(IList.nil())
        );
    }

    /**
     * Variation of {@link State#sequence(IList)} for {@link Stream}.
     * @param sst       the stream of {@code State} values
     * @param <S>       the state type
     * @param <T>       the result type of the {@code State}s in the stream
     * @return          a {@code State} which wraps an {@link Stream} of values
     */
    static <S, T> State<S, Stream<T>> sequence(Stream<State<S, T>> sst) {
        final Iterator<State<S, T>> iter = sst.iterator();
        State<S, IList<T>> slt = pure(IList.nil());
        while (iter.hasNext()) {
            final State<S, T> st = iter.next();
            slt = st.apply(slt.map(lt -> lt::add));
        }
        return slt.map(IList::stream);
    }

    /**
     * {@code Kleisli} models composable operations that return a {@code State}.
     * @param <S>       the state type
     * @param <A>       the input type
     * @param <B>       the value type of the returned @{code State} type
     */
    @FunctionalInterface
    interface Kleisli<S, A, B> {
        /**
         * Construct a {@code Kleisli} value from a function.
         * @param f         the function
         * @param <S>       the state type
         * @param <T>       the input type
         * @param <U>       the value type of the returned @{code State} value
         * @return          the new {@code Kleisli}
         */
        static <S, T, U> Kleisli<S, T, U> of(F<T, State<S, U>> f) {
            return f::apply;
        }

        /**
         * Run this {@code Kleisli} operation
         * @param a         the input value
         * @return          the result of the operation
         */
        State<S, B> run(A a);

        /**
         * Compose this {@code Kleisli} with another by applying this one first,
         * then the other.
         * @param kUV       the {@code Kleisli} to be applied after this one
         * @param <V>       the second {@code Kleisli}'s return type
         * @return          the composed {@code Kleisli}
         */
        default <V> Kleisli<S, A, V> andThen(Kleisli<S, B, V> kUV) {
            return a -> run(a).flatMap(kUV::run);
        }

        /**
         * Compose this {@code Kleisli} with another by applying the other one first,
         * and then this one.
         * @param kST       the {@code Kleisli} to be applied after this one
         * @param <C>       the first {@code Kleisli}'s input type
         * @return          the composed {@code Kleisli}
         */
        default <C> Kleisli<S, C, B> compose(Kleisli<S, C, A> kST) {
            return s -> kST.run(s).flatMap(this::run);
        }
    }

    /**
     * The state processor.
     * <p>
     * This the SAM that implementations implement, typically via a lambda,
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

