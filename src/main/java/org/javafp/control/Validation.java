package org.javafp.control;

import org.javafp.data.IList;
import org.javafp.util.Functions.*;
import org.javafp.util.FunctionsGenEx;

import java.util.function.Consumer;

public interface Validation<E, T> {
    static <E, T> Validation<E, T> success(T result) {
        return new Success<E, T>(result);
    }

    static <E, T> Validation<E, T> failure(IList<E> errors) {
        return new Failure<E, T>(errors);
    }

    static <E, T> Validation<E, T> failure(E error) {
        return new Failure<E, T>(IList.of(error));
    }

    static <E, T, X extends Exception>
    Validation<E, T> of(FunctionsGenEx.F0<T, X> get, F<X, E> error) {
        try {
            return success(get.apply());
        } catch (Exception ex) {
            return failure(error.apply((X)ex));
        }
    }

    static <E, A, B> Validation<E, B> ap(Validation<E, F<A, B>> vf, Validation<E, A> va) {
        return va.apply(vf);
    }

    boolean isSuccess();

    void handle(Consumer<Success<E, T>> success, Consumer<Failure<E, T>> failure);

    <U> U match(F<Success<E, T>, U> success, F<Failure<E, T>, U> failure);

    <U> Validation<E, U> map(F<T, U> f);

    <U> Validation<E, U> apply(Validation<E, F<T, U>> vf);

    <U> Validation<E, U> flatMap(F<T, Validation<E, U>> f);

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

                        public <G> _6<G> and(Validation<E, G> pg) {
                            return new _6<G>(pg);
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
