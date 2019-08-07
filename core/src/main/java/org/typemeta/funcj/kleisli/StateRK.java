package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.control.StateR;
import org.typemeta.funcj.functions.Functions;

/**
 * {@code StateRK} models composable operations that return a {@code StateR}.
 * @param <S>       the state type
 * @param <A>       the input type
 * @param <B>       the value type of the returned {@code StateR} type
 */
@FunctionalInterface
public interface StateRK<S, A, B> {
    /**
     * Construct a {@code StateRK} value from a function.
     * @param f         the function
     * @param <S>       the state type
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code StateR} value
     * @return          the new {@code StateRK}
     */
    static <S, T, U> StateRK<S, T, U> of(Functions.F<T, StateR<S, U>> f) {
        return f::apply;
    }

    /**
     * Apply this {@code StateRK} operation
     * @param a         the input value
     * @return          the result of the operation
     */
    StateR<S, B> apply(A a);

    /**
     * Compose this {@code StateRK} with another by applying this one first,
     * then the other.
     * @param kUV       the {@code StateRK} to be applied after this one
     * @param <C>       the second {@code StateRK}'s return type
     * @return          the composed {@code StateRK}
     */
    default <C> StateRK<S, A, C> andThen(StateRK<S, B, C> kUV) {
        return a -> this.apply(a).flatMap(kUV::apply);
    }

    /**
     * Compose this {@code StateRK} with another by applying the other one first,
     * and then this one.
     * @param kST       the {@code StateRK} to be applied after this one
     * @param <C>       the first {@code StateRK}'s input type
     * @return          the composed {@code StateRK}
     */
    default <C> StateRK<S, C, B> compose(StateRK<S, C, A> kST) {
        return s -> kST.apply(s).flatMap(this::apply);
    }

    /**
     * Compose this {@code StateRK} with a function,
     * by applying this {@code StateRK} first,
     * and then mapping the function over the result.
     * @param f         the function
     * @param <C>       the function return type
     * @return          the composed {@code StateRK}
     */
    default <C> StateRK<S, A, C> map(Functions.F<B, C> f) {
        return t -> this.apply(t).map(f);
    }
}
