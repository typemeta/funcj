package org.javafp.util;

/**
 * Interfaces for composable functions which throw.
 */
public abstract class FunctionsEx {

    /**
     * Function of arity 0.
     * @param <R> return type
     */
    @FunctionalInterface
    public interface F0<R> {
        static <R> F0<R> of(F0<R> f) {
            return f;
        }

        static <R> F0<R> konst(R r) {
            return () -> r;
        }

        R apply() throws Exception;
    }

    /**
     * Function of arity 1.
     * @param <A> 1st argument type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface F<A, R> {
        static <A, R> F<A, R> of(F<A, R> f) {
            return f;
        }

        static <A> F<A, A> id() {
            return x -> x;
        }

        static <A, R> F<A, R> konst(R r) {
            return a -> r;
        }

        static <A, B, R> F<B, F<A, R>> flip(F<A, F<B, R>> f) {
            return b -> a -> f.apply(a).apply(b);
        }

        R apply(A a) throws Exception;

        default <T> F<T, R> compose(F<T, A> f) {
            return t -> this.apply(f.apply(t));
        }
    }

    /**
     * Function of arity 2.
     * @param <A> 1st argument type
     * @param <B> 2nd argument type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface F2<A, B, R> {
        static <A, B, R> F2<A, B, R> of(F2<A, B, R> f) {
            return f;
        }

        static <A, B, R> F<A, F<B, R>> curry(F2<A, B, R> f) {
            return f.curry();
        }

        static <A, B, R> F2<A, B, R> uncurry(F<A, F<B, R>> f) {
            return (a, b) -> f.apply(a).apply(b);
        }

        static <A, B> F2<A, B, A> first() {
            return (a, b) -> a;
        }

        static <A, B> F2<A, B, B> second() {
            return (a, b) -> b;
        }

        R apply(A a, B b) throws Exception;

        default F<B, R> partial(A a) {
            return b -> apply(a, b);
        }

        default F<A, F<B, R>> curry() {
            return a -> b -> apply(a, b);
        }

        default F2<B, A, R> flip() {
            return (b, a) -> apply(a, b);
        }
    }

    /**
     * Function of arity 3.
     * @param <A> 1st argument type
     * @param <B> 2nd argument type
     * @param <C> 3rd argument type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface F3<A, B, C, R> {
        static <A, B, C, R> F3<A, B, C, R>of(F3<A, B, C, R> f) {
            return f;
        }

        static <A, B, C, R> F<A, F<B, F<C, R>>> curry(F3<A, B, C, R> f) {
            return f.curry();
        }

        static <A, B, C, R> F3<A, B, C, R> uncurry(F<A, F<B, F<C, R>>> f) {
            return (a, b, c) -> f.apply(a).apply(b).apply(c);
        }

        R apply(A a, B b, C c) throws Exception;

        default F2<B, C, R> partial(A a) {
            return (b, c) -> apply(a, b, c);
        }

        default F<C, R> partial(A a, B b) {
            return c -> apply(a, b, c);
        }

        default F<A, F<B, F<C, R>>> curry() {
            return a -> b -> c -> apply(a, b, c);
        }
    }

    /**
     * Function of arity 4.
     * @param <A> 1st argument type
     * @param <B> 2nd argument type
     * @param <C> 3rd argument type
     * @param <D> 4th argument type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface F4<A, B, C, D, R> {
        static <A, B, C, D, R> F4<A, B, C, D, R> of(F4<A, B, C, D, R> f) {
            return f;
        }

        static <A, B, C, D, R> F<A, F<B, F<C, F<D, R>>>> curry(F4<A, B, C, D, R> f) {
            return f.curry();
        }

        static <A, B, C, D, R> F4<A, B, C, D, R> uncurry(F<A, F<B, F<C, F<D, R>>>> f) {
            return (a, b, c, d) -> f.apply(a).apply(b).apply(c).apply(d);
        }

        R apply(A a, B b, C c, D d) throws Exception;

        default F3<B, C, D, R> partial(A a) {
            return (b, c, d) -> apply(a, b, c, d);
        }

        default F2<C, D, R> partial(A a, B b) {
            return (c, d) -> apply(a, b, c, d);
        }

        default F<D, R> partial(A a, B b, C c) {
            return d -> apply(a, b, c, d);
        }

        default F<A, F<B, F<C, F<D, R>>>> curry() {
            return a -> b -> c -> d -> apply(a, b, c, d);
        }
    }

    /**
     * Function of arity 5.
     * @param <A> 1st argument type
     * @param <B> 2nd argument type
     * @param <C> 3rd argument type
     * @param <D> 4th argument type
     * @param <E> 5th argument type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface F5<A, B, C, D, E, R> {
        static <A, B, C, D, E, R> F5<A, B, C, D, E, R> of(F5<A, B, C, D, E, R> f) {
            return f;
        }

        static <A, B, C, D, E, R> F<A, F<B, F<C, F<D, F<E, R>>>>> curry(F5<A, B, C, D, E, R> f) {
            return f.curry();
        }

        static <A, B, C, D, E, R> F5<A, B, C, D, E, R> uncurry(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
            return (a, b, c, d, e) -> f.apply(a).apply(b).apply(c).apply(d).apply(e);
        }

        R apply(A a, B b, C c, D d, E e) throws Exception;

        default F4<B, C, D, E, R> partial(A a) {
            return (b, c, d, e) -> apply(a, b, c, d, e);
        }

        default F3<C, D, E, R> partial(A a, B b) {
            return (c, d, e) -> apply(a, b, c, d, e);
        }

        default F2<D, E, R> partial(A a, B b, C c) {
            return (d, e) -> apply(a, b, c, d, e);
        }

        default F<E, R> partial(A a, B b, C c, D d) {
            return e -> apply(a, b, c, d, e);
        }

        default F<A, F<B, F<C, F<D, F<E, R>>>>> curry() {
            return a -> b -> c -> d -> e -> apply(a, b, c, d, e);
        }
    }

    /**
     * Function of arity 6.
     * @param <A> 1st argument type
     * @param <B> 2nd argument type
     * @param <C> 3rd argument type
     * @param <D> 4th argument type
     * @param <E> 5th argument type
     * @param <G> 6th argument type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface F6<A, B, C, D, E, G, R> {
        static <A, B, C, D, E, G, R> F6<A, B, C, D, E, G, R> of(F6<A, B, C, D, E, G, R> f) {
            return f;
        }

        static <A, B, C, D, E, G, R> F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> curry(F6<A, B, C, D, E, G, R> f) {
            return f.curry();
        }

        static <A, B, C, D, E, G, R> F6<A, B, C, D, E, G, R> uncurry(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
            return (a, b, c, d, e, g) -> f.apply(a).apply(b).apply(c).apply(d).apply(e).apply(g);
        }

        R apply(A a, B b, C c, D d, E e, G g) throws Exception;

        default F5<B, C, D, E, G, R> partial(A a) {
            return (b, c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        default F4<C, D, E, G, R> partial(A a, B b) {
            return (c, d, e, g) -> apply(a, b, c, d, e, g);
        }

        default F3<D, E, G, R> partial(A a, B b, C c) {
            return (d, e, g) -> apply(a, b, c, d, e, g);
        }

        default F2<E, G, R> partial(A a, B b, C c, D d) {
            return (e, g) -> apply(a, b, c, d, e, g);
        }

        default F<G, R> partial(A a, B b, C c, D d, E e) {
            return g -> apply(a, b, c, d, e, g);
        }

        default F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> curry() {
            return a -> b -> c -> d -> e -> g -> apply(a, b, c, d, e, g);
        }
    }

    /**
     * Unary operator interface.
     * @param <T> operand type
     */
    @FunctionalInterface
    public interface Op<T> extends F<T, T> {
        T apply(T t) throws Exception;
    }

    /**
     * Binary operator interface.
     * @param <T> operand type
     */
    @FunctionalInterface
    public interface Op2<T> extends F2<T, T, T> {
        T apply(T l, T r) throws Exception;
    }
}
