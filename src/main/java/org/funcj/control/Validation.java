package org.funcj.control;

import org.funcj.data.IList;
import org.funcj.util.Functions.*;
import org.funcj.util.FunctionsGenEx;

import java.util.function.Consumer;

/**
 * Simple monadic wrapper for computations which result in either a successfully computed value
 * or a list of errors.
 * Validation is effectively a discriminated union of {@code Success} (which wraps a value)
 * and {@code Failure} (which wraps a list of errors).
 * @param <E> error type
 * @param <T> successful result type
 */
public interface Validation<E, T> {
    /**
     * Create a {@code Success} value that wraps a successful result.
     * @param result successful result to be wrapped
     * @param <E> error type
     * @param <T> successful result type
     * @return a success value
     */
    static <E, T> Validation<E, T> success(T result) {
        return new Success<E, T>(result);
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param errors list of failures result to be wrapped
     * @param <E> error type
     * @param <T> successful result type
     * @return a failure value
     */
    static <E, T> Validation<E, T> failure(IList<E> errors) {
        return new Failure<E, T>(errors);
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param error single failure result to be wrapped
     * @param <E> error type
     * @param <T> successful result type
     * @return a failure value
     */
    static <E, T> Validation<E, T> failure(E error) {
        return new Failure<E, T>(IList.of(error));
    }

    /**
     * Create a {@code Validation} value from a function which either yields a result or throws.
     * @param f function which may throw
     * @param <T> successful result type
     * @return {@code Validation} value which wraps the function result
     */
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
     * @param vf function wrapped in a {@code Validation}
     * @param va function argument wrapped in a {@code Validation}
     * @param <A> function argument type
     * @param <B> function return type
     * @return the result of applying the function to the argument, wrapped in a {@code Validation}
     */
    static <E, A, B> Validation<E, B> ap(Validation<E, F<A, B>> vf, Validation<E, A> va) {
        return va.apply(vf);
    }

    /**
     * Standard applicative traversal.
     * @param lt list of {@code Validation} values
     * @param f function to be applied to each {@code Validation} in the list
     * @param <T> type of list elements
     * @param <U> type wrapped by the {@code Try} returned by the function
     * @return a {@code Validation} which wraps an {@link org.funcj.data.IList} of values
     */
    static <E, T, U> Validation<E, IList<U>> traverse(IList<T> lt, F<T, Validation<E, U>> f) {
        return lt.foldRight(
                (t, tlt) -> f.apply(t).apply(tlt.map(l -> l::add)),
                success(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * @param lt list of {@code Validation} values
     * @param <T> type of list elements
     * @return a {@code Validation} which wraps an {@link org.funcj.data.IList} of values
     */

    static <E, T> Validation<E, IList<T>> sequence(IList<Validation<E, T>> lt) {
        return lt.foldRight(
                (tt, tlt) -> tt.apply(tlt.map(l -> l::add)),
                success(IList.nil())
        );
    }

    /**
     * Indicates if this value is a {code Success} value.
     * @return true if this value is a {code Success} value
     */
    boolean isSuccess();

    /**
     * Push the result to a {@link java.util.function.Consumer}.
     * @param success consumer to be applied to {@code Success} values
     * @param failure consumer to be applied to {@code Failure} values
     */
    void handle(Consumer<Success<E, T>> success, Consumer<Failure<E, T>> failure);

    /**
     * Map a function over the value.
     * @param success
     * @param failure
     * @param <R>
     * @return
     */
    <R> R match(F<Success<E, T>, R> success, F<Failure<E, T>, R> failure);

    <R> Validation<E, R> map(F<T, R> f);

    <R> Validation<E, R> apply(Validation<E, F<T, R>> vf);

    <R> Validation<E, R> flatMap(F<T, Validation<E, R>> f);

    default <U> ApplyBuilder._2<E, T, U> and(Validation<E, U> vb) {
        return new ApplyBuilder._2<E, T, U>(this, vb);
    }

    class Success<E, T> implements Validation<E, T> {
        public final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public void handle(Consumer<Success<E, T>> success, Consumer<Failure<E, T>> failure) {
            success.accept(this);
        }

        @Override
        public <U> U match(F<Success<E, T>, U> success, F<Failure<E, T>, U> failure) {
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
        public <U> Validation<E, U> flatMap(F<T, Validation<E, U>> f) {
            return f.apply(value);
        }
    }

    class Failure<E, T> implements Validation<E, T> {
        public final IList<E> errors;

        public Failure(IList<E> errors) {
            this.errors = errors;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public void handle(Consumer<Success<E, T>> success, Consumer<Failure<E, T>> failure) {
            failure.accept(this);
        }

        @Override
        public <U> U match(F<Success<E, T>, U> success, F<Failure<E, T>, U> failure) {
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
        public <U> Validation<E, U> flatMap(F<T, Validation<E, U>> f) {
            return cast();
        }

        public <U> Failure<E, U> cast() {
            return (Failure<E, U>) this;
        }
    }

    class ApplyBuilder {
        public static class _2<E, A, B> {
            private final Validation<E, A> pa;
            private final Validation<E, B> pb;

            _2(Validation<E, A> pa, Validation<E, B> pb) {
                this.pa = pa;
                this.pb = pb;
            }

            public <R> Validation<E, R> map(F<A, F<B, R>> f) {
                return pb.apply(pa.map(f));
            }

            public <R> Validation<E, R> map(F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Validation<E, C> pc) {
                return new _3<C>(pc);
            }

            public class _3<C> {
                private final Validation<E, C> pc;

                private _3(Validation<E, C> pc) {
                    this.pc = pc;
                }

                public <R> Validation<E, R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), pc);
                }

                public <R> Validation<E, R> map(F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Validation<E, D> pd) {
                    return new _4<D>(pd);
                }

                public class _4<D> {
                    private final Validation<E, D> pd;

                    private _4(Validation<E, D> pd) {
                        this.pd = pd;
                    }

                    public <R> Validation<E, R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), pd);
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

                        public <H> _6<H> and(Validation<E, H> pg) {
                            return new _6<H>(pg);
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
