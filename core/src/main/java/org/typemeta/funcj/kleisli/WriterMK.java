package org.typemeta.funcj.kleisli;

import org.typemeta.funcj.control.WriterM;
import org.typemeta.funcj.functions.Functions;

/**
 * Kleisli models composable operations that return a {@code Reader}.
 * @param <W>       the input type of the returned {@code Reader} type
 * @param <A>       the input type of the returned {@code F} type
 * @param <B>       the return type of the returned {@code Reader} type
 */
@FunctionalInterface
public interface WriterMK<W, A, B> {
    /**
     * Construct a {@code Kleisli} value from a writer.
     * @param wB        the writer
     * @param <W>       the writer (fixed) input type
     * @param <A>       the input type of the returned {@code F} type
     * @param <B>       the return type of the returned {@code F} type
     * @return          the new {@code Kleisli}
     */
    static <W, A, B> WriterMK<W, A, B> of(Functions.F<A, WriterM<W, B>> wB) {
        return wB::apply;
    }

    /**
     * Apply this {@code Kleisli} operation
     * @param a         the input value
     * @return          the result of the operation
     */
    WriterM<W, B> apply(A a);

    /**
     * Compose this {@code Kleisli} with another by applying this one first,
     * then the other.
     * @param kBC       the {@code Kleisli} to be applied after this one
     * @param <C>       the second {@code Kleisli}'s return type
     * @return          the composed {@code Kleisli}
     */
    default <C> WriterMK<W, A, C> andThen(WriterMK<W, B, C> kBC) {
        return t -> apply(t).flatMap(kBC::apply);
    }

    /**
     * Compose this {@code Kleisli} with another by applying the other one first,
     * and then this one.
     * @param kCA       the {@code Kleisli} to be applied after this one
     * @param <C>       the first {@code Kleisli}'s input type
     * @return          the composed {@code Kleisli}
     */
    default <C> WriterMK<W, C, B> compose(WriterMK<W, C, A> kCA) {
        return s -> kCA.apply(s).flatMap(this::apply);
    }

    /**
     * Compose this {@code Kleisli} with a function,
     * by applying this {@code Kleisli} first,
     * and then mapping the function over the result.
     * @param fC        the function
     * @param <C>       the function return type
     * @return          the composed {@code Kleisli}
     */
    default <C> WriterMK<W, A, C> map(Functions.F<B, C> fC) {
        return t -> apply(t).map(fC);
    }
}
