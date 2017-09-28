package org.typemeta.funcj.util;

/**
 * Interfaces for composable functions.
 */
public abstract class Functions {

    /**
     * Function of arity 0.
     * @param <R>       the function return type
     */
    @FunctionalInterface
    public interface F0<R> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <R>       the function return type
         * @return          the function
         */
        static <R> F0<R> of(F0<R> f) {
            return f;
        }

        /**
         * Return a zero-argument constant function that always returns the same value.
         * @param r         the value the function always returns
         * @param <R>       the value type
         * @return          a zero-argument constant function that always returns the same value
         */
        static <R> F0<R> konst(R r) {
            return () -> r;
        }

        /**
         * Apply this function
         * @return          the result of applying this function
         */
        R apply();
    }

    /**
     * Function of arity 1.
     * Note: if the input type to {@code F} is fixed to type T then the result is a monad,
     * where pure = {@code konst} and bind = {@code flatMap}.
     * @param <A>       the function argument type
     * @param <R>       the function return type
     */
    @FunctionalInterface
    public interface F<A, R> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function argument type
         * @param <R>       the function return type
         * @return          the function
         */
        static <A, R> F<A, R> of(F<A, R> f) {
            return f;
        }

        /**
         * The identity function, that simply returns its argument.
         * @param <A>       input and output type of function
         * @return          the identity function
         */
        static <A> F<A, A> id() {
            return x -> x;
        }

        /**
         * The constant function, that always returns the same value, regardless of its argument
         * @param r         the value the constant function will return
         * @param <A>       the input type of the function
         * @param <R>       the type of the constant value {@code r}
         * @return          the constant function
         */
        static <A, R> F<A, R> konst(R r) {
            return a -> r;
        }

        /**
         * Convert a curried function by reversing the order of its arguments
         * @param f         the function to be flipped
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function's return type
         * @return          the flipped function
         */
        static <A, B, R> F<B, F<A, R>> flip(F<A, F<B, R>> f) {
            return b -> a -> f.apply(a).apply(b);
        }

        /**
         * Apply this function
         * @param a         the function argument
         * @return          the result of applying this function
         */
        R apply(A a);

        /**
         * Compose this function with another,
         * to create a function that first applies {@code f}
         * and then applies this function to the result.
         * @param f         the function to compose with
         * @param <T>       the argument type to {@code f}
         * @return          a function that first applies {@code f} and then applies this function to the result.
         */
        default <T> F<T, R> compose(F<? super T, ? extends A> f) {
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
        default <T> F<A, T> andThen(F<? super R, ? extends T> f) {
            return a -> f.apply(this.apply(a));
        }

        /**
         * Map a function over this oemn.
         * Essentially {@link F#compose} without the wildcard generic types.
         * @param f         the function to compose with
         * @param <T>       the argument type to {@code f}
         * @return          a function that first applies {@code f} and then applies this function to the result.
         */
        default <T> F<A, T> map(F<R, T> f) {
            return f.compose(this);
        }

        /**
         * Applicative function composition.
         * @param f         the function to compose with
         * @param <B>       the return type of the function returned by {code f}
         * @return          the composed function
         */
        default <B> F<A, B> app(F<A, F<R, B>> f) {
            return a -> f.apply(a).apply(apply(a));
        }

        /**
         * Monadic function composition.
         * @param f         the function to compose with
         * @param <B>       the return type of the function returned by {code f}
         * @return          the composed function
         */
        default <B> F<A, B> flatMap(F<R, F<A, B>> f) {
            return a -> f.apply(apply(a)).apply(a);
        }
    }

    /**
     * Function of arity 2.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <R>       the function return type
     */
    @FunctionalInterface
    public interface F2<A, B, R> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function return type
         * @return          the function
         */
        static <A, B, R> F2<A, B, R> of(F2<A, B, R> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the uncurried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function return type
         * @return          the curried function
         */
        static <A, B, R> F<A, F<B, R>> curry(F2<A, B, R> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the curried function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <R>       the function return type
         * @return          the uncurried function
         */
        static <A, B, R> F2<A, B, R> uncurry(F<A, F<B, R>> f) {
            return (a, b) -> f.apply(a).apply(b);
        }

        /**
         * A function that always returns its first argument.
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @return          a function that always returns its first argument.
         */
        static <A, B> F2<A, B, A> first() {
            return (a, b) -> a;
        }

        /**
         * A function that always returns its second argument.
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @return          a function that always returns its second argument.
         */
        static <A, B> F2<A, B, B> second() {
            return (a, b) -> b;
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @return          the result of applying this function
         */
        R apply(A a, B b);

        /**
         * Partially apply this function.
         * @param a         the value to partially apply this function to
         * @return          the partially applied function
         */
        default F<B, R> partial(A a) {
            return b -> apply(a, b);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, R>> curry() {
            return a -> b -> apply(a, b);
        }

        /**
         * Flip this function by reversing the order of its arguments.
         * @return          the flipped function
         */
        default F2<B, A, R> flip() {
            return (b, a) -> apply(a, b);
        }
    }

    /**
     * Function of arity 3.
     * @param <A>       the function's first argument type
     * @param <B>       the function's second argument type
     * @param <C>       the function's third argument type
     * @param <R>       the function's return type
     */
    @FunctionalInterface
    public interface F3<A, B, C, R> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <R>       the function's return type
         * @return          the function
         */
        static <A, B, C, R> F3<A, B, C, R> of(F3<A, B, C, R> f) {
            return f;
        }

        /**
         * Convert an uncurried function to its curried equivalent.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <R>       the function's return type
         * @return          the curried function
         */
        static <A, B, C, R> F<A, F<B, F<C, R>>> curry(F3<A, B, C, R> f) {
            return f.curry();
        }

        /**
         * Convert an curried function to its uncurried equivalent.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <R>       the function's return type
         * @return          the uncurried function
         */
        static <A, B, C, R> F3<A, B, C, R> uncurry(F<A, F<B, F<C, R>>> f) {
            return (a, b, c) -> f.apply(a).apply(b).apply(c);
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @return          the result of applying this function
         */
        R apply(A a, B b, C c);

        /**
         * Partially apply this function to one value.
         * @param a         the value
         * @return          the partially applied function
         */
        default F2<B, C, R> partial(A a) {
            return (b, c) -> apply(a, b, c);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F<C, R> partial(A a, B b) {
            return c -> apply(a, b, c);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, R>>> curry() {
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
     */
    @FunctionalInterface
    public interface F4<A, B, C, D, R> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <R>       the function's return type
         * @return          the function
         */
        static <A, B, C, D, R> F4<A, B, C, D, R> of(F4<A, B, C, D, R> f) {
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
         * @return          the curried function
         */
        static <A, B, C, D, R> F<A, F<B, F<C, F<D, R>>>> curry(F4<A, B, C, D, R> f) {
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
         * @return          the uncurried function
         */
        static <A, B, C, D, R> F4<A, B, C, D, R> uncurry(F<A, F<B, F<C, F<D, R>>>> f) {
            return (a, b, c, d) -> f.apply(a).apply(b).apply(c).apply(d);
        }

        /**
         * Apply this function.
         * @param a         the function's first argument
         * @param b         the function's second argument
         * @param c         the function's third argument
         * @param d         the function's fourth argument
         * @return          the result of applying this function
         */
        R apply(A a, B b, C c, D d);

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F3<B, C, D, R> partial(A a) {
            return (b, c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F2<C, D, R> partial(A a, B b) {
            return (c, d) -> apply(a, b, c, d);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F<D, R> partial(A a, B b, C c) {
            return d -> apply(a, b, c, d);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, R>>>> curry() {
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
     */
    @FunctionalInterface
    public interface F5<A, B, C, D, E, R> {
        /**
         * Static constructor.
         * @param f         the function
         * @param <A>       the function's first argument type
         * @param <B>       the function's second argument type
         * @param <C>       the function's third argument type
         * @param <D>       the function's fourth argument type
         * @param <E>       the function's fifth argument type
         * @param <R>       the function's return type
         * @return          the function
         */
        static <A, B, C, D, E, R> F5<A, B, C, D, E, R> of(F5<A, B, C, D, E, R> f) {
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
         * @return          the curried function
         */
        static <A, B, C, D, E, R> F<A, F<B, F<C, F<D, F<E, R>>>>> curry(F5<A, B, C, D, E, R> f) {
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
         * @return          the uncurried function
         */
        static <A, B, C, D, E, R> F5<A, B, C, D, E, R> uncurry(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
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
         */
        R apply(A a, B b, C c, D d, E e);

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F4<B, C, D, E, R> partial(A a) {
            return (b, c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F3<C, D, E, R> partial(A a, B b) {
            return (c, d, e) -> apply(a, b, c, d, e);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F2<D, E, R> partial(A a, B b, C c) {
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
        default F<E, R> partial(A a, B b, C c, D d) {
            return e -> apply(a, b, c, d, e);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, R>>>>> curry() {
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
     */
    @FunctionalInterface
    public interface F6<A, B, C, D, E, G, R> {
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
         * @return          the function
         */
        static <A, B, C, D, E, G, R> F6<A, B, C, D, E, G, R> of(F6<A, B, C, D, E, G, R> f) {
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
         * @return          the curried function
         */
        static <A, B, C, D, E, G, R> F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> curry(F6<A, B, C, D, E, G, R> f) {
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
         * @return          the uncurried function
         */
        static <A, B, C, D, E, G, R> F6<A, B, C, D, E, G, R> uncurry(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
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
         */
        R apply(A a, B b, C c, D d, E e, G g);

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F5<B, C, D, E, G, R> partial(A a) {
            return (b, c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to two values.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F4<C, D, E, G, R> partial(A a, B b) {
            return (c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F3<D, E, G, R> partial(A a, B b, C c) {
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
        default F2<E, G, R> partial(A a, B b, C c, D d) {
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
        default F<G, R> partial(A a, B b, C c, D d, E e) {
            return g -> apply(a, b, c, d, e, g);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> curry() {
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
     */
    @FunctionalInterface
    public interface F7<A, B, C, D, E, G, H, R> {
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
         * @return          the function
         */
        static <A, B, C, D, E, G, H, R> F7<A, B, C, D, E, G, H, R> of(F7<A, B, C, D, E, G, H, R> f) {
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
         * @return          the curried function
         */
        static <A, B, C, D, E, G, H, R> F<A, F<B, F<C, F<D, F<E, F<G, F<H, R>>>>>>> curry(
                F7<A, B, C, D, E, G, H, R> f) {
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
         * @return          the uncurried function
         */
        static <A, B, C, D, E, G, H, R> F7<A, B, C, D, E, G, H, R> uncurry(
                F<A, F<B, F<C, F<D, F<E, F<G, F<H, R>>>>>>> f) {
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
         */
        R apply(A a, B b, C c, D d, E e, G g, H h);

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F6<B, C, D, E, G, H, R> partial(A a) {
            return (b, c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F5<C, D, E, G, H, R> partial(A a, B b) {
            return (c, d, e, g, h) -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F4<D, E, G, H, R> partial(A a, B b, C c) {
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
        default F3<E, G, H, R> partial(A a, B b, C c, D d) {
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
        default F2<G, H, R> partial(A a, B b, C c, D d, E e) {
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
        default F<H, R> partial(A a, B b, C c, D d, E e, G g) {
            return h -> apply(a, b, c, d, e, g, h);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, F<G, F<H, R>>>>>>> curry() {
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
     */
    @FunctionalInterface
    public interface F8<A, B, C, D, E, G, H, I, R> {
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
         * @return          the function
         */
        static <A, B, C, D, E, G, H, I, R> F8<A, B, C, D, E, G, H, I, R> of(F8<A, B, C, D, E, G, H, I, R> f) {
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
         * @return          the curried function
         */
        static <A, B, C, D, E, G, H, I, R> F<A, F<B, F<C, F<D, F<E, F<G, F<H, F<I, R>>>>>>>> curry(
                F8<A, B, C, D, E, G, H, I, R> f) {
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
         * @return          the uncurried function
         */
        static <A, B, C, D, E, G, H, I, R> F8<A, B, C, D, E, G, H, I, R> uncurry(
                F<A, F<B, F<C, F<D, F<E, F<G, F<H, F<I, R>>>>>>>> f) {
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
         */
        R apply(A a, B b, C c, D d, E e, G g, H h, I i);

        /**
         * Partially apply this function to one value.
         * @param a         the first value
         * @return          the partially applied function
         */
        default F7<B, C, D, E, G, H, I, R> partial(A a) {
            return (b, c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to two value.
         * @param a         the first value
         * @param b         the second value
         * @return          the partially applied function
         */
        default F6<C, D, E, G, H, I, R> partial(A a, B b) {
            return (c, d, e, g, h, i) -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Partially apply this function to three values.
         * @param a         the first value
         * @param b         the second value
         * @param c         the third value
         * @return          the partially applied function
         */
        default F5<D, E, G, H, I, R> partial(A a, B b, C c) {
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
        default F4<E, G, H, I, R> partial(A a, B b, C c, D d) {
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
        default F3<G, H, I, R> partial(A a, B b, C c, D d, E e) {
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
        default F2<H, I, R> partial(A a, B b, C c, D d, E e, G g) {
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
        default F<I, R> partial(A a, B b, C c, D d, E e, G g, H h) {
            return i -> apply(a, b, c, d, e, g, h, i);
        }

        /**
         * Convert this function to its curried equivalent.
         * @return          the curried equivalent of this function
         */
        default F<A, F<B, F<C, F<D, F<E, F<G, F<H, F<I, R>>>>>>>> curry() {
            return a -> b -> c -> d -> e -> g -> h -> i -> apply(a, b, c, d, e, g, h, i);
        }
    }

    /**
     * Unary operator function.
     * @param <T>       the operand type
     */
    @FunctionalInterface
    public interface Op<T> extends F<T, T> {
        static <T> Op<T> of(Op<T> op) {
            return op;
        }

        T apply(T t);
    }

    /**
     * Binary operator function.
     * @param <T>       the operand type
     */
    @FunctionalInterface
    public interface Op2<T> extends F2<T, T, T> {
        /**
         * Static constructor
         * @param op        the operator function
         * @param <T>       the operand type
         * @return          the operator function
         */
        static <T> Op2<T> of(Op2<T> op) {
            return op;
        }

        /**
         * Flip this function by reversing the order of its arguments.
         * @return          the flipped function
         */
        default Op2<T> flip() {
            return (b, a) -> apply(a, b);
        }
    }

    /**
     * Predicate function
     * @param <T>       the operand type
     */
    @FunctionalInterface
    public interface Predicate<T> extends F<T, Boolean> {
        /**
         * Static constructor
         * @param pr        the predicate function
         * @param <T>       the operand type
         * @return          the predicate function
         */
        static <T> Predicate<T> of(Predicate<T> pr) {
            return pr;
        }
    }
}
