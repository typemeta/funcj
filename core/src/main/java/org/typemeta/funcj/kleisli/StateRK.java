package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.control.StateR;
import org.typemeta.funcj.functions.Functions;

/**
 * {@code Kleisli} models composable operations that return a {@code StateR}.
 * @param <S>       the state type
 * @param <A>       the input type
 * @param <B>       the value type of the returned {@code StateR} type
 */
@FunctionalInterface
public
interface StateRK<S, A, B> {
    /**
     * Construct a {@code Kleisli} value from a function.
     * @param f         the function
     * @param <S>       the state type
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code StateR} value
     * @return          the new {@code Kleisli}
     */
    static <S, T, U> StateRK<S, T, U> of(Functions.F<T, StateR<S, U>> f) {
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
    default <C> StateRK<S, A, C> andThen(StateRK<S, B, C> kUV) {
        return a -> this.apply(a).flatMap(kUV::apply);
    }

    /**
     * Compose this {@code Kleisli} with another by applying the other one first,
     * and then this one.
     * @param kST       the {@code Kleisli} to be applied after this one
     * @param <C>       the first {@code Kleisli}'s input type
     * @return          the composed {@code Kleisli}
     */
    default <C> StateRK<S, C, B> compose(StateRK<S, C, A> kST) {
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
    default <C> StateRK<S, A, C> map(Functions.F<B, C> f) {
        return t -> this.apply(t).map(f);
    }
}
