package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.tuples.Tuple2;
import org.typemeta.funcj.util.Folds;
import org.typemeta.funcj.util.Functors;

import java.util.*;

import static org.typemeta.funcj.data.Unit.UNIT;

/**
 * State monad.
 * <p>
 * Each {@code StateR} instance is a state processor that takes a state of type {@code S},
 * and produces a new state and a result of type {@code A}.
 * The state processor is represented by the {@link StateR#runState(Object)} SAM.
 * <p>
 * Note, this {@code StateR} type uses recursion and will blow the stack
 * for heavily chained computations.
 * If this is an issue then use {@link State}, which avoids recursion.
 * @param <S>       the state type
 * @param <A>       the result type
 */
@FunctionalInterface
public interface StateR<S, A> {

    /**
     * A construct a {@code StateR} instance which leaves the input state unchanged and sets the result to a.
     * @param a         the state result value
     * @param <S>       the state type
     * @param <A>       the result type
     * @return          the new {@code StateR} instance
     */
    static <S, A> StateR<S, A> pure(A a) {
        return st -> Tuple2.of(st, a);
    }

    /**
     * Take a function provided by a {@code StateR} and apply it to a value in another {@code StateR}.
     * @param sf        the state that wraps a function type
     * @param sa        the state that wraps the function argument type
     * @param <S>       the state type
     * @param <A>       the function argument type
     * @param <B>       the function return type
     * @return          a new {@code StateR} instance which contains the result of applying the function
     */
    static <S, A, B> StateR<S, B> ap(StateR<S, F<A, B>> sf, StateR<S, A> sa) {
        return sa.app(sf);
    }

    /**
     * Construct a {@code StateR} which sets the state to {@code st} and sets the result to {@link Unit#UNIT}.
     * @param st        the state value
     * @param <S>       the state type
     * @return          the new {@code StateR} instance
     */
    static <S> StateR<S, Unit> put(S st) {
        return u -> Tuple2.of(st, UNIT);
    }

    /**
     * Construct a {@code StateR} which leaves the state value unchanged and sets the result to the given value.
     * @param <S>       the state type
     * @return          the new {@code StateR} instance
     */
    static <S> StateR<S, S> get() {
        return s -> Tuple2.of(s, s);
    }

    /**
     * Construct a {@code StateR} which applies a function to the state,
     * and sets the result to {@link Unit#UNIT}.
     * @param f         the function
     * @param <S>       the state type
     * @return          the new {@code StateR} instance
     */
    static <S> StateR<S, Unit> modify(F<S, S> f) {
        return StateR.<S>get().flatMap(s ->
            put(f.apply(s))
        );
    }

    /**
     * A state which leaves the state unchanged,
     * and sets the result to the function f applied to the state.
     * @param f         the function
     * @param <S>       the state type
     * @param <A>       the result type
     * @return          the new {@code StateR} instance
     */
    static <S, A> StateR<S, A> inspect(F<S, A> f) {
        return StateR.<S>get().map(f);
    }

    /**
     * A state which leaves the state unchanged,
     * and sets the result to the function f applied to the state.
     * @param f         the function
     * @param <S>       the state type
     * @param <A>       the result type
     * @return          the new {@code StateR} instance
     */
    static <S, A> StateR<S, A> gets(F<S, A> f) {
        return StateR.<S>get().map(f);
    }

    /**
     * Standard applicative traversal for {@link IList}.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la        {@link IList} of values
     * @param f         function to be applied to each value in the list
     * @param <S>       the state type
     * @param <A>       type of list elements
     * @param <B>       the state result type
     * @return          a {@code StateR} which wraps a {@code IList} of values
     */
    static <S, A, B> StateR<S, IList<B>> traverse(IList<A> la, F<A, StateR<S, B>> f) {
        return la.foldRight(
                (a, slb) -> slb.app(f.apply(a).map(b -> l -> l.add(b))),
                pure(IList.nil())
        );
    }

    /**
     * Standard applicative traversal for {@link List}.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la        {@link List} of values
     * @param f         function to be applied to each value in the list
     * @param <S>       the state type
     * @param <A>       type of list elements
     * @param <B>       the state result type
     * @return          a {@code StateR} which wraps a {@code List} of values
     */
    static <S, A, B> StateR<S, List<B>> traverse(List<A> la, F<A, StateR<S, B>> f) {
        return sequence(Functors.map(f, la));
    }

