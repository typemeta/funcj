package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.functions.Functions;

/**
 * {@code EitherK} models composable operations that return an {@code Either}.
 * @param <E>       the left-hand type
 * @param <T>       the input type
 * @param <U>       the value type of the returned {@code Either} type
 */
@FunctionalInterface
public interface EitherK<E, T, U> {
    /**
     * Construct a {@code EitherK} value from a function.
     * @param f         the function
     * @param <E>       the left-hand type
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code Either} value
     * @return          the new {@code EitherK}
     */
    static <E, T, U> EitherK<E, T, U> of(Functions.F<T, Either<E, U>> f) {
        return f::apply;
    }

    /**
     * Apply this {@code EitherK} operation
     * @param t         the input value
     * @return          the result of the operation
     */
    Either<E, U> apply(T t);

    /**
     * Compose this {@code EitherK} with another by applying this one first,
     * then the other.
     * @param kUV       the {@code EitherK} to be applied after this one
     * @param <V>       the second {@code EitherK}'s return type
     * @return          the composed {@code EitherK}
     */
    default <V> EitherK<E, T, V> andThen(EitherK<E, U, V> kUV) {
        return t -> this.apply(t).flatMap(kUV::apply);
    }

    /**
     * Compose this {@code EitherK} with another by applying the other one first,
     * and then this one.
     * @param kST       the {@code EitherK} to be applied after this one
     * @param <S>       the first {@code EitherK}'s input type
     * @return          the composed {@code EitherK}
     */
    default <S> EitherK<E, S, U> compose(EitherK<E, S, T> kST) {
        return s -> kST.apply(s).flatMap(this::apply);
    }

    /**
     * Compose this {@code EitherK} with a function,
     * by applying this {@code EitherK} first,
     * and then mapping the function over the result.
     * @param f         the function
     * @param <V>       the function return type
     * @return          the composed {@code EitherK}
     */
    default <V> EitherK<E, T, V> map(Functions.F<U, V> f) {
        return t -> this.apply(t).map(f);
    }
}
