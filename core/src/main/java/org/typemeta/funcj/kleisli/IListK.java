package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;

/**
 * {@code IListK} models composable operations that return an {@code IList}.
 * @param <T>       the input type
 * @param <U>       the value type of the returned {@code IList} type
 */
@FunctionalInterface
public interface IListK<T, U> {
    /**
     * Construct a {@code IListK} value from a function.
     * @param f         the function
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code Option} value
     * @return          the new {@code IListK}
     */
    static <T, U> IListK<T, U> of(Functions.F<T, IList<U>> f) {
        return f::apply;
    }

    /**
     * Run this {@code IListK} operation
     * @param t         the input value
     * @return          the result of the operation
     */
    IList<U> run(T t);

    /**
     * Compose this {@code IListK} with another by applying this one first,
     * then the other.
     * @param kUV       the {@code IListK} to be applied after this one
     * @param <V>       the second {@code IListK}'s return type
     * @return          the composed {@code IListK}
     */
    default <V> IListK<T, V> andThen(IListK<U, V> kUV) {
        return t -> run(t).flatMap(kUV::run);
    }

    /**
     * Compose this {@code IListK} with another by applying the other one first,
     * and then this one.
     * @param kST       the {@code IListK} to be applied after this one
     * @param <S>       the first {@code IListK}'s input type
     * @return          the composed {@code IListK}
     */
    default <S> IListK<S, U> compose(IListK<S, T> kST) {
        return s -> kST.run(s).flatMap(this::run);
    }

    /**
     * Compose this {@code IListK} with a function,
     * by applying this {@code IListK} first,
     * and then mapping the function over the result.
     * @param f         the function
     * @param <V>       the function return type
     * @return          the composed {@code IListK}
     */
    default <V> IListK<T, V> map(Functions.F<U, V> f) {
        return t -> run(t).map(f);
    }
}
