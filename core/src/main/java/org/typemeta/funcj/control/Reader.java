package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.F;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * The {@code Reader} class adds utility methods for using the function monad {@link F}
 * as a Reader monad.
 */
public interface Reader<A, R> {

    /**
     * The constant function, that always returns the same value, regardless of its argument
     * @param r         the value the constant function will return
     * @param <A>       the input type of the function
     * @param <R>       the type of the constant value {@code r}
     * @return          the constant function
     */
    static <A, R> Reader<A, R> pure(R r) {
        return a -> r;
    }

    static <T> F<T, T> ask() {
        return t -> t;
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param la        the list of values
     * @param ffb       the function to be applied to each value in the list
     * @param <T>       the input type of the applicative function
     * @param <A>       the type of list elements
     * @param <B>       the return type of the {@code F} returned by the function
     * @return          an {@code F} which wraps an {@link IList} of values
     */
    static <T, A, B> F<T, IList<B>> traverse(IList<A> la, F<A, F<T, B>> ffb) {
        return la.foldRight(
                (a, flb) -> ffb.apply(a).app(flb.map(l -> l::add)),
                F.konst(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code F}s into a {@code F} of an {@code IList},
     * by composing each consecutive {@code F}s using the {@link F#app(F)} method.
     * @param lfu       the list of {@code F} values
     * @param <T>       the input type of the applicative function
     * @param <U>       the return type of the {@code F}s in the list
     * @return
     */
    static <T, U> F<T, IList<U>> sequence(IList<? extends F<T, U>> lfu) {
        return lfu.foldRight(
                (fu, flu) -> fu.app(flu.map(l -> l::add)),
                F.konst(IList.nil())
        );
    }

    /**
     * Variation of {@link Reader#sequence(IList)} for {@link Stream}.
     * @param sfu       the stream of {@code F} values
     * @param <T>       the error type
     * @param <U>       the return type of the {@code F}s in the stream
     * @return          a {@code F} which wraps an {@link Stream} of values
     */
    static <T, U> F<T, Stream<U>> sequence(Stream<F<T, U>> sfu) {
        final Iterator<F<T, U>> iter = sfu.iterator();
        F<T, IList<U>> flu = F.konst(IList.nil());
        while (iter.hasNext()) {
            final F<T, U> fu = iter.next();
            flu = fu.app(flu.map(lt -> lt::add));
        }
        return flu.map(IList::stream);
    }

    /**
     * Kleisli models composable operations that return a {@code F}.
     * @param <T>       the function (fixed) input type
     * @param <U>       the input type of the returned {@code F} type
     * @param <V>       the return type of the returned {@code F} type
     */

    @FunctionalInterface
    interface Kleisli<T, U, V> {
        /**
         * Construct a {@code Kleisli} value from a function.
         * @param f         the function
         * @param <T>       the function (fixed) input type
         * @param <U>       the input type of the returned {@code F} type
         * @param <V>       the return type of the returned {@code F} type
         * @return          the new {@code Kleisli}
         */
        static <T, U, V> Kleisli<T, U, V> of(F<U, Reader<T, V>> f) {
            return f::apply;
        }

        /**
         * Apply this {@code Kleisli} operation
         * @param t         the input value
         * @return          the result of the operation
         */
        Reader<T, V> apply(U t);

        /**
         * Compose this {@code Kleisli} with another by applying this one first,
         * then the other.
         * @param kVW       the {@code Kleisli} to be applied after this one
         * @param <W>       the second {@code Kleisli}'s return type
         * @return          the composed {@code Kleisli}
         */
        default <W> Kleisli<T, U, W> andThen(Kleisli<T, V, W> kVW) {
            return t -> apply(t).flatMap(kVW::apply);
        }

        /**
         * Compose this {@code Kleisli} with another by applying the other one first,
         * and then this one.
         * @param kSU       the {@code Kleisli} to be applied after this one
         * @param <S>       the first {@code Kleisli}'s input type
         * @return          the composed {@code Kleisli}
         */
        default <S> Kleisli<T, S, V> compose(Kleisli<T, S, U> kSU) {
            return s -> kSU.apply(s).flatMap(this::apply);
        }

        /**
         * Compose this {@code Kleisli} with a function,
         * by applying this {@code Kleisli} first,
         * and then mapping the function over the result.
         * @param f         the function
         * @param <W>       the function return type
         * @return          the composed {@code Kleisli}
         */
        default <W> Kleisli<T, U, W> map(F<V, W> f) {
            return t -> apply(t).map(f);
        }
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
    default <T> Reader<T, R> compose(Reader<? super T, ? extends A> f) {
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
    default <T> Reader<A, T> andThen(Reader<? super R, ? extends T> f) {
        return a -> f.apply(this.apply(a));
    }

    /**
     * Map a function over this oemn.
     * Essentially {@link Reader#compose} without the wildcard generic types.
     * @param f         the function to compose with
     * @param <T>       the argument type to {@code f}
     * @return          a function that first applies {@code f} and then applies this function to the result.
     */
    default <T> Reader<A, T> map(F<R, T> f) {
        return a -> f.apply(apply(a));
    }

    /**
     * Applicative function composition.
     * @param f         the function to compose with
     * @param <B>       the return type of the function returned by {code f}
     * @return          the composed function
     */
    default <B> Reader<A, B> app(F<A, Reader<R, B>> f) {
        return a -> f.apply(a).apply(this.apply(a));
    }

    /**
     * Monadic function composition.
     * @param f         the function to compose with
     * @param <B>       the return type of the function returned by {code f}
     * @return          the composed function
     */
    default <B> Reader<A, B> flatMap(F<R, Reader<A, B>> f) {
        return a -> f.apply(this.apply(a)).apply(a);
    }
}
