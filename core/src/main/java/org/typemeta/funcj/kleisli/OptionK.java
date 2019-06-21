package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.control.Option;
import org.typemeta.funcj.functions.Functions;

/**
 * Kleisli models composable operations that return a {@code Option}.
 * @param <T>       the input type
 * @param <U>       the value type of the returned {@code Option} type
 */
@FunctionalInterface
public
interface OptionK<T, U> {
    /**
     * Construct a {@code Kleisli} value from a function.
     * @param f         the function
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code Option} value
     * @return          the new {@code Kleisli}
     */
    static <T, U> OptionK<T, U> of(Functions.F<T, Option<U>> f) {
        return f::apply;
    }

    /**
     * Apply this {@code Kleisli} operation
     * @param t         the input value
     * @return          the result of the operation
     */
    Option<U> apply(T t);

    /**
     * Compose this {@code Kleisli} with another by applying this one first,
     * then the other.
     * @param kUV       the {@code Kleisli} to be applied after this one
     * @param <V>       the second {@code Kleisli}'s return type
     * @return          the composed {@code Kleisli}
     */
    default <V> OptionK<T, V> andThen(OptionK<U, V> kUV) {
        return t -> this.apply(t).flatMap(kUV::apply);
    }

    /**
     * Compose this {@code Kleisli} with another by applying the other one first,
     * and then this one.
     * @param kST       the {@code Kleisli} to be applied after this one
     * @param <S>       the first {@code Kleisli}'s input type
     * @return          the composed {@code Kleisli}
     */
    default <S> OptionK<S, U> compose(OptionK<S, T> kST) {
        return s -> kST.apply(s).flatMap(this::apply);
    }

    /**
     * Compose this {@code Kleisli} with a function,
     * by applying this {@code Kleisli} first,
     * and then mapping the function over the result.
     * @param f         the function
     * @param <V>       the function return type
     * @return          the composed {@code Kleisli}
     */
    default <V> OptionK<T, V> map(Functions.F<U, V> f) {
        return t -> this.apply(t).map(f);
    }
}
