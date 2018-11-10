package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.F;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * {@code Reader} is an implementation of the Reader monad.
 * It is essentially an enriched {@link Functions.F}.
 */
public interface Reader<A, R> {

    /**
     * A {@code Reader} that always returns the same value, regardless of its argument
     * @param r         the value the {@code Reader} will return
     * @param <A>       the input type of the {@code Reader}
     * @param <R>       the type of the constant value {@code r}
     * @return          the constant {@code Reader}
     */
    static <A, R> Reader<A, R> pure(R r) {
        return a -> r;
    }

    static <T> F<T, T> ask() {
        return t -> t;
    }

    /**
     * Applicative function application.
     * @param rf        the function wrapped in a {@code Reader}
     * @param rt        the function argument wrapped in a {@code Reader}
     * @param <A>       the function argument type
     * @param <T>       the function input type
     * @param <U>       the function return type
     * @return          the result of applying the function to the argument, wrapped in a {@code Try}
     */
    static <A, T, U> Reader<A, U> ap(Reader<A, F<T, U>> rf, Reader<A, T> rt) {
        return rt.app(rf);
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
    static <T, A, B> Reader<T, IList<B>> traverse(IList<A> la, F<A, Reader<T, B>> ffb) {
        return la.foldRight(
                (a, flb) -> ffb.apply(a).app(flb.map(l -> l::add)),
                pure(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code F}s into a {@code F} of an {@code IList},
     * by composing each consecutive {@code F}s using the {@link F#app} method.
     * @param lfu       the list of {@code F} values
     * @param <T>       the input type of the applicative function
     * @param <U>       the return type of the {@code F}s in the list
     * @return          a {@code Reader} which wraps an {@link IList} of values
     */
    static <T, U> Reader<T, IList<U>> sequence(IList<Reader<T, U>> lfu) {
        return lfu.foldRight(
                (fu, flu) -> fu.app(flu.map(l -> l::add)),
                pure(IList.nil())
        );
    }

    /**
     * Variation of {@link Reader#sequence(IList)} for {@link Stream}.
     * @param sfu       the stream of {@code F} values
     * @param <T>       the error type
     * @param <U>       the return type of the {@code F}s in the stream
     * @return          a {@code F} which wraps an {@link Stream} of values
     */
    static <T, U> Reader<T, Stream<U>> sequence(Stream<Reader<T, U>> sfu) {
        final Iterator<Reader<T, U>> iter = sfu.iterator();
        Reader<T, IList<U>> flu = pure(IList.nil());
        while (iter.hasNext()) {
            final Reader<T, U> fu = iter.next();
            flu = fu.app(flu.map(lt -> lt::add));
        }
        return flu.map(IList::stream);
    }

    /**
     * Kleisli models composable operations that return a {@code Reader}.
     * @param <T>       the input type of the returned {@code Reader} type
     * @param <U>       the input type of the returned {@code F} type
     * @param <V>       the return type of the returned {@code Reader} type
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
     * Compose this {@code Reader} with another,
     * to create a {@code Reader} that first applies {@code f}
     * and then applies this {@code Reader} to the result.
     * @param f         the {@code Reader} to compose with
     * @param <T>       the argument type to {@code f}
     * @return          a {@code Reader} that first applies {@code f} and then applies this to the result.
     */
    default <T> Reader<T, R> compose(Reader<? super T, ? extends A> f) {
        return t -> this.apply(f.apply(t));
    }

    /**
     * Compose this {@code Reader} with another,
     * to create a {@code Reader} that first applies this {@code Reader}
     * and then applies {@code f} to the result.
     * @param f         the function to compose with
     * @param <T>       the argument type to {@code f}
     * @return          a function that first applies this function and then applies {@code f} to the result.
     */
    default <T> Reader<A, T> andThen(Reader<? super R, ? extends T> f) {
        return a -> f.apply(this.apply(a));
    }

    /**
     * Map a function over this {@code Reader}.
     * Essentially {@link Reader#compose} without the wildcard generic types.
     * @param f         the function to compose with
     * @param <T>       the argument type to {@code f}
     * @return          a function that first applies {@code f} and then applies this function to the result.
     */
    default <T> Reader<A, T> map(F<R, T> f) {
        return a -> f.apply(apply(a));
    }

    /**
     * Applicative {@code Reader} composition.
     * @param rf        the {@code Reader} to compose with
     * @param <B>       the return type of the function returned by {@code f}
     * @return          the composed function
     */
    default <B> Reader<A, B> app(Reader<A, F<R, B>> rf) {
        return a -> rf.apply(a).apply(this.apply(a));
    }

    /**
     * Monadic function composition.
     * @param f         the function to compose with
     * @param <B>       the return type of the function returned by {@code f}
     * @return          the composed function
     */
    default <B> Reader<A, B> flatMap(F<R, Reader<A, B>> f) {
        return a -> f.apply(this.apply(a)).apply(a);
    }
}
