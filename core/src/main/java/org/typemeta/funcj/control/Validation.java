package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions.*;
import org.typemeta.funcj.functions.*;
import org.typemeta.funcj.util.Folds;

import java.util.*;
import java.util.stream.Stream;

/**
 * Union type of a successfult result and a list of errors.
 * <p>
 * A {@code Validation<T>} value is either
 * the sub-type {@code Validation.Failure<T>} which wraps an list of errors, or
 * the sub-type {@code Validation.Success<T>} which wraps a value of type T.
 * <p>
 * Null values are not allowed.
 * @param <E>       the error type
 * @param <T>       the successful result type
 */
public interface Validation<E, T> {
    /**
     * Create a {@code Success} value that wraps a successful result.
     * @param result successful result to be wrapped
     * @param <E>       the error type
     * @param <T>       the successful result type
     * @return          a success value
     * @throws          NullPointerException if {@code result} is null
     */
    static <E, T> Validation<E, T> success(T result) {
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
    static <E, T> Validation<E, T> failure(IList<E> errors) {
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
    static <E, T> Validation<E, T> failure(Iterable<E> errors) {
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
    static <E, T> Validation<E, T> failure(E error) {
        return new Failure<E, T>(IList.of(error));
    }

    /**
     * Create a {@code Validation} value from a function which either yields a result or throws.
     * @param f  function which may throw
     * @param error exception to error translator
     * @param <E>       the error type
     * @param <T>       the successful result type
     * @param <X>       the exception type
     * @return          the {@code Validation} value which wraps the function result
     */
    @SuppressWarnings("unchecked")
    static <E, T, X extends Exception>
    Validation<E, T> of(FunctionsGenEx.F0<T, X> f, F<X, E> error) {
        try {
            return success(f.apply());
        } catch (Exception ex) {
            return failure(error.apply((X)ex));
        }
    }

    /**
     * Applicative function application.
     * @param vf        the function wrapped in a {@code Validation}
     * @param va        the function argument wrapped in a {@code Validation}
     * @param <E>       the error type
     * @param <A>       the function argument type
     * @param <B>       the function return type
     * @return          the result of applying the function to the argument, wrapped in a {@code Validation}
     */
    static <E, A, B> Validation<E, B> ap(Validation<E, F<A, B>> vf, Validation<E, A> va) {
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
     * @return          a {@code Validation} which wraps an {@link IList} of values
     */
    static <E, T, U> Validation<E, IList<U>> traverse(IList<T> lt, F<T, Validation<E, U>> f) {
        return lt.foldRight(
                (t, vlu) -> f.apply(t).apply(vlu.map(lu -> lu::add)),
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
     * @return          a {@code Validation} which wraps an {@link List} of values
     */
    static <E, T, U> Validation<E, List<U>> traverse(List<T> lt, F<T, Validation<E, U>> f) {
        return Folds.foldLeft(
                (vlu, t) -> f.apply(t).apply(vlu.map(lu -> u -> {lu.add(u); return lu;})),
                success(new ArrayList<>(lt.size())),
                lt
        );
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code Validation} into a {@code Validation} of an {@code IList},
     * by composing each consecutive {@code Validation} using the {@link Validation#apply(Validation)} method.
     * @param lvt       the list of {@code Validation} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Validation}s in the list
     * @return          a {@code Validation} which wraps an {@link IList} of values
     */
    static <E, T> Validation<E, IList<T>> sequence(IList<Validation<E, T>> lvt) {
        return lvt.foldRight(
                (vt, vlt) -> vt.apply(vlt.map(lt -> lt::add)),
                success(IList.nil())
        );
    }

    /**
     * Variation of {@link Validation#sequence(IList)} for a {@link Stream}.
     * @param svt       the stream of {@code Validation} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Validation}s in the stream
     * @return          a {@code Validation} which wraps an {@link Stream} of values
     */
    static <E, T> Validation<E, Stream<T>> sequence(Stream<Validation<E, T>> svt) {
        final Iterator<Validation<E, T>> iter = svt.iterator();
        Validation<E, IList<T>> vlt = success(IList.nil());
        while (iter.hasNext()) {
            final Validation<E, T> vt = iter.next();
            vlt = vt.apply(vlt.map(lt -> lt::add));
        }
        return vlt.map(IList::stream);
    }

    /**
     * Variation of {@link Validation#sequence(IList)} for a {@link List}.
     * @param lvt       the list of {@code Validation} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Validation}s in the stream
     * @return          a {@code Validation} which wraps an {@link Stream} of values
     */
    static <E, T> Validation<E, List<T>> sequence(List<Validation<E, T>> lvt) {
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
    static <E, A, B> Validation<E, B> tailRecM(A a, F<A, Validation<E, Either<A, B>>> f) {
        while (true) {
            final Validation<E, Either<A, B>> ve = f.apply(a);
            if (ve instanceof Validation.Failure) {
                return ((Validation.Failure)ve).cast();
            } else {
                final Validation.Success<E, Either<A, B>> vse = (Validation.Success<E, Either<A, B>>)ve;
                if (vse.value instanceof Either.Left) {
                    final Either.Left<A, B> left = (Either.Left<A, B>) vse.value;
                    a = left.value;
                } else {
                    final Either.Right<A, B> right = (Either.Right<A, B>) vse.value;
                    return Validation.success(right.value);
                }
            }
        }
    }

    /**
     * Indicates if this is a {@code Success} value.
     * @return          the true if this value is a {code Success} value
     */
    boolean isSuccess();

    /**
     * Either return the wrapped value if it's a {@code Success},
     * otherwise return the supplied default value.
     * @param defaultValue value to be returned if this is a failure value.
     * @return          the success result value if it's a {@code Success},
     *                  otherwise return the supplied default value.
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
     * @param failure   the side-effect to be applied to {@code Failure} values
     * @param success   the side-effect to be applied to {@code Success} values
     */
    void handle(SideEffect.F<Failure<E, T>> failure, SideEffect.F<Success<E, T>> success);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param failure   the function to be applied to {@code Failure} values
     * @param success   the function to be applied to {@code Success} values
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R match(F<Failure<E, T>, R> failure, F<Success<E, T>, R> success);

    /**
     * Functor function application.
     * If this value is a {@code Success} then apply the function to the value,
     * otherwise if this is a {@code Failure} then leave it untouched.
     * @param f         the function to be applied
     * @param <U>       the function return type
     * @return          a {@code Validation} that wraps the function result, or the original failure
     */
    <U> Validation<E, U> map(F<T, U> f);

    /**
     * Applicative function application (inverted).
     * If the {@code vf} parameter is a {@code Success} value and this is a {@code Success} value,
     * then apply the function wrapped in the {@code tf} to this.
     * @param vf        the function wrapped in a {@code Validation}
     * @param <U>       the return type of function
     * @return          a {@code Validation} wrapping the result of applying the function, or a {@code Failure} value
     */
    <U> Validation<E, U> apply(Validation<E, F<T, U>> vf);

    /**
     * Builder API for chaining together n {@code Validation}s,
     * and applying an n-ary function at the end.
     * @param vb        the next {@code Validation} value to chain
     * @param <U>       the successful result type for next {@code Validation}
     * @return          the next builder
     */
    default <U> ApplyBuilder._2<E, T, U> and(Validation<E, U> vb) {
        return new ApplyBuilder._2<E, T, U>(this, vb);
    }

    /**
     * Success type.
     * @param <E>       the error type
     * @param <T>       the successful result type
     */
    class Success<E, T> implements Validation<E, T> {
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
        public void handle(SideEffect.F<Failure<E, T>> failure, SideEffect.F<Success<E, T>> success) {
            success.apply(this);
        }

        @Override
        public <R> R match(F<Failure<E, T>, R> failure, F<Success<E, T>, R> success) {
            return success.apply(this);
        }

        @Override
        public <U> Validation<E, U> map(F<T, U> f) {
            return success(f.apply(value));
        }

        @Override
        public <U> Validation<E, U> apply(Validation<E, F<T, U>> vf) {
            return vf.map(f -> f.apply(value));
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
    class Failure<E, T> implements Validation<E, T> {
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
        public void handle(SideEffect.F<Failure<E, T>> failure, SideEffect.F<Success<E, T>> success) {
            failure.apply(this);
        }

        @Override
        public <R> R match(F<Failure<E, T>, R> failure, F<Success<E, T>, R> success) {
            return failure.apply(this);
        }

        @Override
        public <U> Validation<E, U> map(F<T, U> f) {
            return cast();
        }

        @Override
        public <U> Validation<E, U> apply(Validation<E, F<T, U>> vf) {
            return cast();
        }

        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T getOrThrow() throws Exception {
            throw new Exception("Validation.getOrThrow() called on a Validation.Failure value");
        }

        @SuppressWarnings("unchecked")
        public <U> Failure<E, U> cast() {
            return (Failure<E, U>) this;
        }
    }

    class ApplyBuilder {
        public static class _2<E, A, B> {
            private final Validation<E, A> va;
            private final Validation<E, B> vb;

            _2(Validation<E, A> va, Validation<E, B> vb) {
                this.va = va;
                this.vb = vb;
            }

            public <R> Validation<E, R> map(F<A, F<B, R>> f) {
                return vb.apply(va.map(f));
            }

            public <R> Validation<E, R> map(F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Validation<E, C> vc) {
                return new _3<C>(vc);
            }

            public class _3<C> {
                private final Validation<E, C> vc;

                private _3(Validation<E, C> vc) {
                    this.vc = vc;
                }

                public <R> Validation<E, R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), vc);
                }

                public <R> Validation<E, R> map(F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Validation<E, D> vd) {
                    return new _4<D>(vd);
                }

                public class _4<D> {
                    private final Validation<E, D> vd;

                    private _4(Validation<E, D> vd) {
                        this.vd = vd;
                    }

                    public <R> Validation<E, R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), vd);
                    }

                    public <R> Validation<E, R> map(F4<A, B, C, D, R> f) {
                        return map(f.curry());
                    }

                    public <G> _5<G> and(Validation<E, G> vg) {
                        return new _5<G>(vg);
                    }

                    public class _5<G> {
                        private final Validation<E, G> vg;

                        private _5(Validation<E, G> vg) {
                            this.vg = vg;
                        }

                        public <R> Validation<E, R> map(F<A, F<B, F<C, F<D, F<G, R>>>>> f) {
                            return ap(_4.this.map(f), vg);
                        }

                        public <R> Validation<E, R> map(F5<A, B, C, D, G, R> f) {
                            return map(f.curry());
                        }

                        public <H> _6<H> and(Validation<E, H> vg) {
                            return new _6<H>(vg);
                        }

                        public class _6<H> {
                            private final Validation<E, H> vh;

                            private _6(Validation<E, H> vh) {
                                this.vh = vh;
                            }

                            public <R> Validation<E, R> map(F<A, F<B, F<C, F<D, F<G, F<H, R>>>>>> f) {
                                return ap(_5.this.map(f), vh);
                            }

                            public <R> Validation<E, R> map(F6<A, B, C, D, G, H, R> f) {
                                return map(f.curry());
                            }
                        }
                    }
                }
            }
        }
    }
}
