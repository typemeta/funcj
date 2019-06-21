package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.control.ReaderM;
import org.typemeta.funcj.functions.Functions;

/**
 * Kleisli models composable operations that return a {@code Reader}.
 * @param <ENV>       the input type of the returned {@code Reader} type
 * @param <A>       the input type of the returned {@code F} type
 * @param <B>       the return type of the returned {@code Reader} type
 */
@FunctionalInterface
public
interface ReaderMK<ENV, A, B> {
    /**
     * Construct a {@code Kleisli} value from a reader.
     * @param rB        the reader
     * @param <ENV>     the reader (fixed) input type
     * @param <A>       the input type of the returned {@code F} type
     * @param <B>       the return type of the returned {@code F} type
     * @return          the new {@code Kleisli}
     */
    static <ENV, A, B> ReaderMK<ENV, A, B> of(Functions.F<A, ReaderM<ENV, B>> rB) {
        return rB::apply;
    }

    /**
     * Apply this {@code Kleisli} operation
     * @param a         the input value
     * @return          the result of the operation
     */
    ReaderM<ENV, B> apply(A a);

    /**
     * Compose this {@code Kleisli} with another by applying this one first,
     * then the other.
     * @param kBC       the {@code Kleisli} to be applied after this one
     * @param <C>       the second {@code Kleisli}'s return type
     * @return          the composed {@code Kleisli}
     */
    default <C> ReaderMK<ENV, A, C> andThen(ReaderMK<ENV, B, C> kBC) {
        return a -> apply(a).flatMap(kBC::apply);
    }

    /**
     * Compose this {@code Kleisli} with another by applying the other one first,
     * and then this one.
     * @param kCA       the {@code Kleisli} to be applied after this one
     * @param <C>       the first {@code Kleisli}'s input type
     * @return          the composed {@code Kleisli}
     */
    default <C> ReaderMK<ENV, C, B> compose(ReaderMK<ENV, C, A> kCA) {
        return c -> kCA.apply(c).flatMap(this::apply);
    }

    /**
     * Compose this {@code Kleisli} with a function,
     * by applying this {@code Kleisli} first,
     * and then mapping the function over the result.
     * @param fC         the function
     * @param <C>       the function return type
     * @return          the composed {@code Kleisli}
     */
    default <C> ReaderMK<ENV, A, C> map(Functions.F<B, C> fC) {
        return a -> apply(a).map(fC);
    }
}
