package org.typemeta.funcj.functions;

import org.typemeta.funcj.tuples.Tuple2;
import org.typemeta.funcj.tuples.Tuple3;

/**
 * Interfaces for composable functions which throw a specific exception type.
 */
public abstract class FunctionsGenEx {

    /**
     * Function of arity 0.
     * @param <R>       the function return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F0<R, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <R>       the function return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <R, X extends Exception> F0<R, X> of(F0<R, X> f) {
            return f;
        }

        /**
         * Return a zero-argument constant function that always returns the same value.
         * @param r         the value the function always returns
         * @param <R>       the value type
         * @param <X>       the exception type
         * @return          a zero-argument constant function that always returns the same value
         */
        static <R, X extends Exception> F0<R, X> konst(R r) {
            return () -> r;
        }

        /**
         * Apply this function
         * @return          the result of applying this function
         * @throws X        the exception
         */
        R apply() throws X;
    }

    /**
     * Function of arity 1.
     * Note: if the input type to {@code F} is fixed to type T then the result is a monad,
     * where pure = {@code konst} and bind = {@code flatMap}.
     * @param <A>       the function argument type
     * @param <R>       the function return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F<A, R, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function argument type
         * @param <R>       the function return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, R, X extends Exception> F<A, R, X> of(F<A, R, X> f) {
            return f;
        }

        /**
         * Convert a curried function by reversing the order of its arguments
         * @param f         the function to be flipped
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the flipped function
         */
        static <A, B, R, X extends Exception> F<B, F<A, R, X>, X> flip(F<A, F<B, R, X>, X> f) {
            return b -> a -> f.apply(a).apply(b);
        }

        /**
         * Apply this function
         * @param a         the function argument
         * @return          the result of applying this function
         * @throws X        the exception
         */
        R apply(A a) throws X;

        /**
         * Compose this function with another,
         * to create a function that first applies {@code f}
         * and then applies this function to the result.
         * @param f         the function to compose with
         * @param <T>       the argument type to {@code f}
         * @return          a function that first applies {@code f} and then applies this function to the result.
         */
        default <T> F<T, R, X> compose(F<? super T, ? extends A, X> f) {
            return t -> this.apply(f.apply(t));
        }

        /**
         * Compose this function with another,
         * to create a function that first applies this function
         * and then applies {@code f} to the result.
         * @param f         the function to compose with
         * @param <T>       the argument type to {@code f}
         * @return          a function that first applies this function and then applies {@code f} to the result.
         */
        default <T> F<A, T, X> andThen(F<? super R, ? extends T, X> f) {
            return a -> f.apply(this.apply(a));
        }

        /**
         * Map a function over this oemn.
         * Essentially {@link F#compose} without the wildcard generic types.
         * @param f         the function to compose with
         * @param <T>       the argument type to {@code f}
         * @return          a function that first applies {@code f} and then applies this function to the result.
         */
        default <T> F<A, T, X> map(F<R, T, X> f) {
            return f.compose(this);
        }

        /**
         * Applicative function composition.
         * @param f         the function to compose with
         * @param <B>       the return type of the function returned by {code f}
         * @return          the composed function
         */
        default <B> F<A, B, X> app(F<A, F<R, B, X>, X> f) {
            return a -> f.apply(a).apply(apply(a));
        }

        /**
         * Monadic function composition.
         * @param f         the function to compose with
         * @param <B>       the return type of the function returned by {code f}
         * @return          the composed function
         */
        default <B> F<A, B, X> flatMap(F<R, F<A, B, X>, X> f) {
            return a -> f.apply(apply(a)).apply(a);
        }
    }

    /**
     * Function of arity 2.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <R>       the function return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F2<A, B, R, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, R, X extends Exception> F2<A, B, R, X> of(F2<A, B, R, X> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function return type
         * @param <X>       the exception type
         * @return          the curried function
         */
        static <A, B, R, X extends Exception> F<A, F<B, R, X>, X> curry(F2<A, B, R, X> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the curried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function return type
         * @param <X>       the exception type
         * @return          the uncurried function
         */
        static <A, B, R, X extends Exception> F2<A, B, R, X> uncurry(F<A, F<B, R, X>, X> f) {
            return (a, b) -> f.apply(a).apply(b);
        }

        /**
         * A function that always returns its first argument.
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <X>       the exception type
         * @return          a function that always returns its first argument.
         */
        static <A, B, X extends Exception> F2<A, B, A, X> first() {
            return (a, b) -> a;
        }

        /**
         * A function that always returns its second argument.
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <X>       the exception type
         * @return          a function that always returns its second argument.
         */
        static <A, B, X extends Exception> F2<A, B, B, X> second() {
            return (a, b) -> b;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @return          the result of applying this function
         * @throws X        the exception
         */
        R apply(A a, B b) throws X;

        /**
         * Apply this function to the values withing the supplied {@code Tuple2}
         * @param t2        the {@code Tuple2}
         * @return          the result of applying this function
         * @throws X        the exception
         */
        default R apply(Tuple2<A, B> t2) throws X {
            return apply(t2._1, t2._2);
        }

        /**
         * Partially apply this function.
         * @param a         the value to partially apply this function to
         * @return          the partially applied function
         */
        default F<B, R, X> partial(A a) {
            return b -> apply(a, b);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, R, X>, X> curry() {
            return a -> b -> apply(a, b);
        }

        /**
         * Flip this function by reversing the order of its arguments.
         * @return          the flipped function
         */
        default F2<B, A, R, X> flip() {
            return (b, a) -> apply(a, b);
        }
    }

    /**
     * Function of arity 3.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <R>       the function's return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F3<A, B, C, R, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, R, X extends Exception> F3<A, B, C, R, X> of(F3<A, B, C, R, X> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the curried function
         */
        static <A, B, C, R, X extends Exception> F<A, F<B, F<C, R, X>, X>, X> curry(F3<A, B, C, R, X> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the uncurried function
         */
        static <A, B, C, R, X extends Exception> F3<A, B, C, R, X> uncurry(F<A, F<B, F<C, R, X>, X>, X> f) {
            return (a, b, c) -> f.apply(a).apply(b).apply(c);
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @return          the result of applying this function
         * @throws X        the exception
         */
        R apply(A a, B b, C c) throws X;

        /**
         * Apply this function to the values withing the supplied {@code Tuple3}
         * @param t3        the {@code Tuple3}
         * @return          the result of applying this function
         * @throws X        the exception
         */
        default R apply(Tuple3<A, B, C> t3) throws Exception {
            return apply(t3._1, t3._2, t3._3);
        }

        /**
         * Partially apply this function to one value.
         * @param a         the value
         * @return          the partially applied function
         */
        default F2<B, C, R, X> partial(A a) {
            return (b, c) -> apply(a, b, c);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F<C, R, X> partial(A a, B b) {
            return c -> apply(a, b, c);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, R, X>, X>, X> curry() {
            return a -> b -> c -> apply(a, b, c);
        }
    }

    /**
     * Function of arity 4.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <R>       the function's return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F4<A, B, C, D, R, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, R, X extends Exception> F4<A, B, C, D, R, X> of(F4<A, B, C, D, R, X> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the curried function
         */
        static <A, B, C, D, R, X extends Exception> F<A, F<B, F<C, F<D, R, X>, X>, X>, X> curry(F4<A, B, C, D, R, X> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the uncurried function
         */
        static <A, B, C, D, R, X extends Exception> F4<A, B, C, D, R, X> uncurry(F<A, F<B, F<C, F<D, R, X>, X>, X>, X> f) {
            return (a, b, c, d) -> f.apply(a).apply(b).apply(c).apply(d);
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @return          the result of applying this function
         * @throws X        the exception
         */
        R apply(A a, B b, C c, D d) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F3<B, C, D, R, X> partial(A a) {
            return (b, c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F2<C, D, R, X> partial(A a, B b) {
            return (c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F<D, R, X> partial(A a, B b, C c) {
            return d -> apply(a, b, c, d);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, R, X>, X>, X>, X> curry() {
            return a -> b -> c -> d -> apply(a, b, c, d);
        }
    }

    /**
     * Function of arity 5.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <R>       the function's return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F5<A, B, C, D, E, R, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, R, X extends Exception> F5<A, B, C, D, E, R, X> of(
                F5<A, B, C, D, E, R, X> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the curried function
         */
        static <A, B, C, D, E, R, X extends Exception> F<A, F<B, F<C, F<D, F<E, R, X>, X>, X>, X>, X> curry(
                F5<A, B, C, D, E, R, X> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the uncurried function
         */
        static <A, B, C, D, E, R, X extends Exception> F5<A, B, C, D, E, R, X> uncurry(
                F<A, F<B, F<C, F<D, F<E, R, X>, X>, X>, X>, X> f) {
            return (a, b, c, d, e) -> f.apply(a).apply(b).apply(c).apply(d).apply(e);
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @return          the result of applying this function
         * @throws X         exception
         */
        R apply(A a, B b, C c, D d, E e) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F4<B, C, D, E, R, X> partial(A a) {
            return (b, c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F3<C, D, E, R, X> partial(A a, B b) {
            return (c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F2<D, E, R, X> partial(A a, B b, C c) {
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
        default F<E, R, X> partial(A a, B b, C c, D d) {
            return e -> apply(a, b, c, d, e);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, R, X>, X>, X>, X>, X> curry() {
            return a -> b -> c -> d -> e -> apply(a, b, c, d, e);
        }
    }

    /**
     * Function of arity 6.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <R>       the function's return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F6<A, B, C, D, E, G, R, X extends Exception> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, G, R, X extends Exception> F6<A, B, C, D, E, G, R, X> of(
                F6<A, B, C, D, E, G, R, X> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the curried function
         */
        static <A, B, C, D, E, G, R, X extends Exception> F<A, F<B, F<C, F<D, F<E, F<G, R, X>, X>, X>, X>, X>, X> curry(
                F6<A, B, C, D, E, G, R, X> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the uncurried function
         */
        static <A, B, C, D, E, G, R, X extends Exception> F6<A, B, C, D, E, G, R, X> uncurry(
                F<A, F<B, F<C, F<D, F<E, F<G, R, X>, X>, X>, X>, X>, X> f) {
            return (a, b, c, d, e, g) -> f.apply(a).apply(b).apply(c).apply(d).apply(e).apply(g);
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @param e         the function's fifth argument
         * @param g         the function's sixth argument
         * @return          the result of applying this function
         * @throws X         exception
         */
        R apply(A a, B b, C c, D d, E e, G g) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F5<B, C, D, E, G, R, X> partial(A a) {
            return (b, c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F4<C, D, E, G, R, X> partial(A a, B b) {
            return (c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F3<D, E, G, R, X> partial(A a, B b, C c) {
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
        default F2<E, G, R, X> partial(A a, B b, C c, D d) {
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
        default F<G, R, X> partial(A a, B b, C c, D d, E e) {
            return g -> apply(a, b, c, d, e, g);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, F<G, R, X>, X>, X>, X>, X>, X> curry() {
            return a -> b -> c -> d -> e -> g -> apply(a, b, c, d, e, g);
        }
    }

    /**
     * Function of arity 7.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <H>       the function's seventh argument type
     * @param <R>       the function's return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F7<A, B, C, D, E, G, H, R, X extends Exception> {
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
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, G, H, R, X extends Exception> F7<A, B, C, D, E, G, H, R, X> of(
                F7<A, B, C, D, E, G, H, R, X> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the curried function
         */
        static <A, B, C, D, E, G, H, R, X extends Exception> F<A, F<B, F<C, F<D, F<E, F<G, F<H, R, X>, X>, X>, X>, X>, X>, X> curry(
                F7<A, B, C, D, E, G, H, R, X> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the uncurried function
         */
        static <A, B, C, D, E, G, H, R, X extends Exception> F7<A, B, C, D, E, G, H, R, X> uncurry(
                F<A, F<B, F<C, F<D, F<E, F<G, F<H, R, X>, X>, X>, X>, X>, X>, X> f) {
            return (a, b, c, d, e, g, h) -> f.apply(a).apply(b).apply(c).apply(d).apply(e).apply(g).apply(h);
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
         * @return          the result of applying this function
         * @throws X         exception
         */
        R apply(A a, B b, C c, D d, E e, G g, H h) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F6<B, C, D, E, G, H, R, X> partial(A a) {
            return (b, c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F5<C, D, E, G, H, R, X> partial(A a, B b) {
            return (c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F4<D, E, G, H, R, X> partial(A a, B b, C c) {
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
        default F3<E, G, H, R, X> partial(A a, B b, C c, D d) {
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
        default F2<G, H, R, X> partial(A a, B b, C c, D d, E e) {
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
        default F<H, R, X> partial(A a, B b, C c, D d, E e, G g) {
            return h -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, F<G, F<H, R, X>, X>, X>, X>, X>, X>, X> curry() {
            return a -> b -> c -> d -> e -> g -> h -> apply(a, b, c, d, e, g, h);
        }
    }

    /**
     * Function of arity 8.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <D>       the function's fourth argument type
     * @param <E>       the function's fifth argument type
     * @param <G>       the function's sixth argument type
     * @param <H>       the function's seventh argument type
     * @param <I>       the function's eighth argument type
     * @param <R>       the function's return type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface F8<A, B, C, D, E, G, H, I, R, X extends Exception> {
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
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the function
         */
        static <A, B, C, D, E, G, H, I, R, X extends Exception> F8<A, B, C, D, E, G, H, I, R, X> of(
                F8<A, B, C, D, E, G, H, I, R, X> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @param <I>       the function's eighth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the curried function
         */
        static <A, B, C, D, E, G, H, I, R, X extends Exception> F<A, F<B, F<C, F<D, F<E, F<G, F<H, F<I, R, X>, X>, X>, X>, X>, X>, X>, X> curry(
                F8<A, B, C, D, E, G, H, I, R, X> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <G>       the function's sixth argument type
         * @param <H>       the function's seventh argument type
         * @param <I>       the function's eighth argument type
         * @param <R>       the function's return type
         * @param <X>       the exception type
         * @return          the uncurried function
         */
        static <A, B, C, D, E, G, H, I, R, X extends Exception> F8<A, B, C, D, E, G, H, I, R, X> uncurry(
                F<A, F<B, F<C, F<D, F<E, F<G, F<H, F<I, R, X>, X>, X>, X>, X>, X>, X>, X> f) {
            return (a, b, c, d, e, g, h, i) ->
                    f.apply(a).apply(b).apply(c).apply(d).apply(e).apply(g).apply(h).apply(i);
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
         * @return          the result of applying this function
         * @throws X         exception
         */
        R apply(A a, B b, C c, D d, E e, G g, H h, I i) throws X;

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F7<B, C, D, E, G, H, I, R, X> partial(A a) {
            return (b, c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F6<C, D, E, G, H, I, R, X> partial(A a, B b) {
            return (c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F5<D, E, G, H, I, R, X> partial(A a, B b, C c) {
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
        default F4<E, G, H, I, R, X> partial(A a, B b, C c, D d) {
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
        default F3<G, H, I, R, X> partial(A a, B b, C c, D d, E e) {
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
        default F2<H, I, R, X> partial(A a, B b, C c, D d, E e, G g) {
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
        default F<I, R, X> partial(A a, B b, C c, D d, E e, G g, H h) {
            return i -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, F<G, F<H, F<I, R, X>, X>, X>, X>, X>, X>, X>, X> curry() {
            return a -> b -> c -> d -> e -> g -> h -> i -> apply(a, b, c, d, e, g, h, i);
        }
    }

    /**
     * Unary operator function.
     * @param <T>       the operand type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface Op<T, X extends Exception> extends F<T, T, X> {
        /**
         * Static constructor
         * @param op        the operator function
         * @param <T>       the operand type
         * @param <X>       the exception type
         * @return          the operator function
         */
        static <T, X extends Exception> Op<T, X> of(Op<T, X> op) {
            return op;
        }
    }

    /**
     * Binary operator function.
     * @param <T>       the operand type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface Op2<T, X extends Exception> extends F2<T, T, T, X> {
        /**
         * Static constructor
         * @param op        the operator function
         * @param <T>       the operand type
         * @param <X>       the exception type
         * @return          the operator function
         */
        static <T, X extends Exception> Op2<T, X> of(Op2<T, X> op) {
            return op;
        }

        /**
         * Flip this function by reversing the order of its arguments.
         * @return          the flipped function
         */
        default Op2<T, X> flip() {
            return (b, a) -> apply(a, b);
        }
    }

    /**
     * Predicate function
     * @param <T>       the operand type
     * @param <X>       the exception type
     */
    @FunctionalInterface
    public interface Predicate<T, X extends Exception> extends F<T, Boolean, X> {
        /**
         * Static constructor
         * @param pr        the predicate function
         * @param <T>       the operand type
         * @param <X>       the exception type
         * @return          the predicate function
         */
        static <T, X extends Exception> Predicate<T, X> of(Predicate<T, X> pr) {
            return pr;
        }
    }
}
