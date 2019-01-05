package org.typemeta.funcj.control;

import org.typemeta.funcj.algebra.Monoid;
import org.typemeta.funcj.functions.Functions;

public interface WriterM<W, A> {
    class Base<W, A> implements WriterM<W, A> {
        final Monoid<W> monoid;
        final A value;
        final W written;

        public Base(Monoid<W> monoid, A value, W written) {
            this.monoid = monoid;
            this.value = value;
            this.written = written;
        }

        @Override
        public Monoid<W> monoid() {
            return monoid;
        }

        @Override
        public A value() {
            return value;
        }

        @Override
        public W written() {
            return written;
        }
    }

    static <W, A> WriterM<W, A> of(Monoid<W> monoid, A value, W written) {
        return new Base<>(monoid, value, written);
    }

    static <W, A> WriterM<W, A> pure(Monoid<W> monoid, A value) {
        return new Base<>(monoid, value, monoid.zero());
    }

    /**
     * Kleisli models composable operations that return a {@code Reader}.
     * @param <W>       the input type of the returned {@code Reader} type
     * @param <A>       the input type of the returned {@code F} type
     * @param <B>       the return type of the returned {@code Reader} type
     */
    @FunctionalInterface
    interface Kleisli<W, A, B> {
        /**
         * Construct a {@code Kleisli} value from a writer.
         * @param wB        the writer
         * @param <W>       the writer (fixed) input type
         * @param <A>       the input type of the returned {@code F} type
         * @param <B>       the return type of the returned {@code F} type
         * @return          the new {@code Kleisli}
         */
        static <W, A, B> Kleisli<W, A, B> of(Functions.F<A, WriterM<W, B>> wB) {
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
        default <C> Kleisli<W, A, C> andThen(Kleisli<W, B, C> kBC) {
            return t -> apply(t).flatMap(kBC::apply);
        }

        /**
         * Compose this {@code Kleisli} with another by applying the other one first,
         * and then this one.
         * @param kCA       the {@code Kleisli} to be applied after this one
         * @param <C>       the first {@code Kleisli}'s input type
         * @return          the composed {@code Kleisli}
         */
        default <C> Kleisli<W, C, B> compose(Kleisli<W, C, A> kCA) {
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
        default <C> Kleisli<W, A, C> map(Functions.F<B, C> fC) {
            return t -> apply(t).map(fC);
        }
    }

    Monoid<W> monoid();

    A value();

    W written();

    default <B> WriterM<W, B> writer(B value, W written) {
        return new Base<>(monoid(), value, written);
    }

    default <B> WriterM<W, B> flatMap(Functions.F<A, WriterM<W, B>> fb) {
        final WriterM<W, B> wb = fb.apply(value());
        return writer(wb.value(), monoid().combine(written(), wb.written()));
    }

    default <B> WriterM<W, B> map(Functions.F<A, B> f) {
        return writer(f.apply(value()), written());
    }
}
