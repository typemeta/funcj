package org.typemeta.funcj.functions;

import org.typemeta.funcj.tuples.*;

/**
 * Interfaces for composable functions that have no return type,
 * i.e. side-effects, but may throw {@code Exception}s.
 */
public abstract class SideEffectEx {

    /**
     * Side-effect of arity 0.
     */
    @FunctionalInterface
    public interface F0 {
        /**
         * Static constructor.
         * @param f         the function
         * @return          the function
         */
        static F0 of(F0 f) {
            return f;
        }

        /**
         * Apply this function
         */
        void apply() throws Exception;
    }

    /**
     * Side-effect of arity 1.
     * @param <A>       the function argument type
     */
    @FunctionalInterface
    public interface F<A> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function argument type
         * @return          the function
         */
        static <A> F<A> of(F<A> f) {
            return f;
        }

        /**
         * Apply this function
         * @param a         the function argument
         */
        void apply(A a) throws Exception;
    }

    /**
     * Side-effect of arity 2.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     */
    @FunctionalInterface
    public interface F2<A, B> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @return          the function
         */
        static <A, B> F2<A, B> of(F2<A, B> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         */
        void apply(A a, B b) throws Exception;

        /**
         * Partially apply this function.
         * @param a         the value to partially apply this function to
         * @return          the partially applied function
         */
        default F<B> partial(A a) {
            return b -> apply(a, b);
        }

        /**
         * Flip this function by reversing the order of its arguments.
         * @return          the flipped function
         */
        default F2<B, A> flip() {
            return (b, a) -> apply(a, b);
        }

        /**
         * Convert this function to one that operates on a {@link Tuple2}.
         * @return          a function that operates on a {@link Tuple2}
         */
        default F<Tuple2<A, B>> tupled() {
            return t2 -> apply(t2._1, t2._2);
        }
    }

    /**
     * Side-effect of arity 3.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     */
    @FunctionalInterface
    public interface F3<A, B, C> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @return          the function
         */
        static <A, B, C> F3<A, B, C> of(F3<A, B, C> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         */
        void apply(A a, B b, C c) throws Exception;

        /**
         * Partially apply this function to one value.
         * @param a         the value
         * @return          the partially applied function
         */
        default F2<B, C> partial(A a) {
            return (b, c) -> apply(a, b, c);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F<C> partial(A a, B b) {
            return c -> apply(a, b, c);
        }

        /**
         * Convert this function to one that operates on a {@link Tuple3}.
         * @return          a function that operates on a {@link Tuple3}
         */
        default F<Tuple3<A, B, C>> tupled() {
            return t3 -> apply(t3._1, t3._2, t3._3);
        }
    }

    /**
     * Side-effect of arity 4.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     */
    @FunctionalInterface
    public interface F4<A, B, C, D> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @return          the function
         */
        static <A, B, C, D> F4<A, B, C, D> of(F4<A, B, C, D> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         */
        void apply(A a, B b, C c, D d) throws Exception;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F3<B, C, D> partial(A a) {
            return (b, c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F2<C, D> partial(A a, B b) {
            return (c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F<D> partial(A a, B b, C c) {
            return d -> apply(a, b, c, d);
        }
    }

    /**
     * Side-effect of arity 5.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     */
    @FunctionalInterface
    public interface F5<A, B, C, D, E> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @return          the function
         */
        static <A, B, C, D, E> F5<A, B, C, D, E> of(F5<A, B, C, D, E> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         */
        void apply(A a, B b, C c, D d, E e) throws Exception;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F4<B, C, D, E> partial(A a) {
            return (b, c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F3<C, D, E> partial(A a, B b) {
            return (c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F2<D, E> partial(A a, B b, C c) {
            return (d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F<E> partial(A a, B b, C c, D d) {
            return e -> apply(a, b, c, d, e);
        }
    }

    /**
     * Side-effect of arity 6.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     */
    @FunctionalInterface
    public interface F6<A, B, C, D, E, G> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @return          the function
         */
        static <A, B, C, D, E, G> F6<A, B, C, D, E, G> of(F6<A, B, C, D, E, G> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @param g         the function's sixth argument
         */
        void apply(A a, B b, C c, D d, E e, G g) throws Exception;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F5<B, C, D, E, G> partial(A a) {
            return (b, c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F4<C, D, E, G> partial(A a, B b) {
            return (c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F3<D, E, G> partial(A a, B b, C c) {
            return (d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F2<E, G> partial(A a, B b, C c, D d) {
            return (e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to five values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @return          the partially applied function
         */
        default F<G> partial(A a, B b, C c, D d, E e) {
            return g -> apply(a, b, c, d, e, g);
        }
    }

    /**
     * Side-effect of arity 7.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <H>       the function's seventh argument type
     */
    @FunctionalInterface
    public interface F7<A, B, C, D, E, G, H> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @return          the function
         */
        static <A, B, C, D, E, G, H> F7<A, B, C, D, E, G, H> of(F7<A, B, C, D, E, G, H> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @param g         the function's sixth argument
         * @param h         the function's seventh argument
         */
        void apply(A a, B b, C c, D d, E e, G g, H h) throws Exception;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F6<B, C, D, E, G, H> partial(A a) {
            return (b, c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F5<C, D, E, G, H> partial(A a, B b) {
            return (c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F4<D, E, G, H> partial(A a, B b, C c) {
            return (d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F3<E, G, H> partial(A a, B b, C c, D d) {
            return (e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to five values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @return          the partially applied function
         */
        default F2<G, H> partial(A a, B b, C c, D d, E e) {
            return (g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to six values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @param g         the sixth value
         * @return          the partially applied function
         */
        default F<H> partial(A a, B b, C c, D d, E e, G g) {
            return h -> apply(a, b, c, d, e, g, h);
        }
    }

    /**
     * Side-effect of arity 8.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <H>       the function's seventh argument type
     * @param <I>       the function's eighth argument type
     */
    @FunctionalInterface
    public interface F8<A, B, C, D, E, G, H, I> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @param <I>       the function's eighth argument type
         * @return          the function
         */
        static <A, B, C, D, E, G, H, I> F8<A, B, C, D, E, G, H, I> of(F8<A, B, C, D, E, G, H, I> f) {
            return f;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @param g         the function's sixth argument
         * @param h         the function's seventh argument
         * @param i         the function's eighth argument
         */
        void apply(A a, B b, C c, D d, E e, G g, H h, I i) throws Exception;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F7<B, C, D, E, G, H, I> partial(A a) {
            return (b, c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F6<C, D, E, G, H, I> partial(A a, B b) {
            return (c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F5<D, E, G, H, I> partial(A a, B b, C c) {
            return (d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to four values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @return          the partially applied function
         */
        default F4<E, G, H, I> partial(A a, B b, C c, D d) {
            return (e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to five values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @return          the partially applied function
         */
        default F3<G, H, I> partial(A a, B b, C c, D d, E e) {
            return (g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to six values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @param g         the sixth value
         * @return          the partially applied function
         */
        default F2<H, I> partial(A a, B b, C c, D d, E e, G g) {
            return (h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to seven values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @param d         the fourth value
         * @param e         the fifth value
         * @param g         the sixth value
         * @param h         the seventh value
         * @return          the partially applied function
         */
        default F<I> partial(A a, B b, C c, D d, E e, G g, H h) {
            return i -> apply(a, b, c, d, e, g, h, i);
        }
    }
}
