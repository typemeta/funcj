package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.control.State;
import org.typemeta.funcj.functions.Functions;

/**
 * {@code StateK} models composable operations that return a {@code State}.
 * @param <S>       the state type
 * @param <A>       the input type
 * @param <B>       the value type of the returned {@code State} type
 */
@FunctionalInterface
public interface StateK<S, A, B> {
    /**
     * Construct a {@code StateK} value from a function.
     * @param f         the function
     * @param <S>       the state type
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code State} value
     * @return          the new {@code StateK}
     */
    static <S, T, U> StateK<S, T, U> of(Functions.F<T, State<S, U>> f) {
        return f::apply;
    }

    /**
     * Apply this {@code StateK} operation
     * @param a         the input value
     * @return          the result of the operation
     */
    State<S, B> apply(A a);

    /**
     * Compose this {@code StateK} with another by applying this one first,
     * then the other.
     * @param kUV       the {@code StateK} to be applied after this one
     * @param <C>       the second {@code StateK}'s return type
     * @return          the composed {@code StateK}
     */
    default <C> StateK<S, A, C> andThen(StateK<S, B, C> kUV) {
        return a -> this.apply(a).flatMap(kUV::apply);
    }

    /**
     * Compose this {@code StateK} with another by applying the other one first,
     * and then this one.
     * @param kST       the {@code StateK} to be applied after this one
     * @param <C>       the first {@code StateK}'s input type
     * @return          the composed {@code StateK}
     */
    default <C> StateK<S, C, B> compose(StateK<S, C, A> kST) {
        return s -> kST.apply(s).flatMap(this::apply);
    }

    /**
     * Compose this {@code StateK} with a function,
     * by applying this {@code StateK} first,
     * and then mapping the function over the result.
     * @param f         the function
     * @param <C>       the function return type
     * @return          the composed {@code StateK}
     */
    default <C> StateK<S, A, C> map(Functions.F<B, C> f) {
        return t -> this.apply(t).map(f);
    }
}
