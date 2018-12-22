package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions.*;
import org.typemeta.funcj.functions.*;
import org.typemeta.funcj.util.Folds;

import java.util.*;
import java.util.stream.Stream;

/**
 * Union type of a successful result and an exception.
 * <p>
 * A {@code Try<T>} value is either
 * the sub-type {@code Try.Failure<T>} which wraps an exception, or
 * the sub-type {@code Try.Success<T>} which wraps a value of type T.
 * <p>
 * Try is biased towards the {@code Try.Success<T>}, meaning that map, apply and flatMap operate on the
 * {@code Success} value and bypass the {@code Failure} value.
 * <p>
 * Null values are not allowed.
 * @param <T>       the successful result type
 */
public interface Try<T> {

    /**
     * Create a {@code Success} value that wraps a successful result.
     * @param value     the successful result to be wrapped
     * @param <T>       the successful result type
     * @return          a {@code Success} value
     * @throws          NullPointerException if {@code value} is null
     */
    static <T> Try<T> success(T value) {
        return new Success<T>(value);
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param error     the error result
     * @param <T>       the successful result type
     * @return          a {@code Failure} value
     * @throws          NullPointerException if {@code error} is null
     */
    static <T> Try<T> failure(Throwable error) {
        return new Failure<T>(error);
    }

    /**
     * Create a {@code Try} value from a function which either yields a result or throws.
     * @param f         the function which may throw
     * @param <T>       the successful result type
     * @return          a {@code Try} value which wraps the function result
     */
    static <T> Try<T> of(FunctionsEx.F0<T> f) {
        try {
            return new Success<T>(f.apply());
        } catch (Exception ex) {
            return new Failure<T>(ex);
        }
    }

    /**
     * Applicative function application.
     * @param tf        the function wrapped in a {@code Try}
     * @param ta        the function argument wrapped in a {@code Try}
     * @param <A>       the function argument type
     * @param <B>       the function return type
     * @return          the result of applying the function to the argument, wrapped in a {@code Try}
     */
    static <A, B> Try<B> ap(Try<F<A, B>> tf, Try<A> ta) {
        return ta.apply(tf);
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param lt        the list of values
     * @param f         the function to be applied to each value in the list
     * @param <T>       the type of list elements
     * @param <U>       the type wrapped by the {@code Try} returned by the function
     * @return          a {@code Try} which wraps an {@link IList} of values
     */
    static <T, U> Try<IList<U>> traverse(IList<T> lt, F<T, Try<U>> f) {
        return lt.foldLeft(
                (tlu, t) -> f.apply(t).apply(tlu.map(lu -> lu::add)),
                success(IList.nil())
        );
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param lt        the list of values
     * @param f         the function to be applied to each value in the list
     * @param <T>       the type of list elements
     * @param <U>       the type wrapped by the {@code Try} returned by the function
     * @return          a {@code Try} which wraps an {@link IList} of values
     */
    static <T, U> Try<List<U>> traverse(List<T> lt, F<T, Try<U>> f) {
        return Folds.foldLeft(
                (tlt, t) -> f.apply(t).apply(tlt.map(lu -> u -> {lu.add(u); return lu;})),
                success(new ArrayList<>(lt.size())),
                lt
        );
    }

    /**
     * Variation of {@link Try#sequence(IList)} for a {@link List}.
     * @param ltt       the list of {@code Try} values
     * @param <T>       the value type of the {@code Try}s in the stream
     * @return          a {@code Try} which wraps an {@link Stream} of values
     */
    static <T> Try<List<T>> sequence(List<Try<T>> ltt) {
        return Folds.foldRight(
                (tt, tlt) -> tt.apply(tlt.map(lt -> t -> {lt.add(t); return lt;})),
                success(new ArrayList<>(ltt.size())),
                ltt
        );
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code Try} into a {@code Try} of an {@code IList},
     * by composing each consecutive {@code Try} using the {@link Try#apply(Try)} method.
     * @param ltt       the list of {@code Try} values
     * @param <T>       the value type of the {@code Try}s in the list
     * @return          a {@code Try} which wraps an {@link IList} of values
     */
    static <T> Try<IList<T>> sequence(IList<Try<T>> ltt) {
        return ltt.foldRight(
            (tt, tlt) -> tt.apply(tlt.map(lt -> lt::add)),
            success(IList.nil())
        );
    }

    /**
     * Variation of {@link Try#sequence(IList)} for {@link Stream}.
     * @param stt       the stream of {@code Try} values
     * @param <T>       the value type of the {@code Try}s in the stream
     * @return          a {@code Try} which wraps an {@link Stream} of values
     */
    static <T> Try<Stream<T>> sequence(Stream<Try<T>> stt) {
        final Iterator<Try<T>> iter = stt.iterator();
        Try<IList<T>> tlt = success(IList.nil());
        while (iter.hasNext()) {
            final Try<T> tt = iter.next();
            tlt = tt.apply(tlt.map(lt -> lt::add));
        }
        return tlt.map(IList::stream);
    }

    /**
     * Repeatedly call the function {@code f} until it returns {@code Either.Right}.
     * <p>
     * Call the function {@code f} and if it returns a right value then return the wrapped value,
     * otherwise extract the value and call {@code f} again.
     * @param a         the starting value
     * @param f         the function
     * @param <A>       the starting value type
     * @param <B>       the final value type
     * @return          the final value
     */
    @SuppressWarnings("unchecked")
    static <A, B> Try<B> tailRecM(A a, F<A, Try<Either<A, B>>> f) {
        while (true) {
            final Try<Either<A, B>> te = f.apply(a);
            if (te instanceof Try.Failure) {
                return ((Try.Failure)te).cast();
            } else {
                final Try.Success<Either<A, B>> tse = (Try.Success<Either<A, B>>)te;
                if (tse.value instanceof Either.Left) {
                    final Either.Left<A, B> left = (Either.Left<A, B>) tse.value;
                    a = left.value;
                } else {
                    final Either.Right<A, B> right = (Either.Right<A, B>) tse.value;
                    return Try.success(right.value);
                }
            }
        }
    }

    /**
     * Kleisli models composable operations that return a {@code Try}.
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code Try} type
     */
    @FunctionalInterface
    interface Kleisli<T, U> {
        /**
         * Construct a {@code Kleisli} value from a function.
         * @param f         the function
         * @param <T>       the input type
         * @param <U>       the value type of the returned {@code Try} value
         * @return          the new {@code Kleisli}
         */
        static <T, U> Kleisli<T, U> of(F<T, Try<U>> f) {
            return f::apply;
        }

        /**
         * Apply this {@code Kleisli} operation
         * @param t         the input value
         * @return          the result of the operation
         */
        Try<U> apply(T t);

        /**
         * Compose this {@code Kleisli} with another by applying this one first,
         * then the other.
         * @param kUV       the {@code Kleisli} to be applied after this one
         * @param <V>       the second {@code Kleisli}'s return type
         * @return          the composed {@code Kleisli}
         */
        default <V> Kleisli<T, V> andThen(Kleisli<U, V> kUV) {
            return t -> apply(t).flatMap(kUV::apply);
        }

        /**
         * Compose this {@code Kleisli} with another by applying the other one first,
         * and then this one.
         * @param kST       the {@code Kleisli} to be applied after this one
         * @param <S>       the first {@code Kleisli}'s input type
         * @return          the composed {@code Kleisli}
         */
        default <S> Kleisli<S, U> compose(Kleisli<S, T> kST) {
            return s -> kST.apply(s).flatMap(this::apply);
        }

        /**
         * Compose this {@code Kleisli} with a function,
         * by applying this {@code Kleisli} first,
         * and then mapping the function over the result.
         * @param f         the function
         * @param <V>       the function return type
         * @return          the composed {@code Kleisli}
         */
        default <V> Kleisli<T, V> map(F<U, V> f) {
            return t -> apply(t).map(f);
        }
    }

    /**
     * Indicates if this is a {@code Success} value.
     * @return          true if this value is a {@code Success} value
     */
    boolean isSuccess();

    /**
     * Either return the wrapped value if it's a {@code Success},
     * otherwise return the given default value.
     * @param defaultValue value to be returned if this is a failure value.
     * @return          the success result value if it's a {@code Success},
     *                  otherwise return the given default value.
     */
    T getOrElse(T defaultValue);

    /**
     * Return the wrapped value if it's a {@code Success}, otherwise throw the result exception.
     * @return          the wrapped value if it's a {@code Success}
     * @throws          Throwable if the wrapped value is a {@code Failure}
     */
    T getOrThrow() throws Throwable;

    /**
     * Return the wrapped value if it's a {@code Success}, otherwise throw a RuntimeException.
     * @return          the wrapped value if it's a {@code Success}
     */
    T get();

    /**
     * Push the result to a {@link SideEffect.F}.
     * @param failF     the side-effect to be applied the {@code Failure} value
     * @param succF     the side-effect to be applied the {@code Success} value
     */
    void handle(SideEffect.F<Failure<T>> failF, SideEffect.F<Success<T>> succF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param failF     the function to be applied the {@code Failure} value
     * @param succF     the function to be applied the {@code Success} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R match(F<Failure<T>, ? extends R> failF, F<Success<T>, ? extends R> succF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param failF     the function to be applied the {@code Failure} value
     * @param succF     the function to be applied the {@code Success} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R fold(F<Throwable, ? extends R> failF, F<? super T, ? extends R> succF);

    /**
     * Functor function application.
     * If this value is a {@code Success} then apply the function to the value,
     * otherwise if this is a {@code Failure} then leave it untouched.
     * @param f         the function to be applied
     * @param <U>       the function return type
     * @return          a {@code Try} that wraps the function result, or the original failure
     */
    <U> Try<U> map(F<? super T, ? extends U> f);

    /**
     * Applicative function application (inverted).
     * If the {@code tf} parameter is a {@code Success} value and this is a {@code Success} value,
     * then apply the function wrapped in the {@code tf} to this.
     * @param tf        the function wrapped in a {@code Try}
     * @param <U>       the return type of function
     * @return          a {@code Try} wrapping the result of applying the function, or a {@code Failure} value
     */
    <U> Try<U> apply(Try<F<T, U>> tf);

    /**
     * Monadic bind/flatMap.
     * If this is a {@code Success} then apply the function to the value and return the result,
     * otherwise return the {@code Failure} result.
     * @param f         the function to be applied
     * @param <U>       the type parameter to the {@code Try} returned by the function
     * @return          the result of combining this value with the function {@code f}
     */
    <U> Try<U> flatMap(F<? super T, Try<U>> f);

    /**
     * Variant of flatMap which ignores this value.
     * @param f         the function to be invoked
     * @param <U>       the type parameter to the {@code Try} returned by the function
     * @return          the result of combining this value with the function {@code f}
     */
    default <U> Try<U> flatMap(F0<Try<U>> f) {
        return flatMap(u -> f.apply());
    }

    /**
     * Apply a side-effect operation to this value
     * If this value is a {@code Success} then apply the function to the value,
     * otherwise do nothing.
     * @param f         the function to be applied
     */
    void foreach(SideEffect.F<? super T> f);

    /**
     * Builder API for chaining together n {@code Try}s,
     * and applying an n-ary function at the end.
     * @param tb        the next {@code Try} value to chain
     * @param <U>       the successful result type for next {@code Try}
     * @return          the next builder
     */
    default <U> ApplyBuilder._2<T, U> and(Try<U> tb) {
        return new ApplyBuilder._2<T, U>(this, tb);
    }

    /**
     * Success type.
     * @param <T>       the successful result type
     */
    final class Success<T> implements Try<T> {

        public final T value;

        private Success(T value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (!(obj instanceof Success<?>)) {
                return false;
            } else {
                final Success<?> rhs = (Success<?>) obj;
                return value.equals(rhs.value);
            }
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return value;
        }

        @Override
        public T getOrThrow() {
            return value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void handle(SideEffect.F<Failure<T>> failF, SideEffect.F<Success<T>> succF) {
            succF.apply(this);
        }

        @Override
        public <R> R match(F<Failure<T>, ? extends R> failF, F<Success<T>, ? extends R> succF) {
            return succF.apply(this);
        }

        @Override
        public <R> R fold(F<Throwable, ? extends R> failF, F<? super T, ? extends R> succF) {
            return succF.apply(value);
        }

        @Override
        public <U> Try<U> map(F<? super T, ? extends U> f) {
            return success(f.apply(value));
        }

        @Override
        public <U> Try<U> apply(Try<F<T, U>> tf) {
            return tf.map(f -> f.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(F<? super T, Try<U>> f) {
            return f.apply(value);
        }

        @Override
        public void foreach(SideEffect.F<? super T> f) {
            f.apply(value);
        }
    }

    /**
     * Failure type
     * @param <T>       the successful result type
     */
    final class Failure<T> implements Try<T> {

        public final Throwable error;

        private Failure(Throwable error) {
            this.error = Objects.requireNonNull(error);
        }

        @Override
        public String toString() {
            return "Failure(" + error + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (!(obj instanceof Failure<?>)) {
                return false;
            } else {
                final Failure<?> rhs = (Failure<?>) obj;

                if (error.equals(rhs.error)) {
                    return true;
                } else if (!error.getClass().equals(rhs.error.getClass())) {
                    return false;
                }

                // In general the equals() method for Exception classes isn't implemented,
                // which means we get object equality. This is rarely useful so here
                // we instead compare the string representations.
                return error.toString().equals(rhs.error.toString());
            }
        }

        @Override
        public int hashCode() {
            return error.hashCode();
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T getOrThrow() throws Throwable {
            throw error;
        }

        @Override
        public T get() {
            throw new RuntimeException(error);
        }

        @Override
        public void handle(SideEffect.F<Failure<T>> failF, SideEffect.F<Success<T>> succF) {
            failF.apply(this);
        }

        @Override
        public <R> R match(F<Failure<T>, ? extends R> failF, F<Success<T>, ? extends R> succF) {
            return failF.apply(this);
        }

        @Override
        public <R> R fold(F<Throwable, ? extends R> failF, F<? super T, ? extends R> succF) {
            return failF.apply(error);
        }

        @Override
        public <U> Try<U> map(F<? super T, ? extends U> f) {
            return cast();
        }

        @Override
        public <U> Try<U> apply(Try<F<T, U>> tf) {
            return tf.match(
                    fail -> fail.cast(),
                    succ -> this.cast()
            );
        }

        @Override
        public <U> Try<U> flatMap(F<? super T, Try<U>> f) {
            return cast();
        }

        @Override
        public void foreach(SideEffect.F<? super T> f) {
        }

        @SuppressWarnings("unchecked")
        private <U> Try<U> cast() {
            return (Try<U>) this;
        }
    }

    class ApplyBuilder {
        public static class _2<A, B> {
            private final Try<A> ta;
            private final Try<B> tb;

            _2(Try<A> ta, Try<B> tb) {
                this.ta = ta;
                this.tb = tb;
            }

            public <R> Try<R> map(F<A, F<B, R>> f) {
                return tb.apply(ta.map(f));
            }

            public <R> Try<R> map(F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Try<C> tc) {
                return new _3<C>(tc);
            }

            public class _3<C> {
                private final Try<C> tc;

                private _3(Try<C> tc) {
                    this.tc = tc;
                }

                public <R> Try<R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), tc);
                }

                public <R> Try<R> map(F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Try<D> td) {
                    return new _4<D>(td);
                }

                public class _4<D> {
                    private final Try<D> td;

                    private _4(Try<D> td) {
                        this.td = td;
                    }

                    public <R> Try<R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), td);
                    }

                    public <R> Try<R> map(F4<A, B, C, D, R> f) {
                        return map(f.curry());
                    }

                    public <E> _5<E> and(Try<E> te) {
                        return new _5<E>(te);
                    }

                    public class _5<E> {
                        private final Try<E> te;

                        private _5(Try<E> te) {
                            this.te = te;
                        }

                        public <R> Try<R> map(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
                            return ap(_4.this.map(f), te);
                        }

                        public <R> Try<R> map(F5<A, B, C, D, E, R> f) {
                            return map(f.curry());
                        }

                        public <G> _6<G> and(Try<G> tg) {
                            return new _6<G>(tg);
                        }

                        public class _6<G> {
                            private final Try<G> tg;

                            private _6(Try<G> tg) {
                                this.tg = tg;
                            }

                            public <R> Try<R> map(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
                                return ap(_5.this.map(f), tg);
                            }

                            public <R> Try<R> map(F6<A, B, C, D, E, G, R> f) {
                                return map(f.curry());
                            }
                        }
                    }
                }
            }
        }
    }
}
