package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions.*;
import org.typemeta.funcj.functions.*;
import org.typemeta.funcj.util.Folds;

import java.util.*;
import java.util.stream.Stream;

/**
 * Union type of a successful result and a list of errors.
 * <p>
 * A {@code Validated<T>} value is either
 * the sub-type {@code Validated.Failure<T>} which wraps an list of errors, or
 * the sub-type {@code Validated.Success<T>} which wraps a value of type T.
 * <p>
 * Null values are not allowed.
 * @param <E>       the error type
 * @param <T>       the successful result type
 */
public interface Validated<E, T> {
    /**
     * Create a {@code Success} value that wraps a successful result.
     * @param result successful result to be wrapped
     * @param <E>       the error type
     * @param <T>       the successful result type
     * @return          a success value
     * @throws          NullPointerException if {@code result} is null
     */
    static <E, T> Validated<E, T> success(T result) {
        return new Success<E, T>(result);
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param errors list of failures result to be wrapped
     * @param <E>       the error type
     * @param <T>       the successful result type
     * @return          a failure value
     * @throws          NullPointerException if {@code errors} is null
     */
    static <E, T> Validated<E, T> failure(IList<E> errors) {
        return new Failure<E, T>(errors);
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param errors list of failures result to be wrapped
     * @param <E>       the error type
     * @param <T>       the successful result type
     * @return          a failure value
     * @throws          NullPointerException if {@code errors} is null
     */
    static <E, T> Validated<E, T> failure(Iterable<E> errors) {
        return new Failure<E, T>(IList.ofIterable(errors));
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param error single failure result to be wrapped
     * @param <E>       the error type
     * @param <T>       the successful result type
     * @return          a failure value
     * @throws          NullPointerException if {@code error} is null
     */
    static <E, T> Validated<E, T> failure(E error) {
        return new Failure<E, T>(IList.of(error));
    }

    /**
     * Create a {@code Validated} value from a function which either yields a result or throws.
     * @param f  function which may throw
     * @param error exception to error translator
     * @param <E>       the error type
     * @param <T>       the successful result type
     * @param <X>       the exception type
     * @return          the {@code Validated} value which wraps the function result
     */
    @SuppressWarnings("unchecked")
    static <E, T, X extends Exception> Validated<E, T> of(FunctionsGenEx.F0<T, X> f, F<X, E> error) {
        try {
            return success(f.apply());
        } catch (Exception ex) {
            return failure(error.apply((X)ex));
        }
    }

    /**
     * Applicative function application.
     * @param vf        the function wrapped in a {@code Validated}
     * @param va        the function argument wrapped in a {@code Validated}
     * @param <E>       the error type
     * @param <A>       the function argument type
     * @param <B>       the function return type
     * @return          the result of applying the function to the argument, wrapped in a {@code Validated}
     */
    static <E, A, B> Validated<E, B> ap(Validated<E, F<A, B>> vf, Validated<E, A> va) {
        return va.apply(vf);
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param lt        the list of values
     * @param f         the function to be applied to each value in the list
     * @param <E>       the error type
     * @param <T>       the type of list elements
     * @param <U>       the type wrapped by the {@code Try} returned by the function
     * @return          a {@code Validated} which wraps an {@link IList} of values
     */
    static <E, T, U> Validated<E, IList<U>> traverse(IList<T> lt, F<T, Validated<E, U>> f) {
        return lt.foldLeft(
                (vlu, t) -> f.apply(t).apply(vlu.map(lu -> lu::add)),
                success(IList.nil())
        );
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param lt        the list of values
     * @param f         the function to be applied to each value in the list
     * @param <E>       the error type
     * @param <T>       the type of list elements
     * @param <U>       the type wrapped by the {@code Try} returned by the function
     * @return          a {@code Validated} which wraps an {@link List} of values
     */
    static <E, T, U> Validated<E, List<U>> traverse(List<T> lt, F<T, Validated<E, U>> f) {
        return Folds.foldLeft(
                (vlu, t) -> f.apply(t).apply(vlu.map(lu -> u -> {lu.add(u); return lu;})),
                success(new ArrayList<>(lt.size())),
                lt
        );
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code Validated} into a {@code Validated} of an {@code IList},
     * by composing each consecutive {@code Validated} using the {@link Validated#apply(Validated)} method.
     * @param lvt       the list of {@code Validated} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Validated}s in the list
     * @return          a {@code Validated} which wraps an {@link IList} of values
     */
    static <E, T> Validated<E, IList<T>> sequence(IList<Validated<E, T>> lvt) {
        return lvt.foldRight(
                (vt, vlt) -> vt.apply(vlt.map(lt -> lt::add)),
                success(IList.nil())
        );
    }

    /**
     * Variation of {@link Validated#sequence(IList)} for a {@link Stream}.
     * @param svt       the stream of {@code Validated} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Validated}s in the stream
     * @return          a {@code Validated} which wraps an {@link Stream} of values
     */
    static <E, T> Validated<E, Stream<T>> sequence(Stream<Validated<E, T>> svt) {
        final Iterator<Validated<E, T>> iter = svt.iterator();
        Validated<E, IList<T>> vlt = success(IList.nil());
        while (iter.hasNext()) {
            final Validated<E, T> vt = iter.next();
            vlt = vt.apply(vlt.map(lt -> lt::add));
        }
        return vlt.map(IList::stream);
    }

    /**
     * Variation of {@link Validated#sequence(IList)} for a {@link List}.
     * @param lvt       the list of {@code Validated} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Validated}s in the stream
     * @return          a {@code Validated} which wraps an {@link Stream} of values
     */
    static <E, T> Validated<E, List<T>> sequence(List<Validated<E, T>> lvt) {
        return Folds.foldRight(
                (vt, vlt) -> vt.apply(vlt.map(lt -> t -> {lt.add(t); return lt;})),
                success(new ArrayList<>(lvt.size())),
                lvt
        );
    }

    /**
     * Repeatedly call the function {@code f} until it returns {@code Either.Right}.
     * <p>
     * Call the function {@code f} and if it returns a right value then return the wrapped value,
     * otherwise extract the value and call {@code f} again.
     * @param a         the starting value
     * @param f         the function
     * @param <E>       the error type
     * @param <A>       the starting value type
     * @param <B>       the final value type
     * @return          the final value
     */
    @SuppressWarnings("unchecked")
    static <E, A, B> Validated<E, B> tailRecM(A a, F<A, Validated<E, Either<A, B>>> f) {
        while (true) {
            final Validated<E, Either<A, B>> ve = f.apply(a);
            if (ve instanceof Validated.Failure) {
                return ((Validated.Failure)ve).cast();
            } else {
                final Validated.Success<E, Either<A, B>> vse = (Validated.Success<E, Either<A, B>>)ve;
                if (vse.value instanceof Either.Left) {
                    final Either.Left<A, B> left = (Either.Left<A, B>) vse.value;
                    a = left.value;
                } else {
                    final Either.Right<A, B> right = (Either.Right<A, B>) vse.value;
                    return Validated.success(right.value);
                }
            }
        }
    }

    /**
     * Indicates if this is a {@code Success} value.
     * @return          the true if this value is a {@code Success} value
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
     * @throws          Exception if the wrapped value is a {@code Failure}
     */
    T getOrThrow() throws Exception;

    /**
     * Push the result to a {@link SideEffect.F}.
     * @param failF     the side-effect to be applied to the {@code Failure} value
     * @param succF     the side-effect to be applied to the {@code Success} value
     */
    void handle(SideEffect.F<Failure<E, T>> failF, SideEffect.F<Success<E, T>> succF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param failF     the function to be applied to the {@code Failure} value
     * @param succF     the function to be applied to the {@code Success} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R match(F<Failure<E, T>, ? extends R> failF, F<Success<E, T>, ? extends R> succF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param failF     the function to be applied to the {@code Failure} value
     * @param succF     the function to be applied to the {@code Success} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R fold(F<IList<E>, ? extends R> failF, F<? super T, ? extends R> succF);

    /**
     * Functor function application.
     * If this value is a {@code Success} then apply the function to the value,
     * otherwise if this is a {@code Failure} then leave it untouched.
     * @param f         the function to be applied
     * @param <U>       the function return type
     * @return          a {@code Validated} that wraps the function result, or the original failure
     */
    <U> Validated<E, U> map(F<T, U> f);

    /**
     * Applicative function application (inverted).
     * If the {@code vf} parameter is a {@code Success} value and this is a {@code Success} value,
     * then apply the function wrapped in the {@code tf} to this.
     * @param vf        the function wrapped in a {@code Validated}
     * @param <U>       the return type of function
     * @return          a {@code Validated} wrapping the result of applying the function, or a {@code Failure} value
     */
    <U> Validated<E, U> apply(Validated<E, F<T, U>> vf);

    /**
     * Return the result of applying a function to the {@code Success} value,
     * or return the {@code Failure} value.
     * @param f         the function to be applied to the {@code Success} value.
     * @param <U>       the type of the successful value on the function return type.
     * @return          the result of applying a function to the {@code Success} value,
     *                  or return the {@code Failure} value.
     */
    <U> Validated<E, U> andThen(F<T, Validated<E, U>> f);

    /**
     * Apply a side-effect operation to this value
     * If this value is a {@code Success} then apply the function to the value,
     * otherwise do nothing.
     * @param f         the function to be applied
     */
    void foreach(SideEffect.F<? super T> f);

    /**
     * Builder API for chaining together n {@code Validated}s,
     * and applying an n-ary function at the end.
     * @param vb        the next {@code Validated} value to chain
     * @param <U>       the successful result type for next {@code Validated}
     * @return          the next builder
     */
    default <U> ApplyBuilder._2<E, T, U> and(Validated<E, U> vb) {
        return new ApplyBuilder._2<E, T, U>(this, vb);
    }

    /**
     * Success type.
     * @param <E>       the error type
     * @param <T>       the successful result type
     */
    class Success<E, T> implements Validated<E, T> {
        public final T value;

        public Success(T value) {
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
            } else if (!(obj instanceof Success<?, ?>)) {
                return false;
            } else {
                final Success<?, ?> rhs = (Success<?, ?>) obj;
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
        public void handle(SideEffect.F<Failure<E, T>> failF, SideEffect.F<Success<E, T>> succF) {
            succF.apply(this);
        }

        @Override
        public <R> R fold(F<IList<E>, ? extends R> failF, F<? super T, ? extends R> succF) {
            return succF.apply(value);
        }

        @Override
        public <R> R match(F<Failure<E, T>, ? extends R> failF, F<Success<E, T>, ? extends R> succF) {
            return succF.apply(this);
        }

        @Override
        public <U> Validated<E, U> map(F<T, U> f) {
            return success(f.apply(value));
        }

        @Override
        public <U> Validated<E, U> apply(Validated<E, F<T, U>> vf) {
            return vf.map(f -> f.apply(value));
        }

        @Override
        public <U> Validated<E, U> andThen(F<T, Validated<E, U>> f) {
            return f.apply(value);
        }

        @Override
        public void foreach(SideEffect.F<? super T> f) {
            f.apply(value);
        }

        @Override
        public T getOrElse(T defaultValue) {
            return value;
        }

        @Override
        public T getOrThrow() throws Exception {
            return value;
        }
    }

    /**
     * Failure type.
     * @param <E>       the error type
     * @param <T>       the successful result type
     */
    class Failure<E, T> implements Validated<E, T> {
        public final IList<E> errors;

        public Failure(IList<E> errors) {
            this.errors = Objects.requireNonNull(errors);
        }

        @Override
        public String toString() {
            return "Failure(" + errors + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (!(obj instanceof Failure<?, ?>)) {
                return false;
            } else {
                final Failure<?, ?> rhs = (Failure<?, ?>) obj;
                return errors.equals(rhs.errors);
            }
        }

        @Override
        public int hashCode() {
            return errors.hashCode();
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public void handle(SideEffect.F<Failure<E, T>> failF, SideEffect.F<Success<E, T>> succF) {
            failF.apply(this);
        }

        @Override
        public <R> R fold(F<IList<E>, ? extends R> failF, F<? super T, ? extends R> succF) {
            return failF.apply(errors);
        }


        @Override
        public <R> R match(F<Failure<E, T>, ? extends R> failF, F<Success<E, T>, ? extends R> succF) {
            return failF.apply(this);
        }

        @Override
        public <U> Validated<E, U> map(F<T, U> f) {
            return cast();
        }

        @Override
        public <U> Validated<E, U> apply(Validated<E, F<T, U>> vf) {
            return cast();
        }

        @Override
        public <U> Validated<E, U> andThen(F<T, Validated<E, U>> f) {
            return cast();
        }

        @Override
        public void foreach(SideEffect.F<? super T> f) {
        }

        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T getOrThrow() throws Exception {
            throw new Exception("Validated.getOrThrow() called on a Validated.Failure value");
        }

        @SuppressWarnings("unchecked")
        public <U> Failure<E, U> cast() {
            return (Failure<E, U>) this;
        }
    }

    class ApplyBuilder {
        public static class _2<E, A, B> {
            private final Validated<E, A> va;
            private final Validated<E, B> vb;

            _2(Validated<E, A> va, Validated<E, B> vb) {
                this.va = va;
                this.vb = vb;
            }

            public <R> Validated<E, R> map(F<A, F<B, R>> f) {
                return vb.apply(va.map(f));
            }

            public <R> Validated<E, R> map(F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Validated<E, C> vc) {
                return new _3<C>(vc);
            }

            public class _3<C> {
                private final Validated<E, C> vc;

                private _3(Validated<E, C> vc) {
                    this.vc = vc;
                }

                public <R> Validated<E, R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), vc);
                }

                public <R> Validated<E, R> map(F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Validated<E, D> vd) {
                    return new _4<D>(vd);
                }

                public class _4<D> {
                    private final Validated<E, D> vd;

                    private _4(Validated<E, D> vd) {
                        this.vd = vd;
                    }

                    public <R> Validated<E, R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), vd);
                    }

                    public <R> Validated<E, R> map(F4<A, B, C, D, R> f) {
                        return map(f.curry());
                    }

                    public <G> _5<G> and(Validated<E, G> vg) {
                        return new _5<G>(vg);
                    }

                    public class _5<G> {
                        private final Validated<E, G> vg;

                        private _5(Validated<E, G> vg) {
                            this.vg = vg;
                        }

                        public <R> Validated<E, R> map(F<A, F<B, F<C, F<D, F<G, R>>>>> f) {
                            return ap(_4.this.map(f), vg);
                        }

                        public <R> Validated<E, R> map(F5<A, B, C, D, G, R> f) {
                            return map(f.curry());
                        }

                        public <H> _6<H> and(Validated<E, H> vg) {
                            return new _6<H>(vg);
                        }

                        public class _6<H> {
                            private final Validated<E, H> vh;

                            private _6(Validated<E, H> vh) {
                                this.vh = vh;
                            }

                            public <R> Validated<E, R> map(F<A, F<B, F<C, F<D, F<G, F<H, R>>>>>> f) {
                                return ap(_5.this.map(f), vh);
                            }

                            public <R> Validated<E, R> map(F6<A, B, C, D, G, H, R> f) {
                                return map(f.curry());
                            }
                        }
                    }
                }
            }
        }
    }
}