    /**
     * Standard applicative sequencing.
     * @param lsa       the {@link IList} of {@code StateR} values
     * @param <S>       the state type
     * @param <A>       the state result type
     * @return          a {@code StateR} which wraps a {@code IList} of values
     */
    static <S, A> StateR<S, IList<A>> sequence(IList<? extends StateR<S, A>> lsa) {
        return lsa.foldRight(
                (sa, sla) -> sla.app(sa.map(a -> l -> l.add(a))),
                pure(IList.nil())
        );
    }

    /**
     * Variation of {@link StateR#sequence(IList)} for {@link List}.
     * @param lst       the list of {@code StateR} values
     * @param <S>       the state type
     * @param <T>       the result type of the {@code StateR}s in the list
     * @return          a {@code StateR} which wraps an {@code List} of values
     */
    static <S, T> StateR<S, List<T>> sequence(List<StateR<S, T>> lst) {
        final StateR<S, List<T>> res = Folds.foldRight(
                (st, slt) -> slt.app(st.map(t -> lt -> {lt.add(t); return lt;})),
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
    static <S, A, B> StateR<S, B> tailRecM(A a, F<A, StateR<S, Either<A, B>>> f) {
        return st -> {
            A aa = a;
            S s = st;
            while (true) {
                final Tuple2<S, Either<A, B>> t2e = f.apply(aa).runState(s);

                if (t2e._2 instanceof Either.Left) {
                    final Either.Left<A, B> left = (Either.Left<A, B>) t2e._2;
                    aa = left.value;
                    s = t2e._1;
                } else {
                    final Either.Right<A, B> right = (Either.Right<A, B>) t2e._2;
                    return Tuple2.of(t2e._1, right.value);
                }
            }
        };
    }

    /**
     * {@code Kleisli} models composable operations that return a {@code StateR}.
     * @param <S>       the state type
     * @param <A>       the input type
     * @param <B>       the value type of the returned {@code StateR} type
     */
    @FunctionalInterface
    interface Kleisli<S, A, B> {
        /**
         * Construct a {@code Kleisli} value from a function.
         * @param f         the function
         * @param <S>       the state type
         * @param <T>       the input type
         * @param <U>       the value type of the returned {@code StateR} value
         * @return          the new {@code Kleisli}
         */
        static <S, T, U> Kleisli<S, T, U> of(F<T, StateR<S, U>> f) {
            return f::apply;
        }

        /**
         * Apply this {@code Kleisli} operation
         * @param a         the input value
         * @return          the result of the operation
         */
        StateR<S, B> apply(A a);

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
     * This the SAM that implementations implement, typically via a lambda,
     * {@code runState} is essentially a function that can be applied to a
     * state {@code S} to yield a new state and a result.
     * @param state     the input state.
     * @return          the new state and a result
     */
    Tuple2<S, A> runState(S state);

    /**
     * Map a function over the state processor.
     * @param f         the function to be mapped
     * @param <B>       the return type of the function
     * @return          the new {@code StateR}
     */
    default <B> StateR<S, B> map(F<? super A, ? extends B> f) {
        return st -> {
            final Tuple2<S, A> t2 = runState(st);
            return t2.map2(f);
        };
    }

    /**
     * Combine a {@code StateR} that wraps a function with a {@code StateR} that wraps a value,
     * to yield a {@code StateR} that wraps the result of applying the function to the value.
     * Applicative apply operation (with args reversed).
     * @param sf        the {@code StateR} that wraps a function
     * @param <B>       the {@code StateR} that wraps a value
     * @return          a {@code StateR} that wraps the result of applying the function to the value
     */
    default <B> StateR<S, B> app(StateR<S, F<A, B>> sf) {
        return st -> {
            final Tuple2<S, F<A, B>> t2F = sf.runState(st);
            return this.runState(t2F._1).map2(t2F._2);
        };
    }

    /**
     * FlatMap a function over the state.
     * <p>
     * Compose a {@code StateR} with a function that yields another a {@code StateR},
     * to form a new a {@code StateR}.
     * A.k.a. monadic bind operation.
     * @param f         the function that yields a {@code StateR}
     * @param <B>       result value type
     * @return          a {@code StateR}
     */
    default <B> StateR<S, B> flatMap(F<A, StateR<S, B>> f) {
        return st -> {
            final Tuple2<S, A> t2 = runState(st);
            return f.apply(t2._2).runState(t2._1);
        };
    }

    /**
     * Return the state value yielded by running this state processor.
     * @param s         a state value to supply to the state processor
     * @return          the state value yielded by running this state processor
     */
    default S exec(S s) {
        return runState(s)._1;
    }

    /**
     * Return the state result yielded by running this state processor.
     * @param s         a state value to supply to the state processor
     * @return          the state result yielded by running this state processor
     */
    default A eval(S s) {
        return runState(s)._2;
    }
}

