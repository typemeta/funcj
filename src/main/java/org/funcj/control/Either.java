package org.funcj.control;

import org.funcj.data.IList;
import org.funcj.util.*;
import org.funcj.util.Functions.F;

import java.util.*;
import java.util.function.Consumer;

/**
 * Standard tagged union type over two types.
 * A value of Either&lt;A, B&gt; wraps either a value of type A or a value of type B.
 * Either has right-bias, meaning that map, apply and flatMap operate on the
 * Right value and bypass the Left value.
 * @param <E> left-hand type (possibly an error type)
 * @param <S> right-hand type (possibly a success type)
 */
public interface Either<E, S> {
    /**
     * Construct a left value.
     * @param value value to wrap
     * @param <E> left-hand type
     * @param <S> right-hand type
     * @return a {@code Left} value
     */
    static <E, S> Either<E, S> left(E value) {
        return new Left<E, S>(value);
    }

    /**
     * Construct a right value.
     * @param value value to wrap
     * @param <E> left-hand type
     * @param <S> right-hand type
     * @return a {@code Right} value
     */
    static <E, S> Either<E, S> right(S value) {
        return new Right<E, S>(value);
    }

    /**
     * Applicative function application.
     * @param ef function wrapped in a {@code Either}
     * @param eb function argument wrapped in a {@code Either}
     * @param <E> left-hand, error type
     * @param <S> right-hand, success type
     * @param <T> function return type
     * @return the result of applying the function to the argument, wrapped in a {@code Either}
     */
    static <E, S, T> Either<E, T> ap(Either<E, F<S, T>> ef, Either<E, S> eb) {
        return eb.apply(ef);
    }

    /**
     * Standard applicative traversal.
     * @param ls list of values
     * @param f function to be applied to each value in the list
     * @param <E> left-hand, error type of the {@code Either} returned by the function
     * @param <S> type of list elements
     * @param <T> right-hand, success type of the {@code Either} returned by the function
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <E, S, T> Either<E, IList<T>> traverse(IList<S> ls, F<S, Either<E, T>> f) {
        return ls.foldRight(
            (s, elt) -> f.apply(s).apply(elt.map(lt -> lt::add)),
            right(IList.nil())
        );
    }

    /**
     * Standard applicative traversal.
     * @param ls list of values
     * @param f function to be applied to each value in the list
     * @param <E> left-hand, error type of the {@code Either} returned by the function
     * @param <S> type of list elements
     * @param <T> right-hand, success type of the {@code Either} returned by the function
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <E, S, T> Either<E, List<T>> traverse(List<S> ls, F<S, Either<E, T>> f) {
        return Folds.foldRight(
                (s, elt) -> f.apply(s).apply(elt.map(lt -> t -> {lt.add(t); return lt;})),
                right(new ArrayList<>()),
                ls
        );
    }

    /**
     * Standard applicative sequencing.
     * @param les list of {@code Try} values
     * @param <E> left-hand, error type
     * @param <S> right-hand, success type
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <E, S> Either<E, IList<S>> sequence(IList<Either<E, S>> les) {
        return les.foldRight(
            (es, els) -> es.apply(els.map(ls -> ls::add)),
            right(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * @param les list of {@code Try} values
     * @param <E> left-hand, error type
     * @param <S> right-hand, success type
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <E, S> Either<E, List<S>> sequence(List<Either<E, S>> les) {
        return Folds.foldRight(
                (es, els) -> es.apply(els.map(ls -> s -> {ls.add(s); return ls;})),
                right(new ArrayList<>()),
                les
        );
    }

    /**
     * Indicates if this is a {code Right} value.
     * @return true if this value is a {code Right} value
     */
    boolean isRight();

    /**
     * Downgrade this value into an {@link java.util.Optional}.
     * @return a populated {@code Optional} value if this is a {Code Right} value,
     * otherwise an empty {@code Optional}
     */
    Optional<S> asOptional();

    /**
     * Push the result to a {@link java.util.function.Consumer}.
     * @param left consumer to be applied to {@code Left} values
     * @param right consumer to be applied to {@code Right} values
     */
    void handle(Consumer<Left<E, S>> left, Consumer<Right<E, S>> right);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param left function to be applied to {@code Left} values
     * @param right function to be applied to {@code Right} values
     * @param <R> return type of functions
     * @return the result of applying either function
     */
    <R> R match(F<Left<E, S>, ? extends R> left, F<Right<E, S>, ? extends R> right);

    /**
     * Functor function application.
     * If this value is a {@code Right} then apply the function to the value,
     * otherwise if this is a {@code Left} then leave it untouched.
     * @param f function to be applied
     * @param <R> function return type
     * @return an {@code Either} that wraps the function result, or the original {@code Left} value
     */
    <R> Either<E, R> map(F<? super S, ? extends R> f);

    /**
     * Function application for {@code Left} values.
     * If this value is a {@code Left} then apply the function to the value,
     * otherwise if this is a {@code Right} then leave it untouched.
     * @param f function to be applied
     * @param <R> function return type
     * @return an {@code Either} that wraps the function result, or the original {@code Right} value
     */
    <R> Either<R, S> mapLeft(F<? super E, ? extends R> f);

    /**
     * Applicative function application (inverted).
     * If the {@code ef} parameter is a {@code Right} value and this is a {@code Right} value,
     * then apply the function wrapped in the {@code ef} to this.
     * @param ef function wrapped in an {@code Either}
     * @param <R> return type of function
     * @return a {@code Either} wrapping the result of applying the function, or a {@code Left} value
     */
    <R> Either<E, R> apply(Either<E, F<S, R>> ef);

    /**
     * Monadic bind/flatMap.
     * If this is a {@code Right} then apply the function to the value and return the result,
     * otherwise return the {@code Left} result.
     * @param f function to be applied
     * @param <R> type parameter to the {@code Either} returned by the function
     * @return the result of combining this value with the function {@code f}
     */
    <R> Either<E, R> flatMap(F<? super S, Either<E, R>> f);

    /**
     * Builder API for chaining together n {@code Validation}s,
     * and applying an n-ary function at the end.
     * @param vb next {@code Validation} value to chain
     * @param <T> successful result type for next {@code Validation}
     * @return next builder
     */
    default <T> ApplyBuilder._2<E, S, T> and(Either<E, T> vb) {
        return new ApplyBuilder._2<E, S, T>(this, vb);
    }

    /**
     * Left value.
     * @param <E> left-hand type
     * @param <S> right-hand type
     */
    final class Left<E, S> implements Either<E, S> {
        public final E value;

        private Left(E value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public Optional<S> asOptional() {
            return Optional.empty();
        }

        @Override
        public void handle(Consumer<Left<E, S>> left, Consumer<Right<E, S>> right) {
            left.accept(this);
        }

        @Override
        public <T> T match(F<Left<E, S>, ? extends T> left, F<Right<E, S>, ? extends T> right) {
            return left.apply(this);
        }

        @Override
        public <T> Either<T, S> mapLeft(F<? super E, ? extends T> f) {
            return Either.<T, S>left(f.apply(value));
        }

        @Override
        public <T> Either<E, T> map(F<? super S, ? extends T> f) {
            return cast();
        }

        @Override
        public <C> Either<E, C> apply(Either<E, F<S, C>> ef) {
            return cast();
        }

        @Override
        public <T> Either<E, T> flatMap(F<? super S, Either<E, T>> f) {
            return cast();
        }

        private <C> Either<E, C> cast() {
            return (Either<E, C>) this;
        }
    }

    /**
     * Right value.
     * @param <E> left-hand type
     * @param <S> right-hand type
     */
    final class Right<E, S> implements Either<E, S> {
        public final S value;

        private Right(S value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public Optional<S> asOptional() {
            return Optional.of(value);
        }

        @Override
        public void handle(Consumer<Left<E, S>> left, Consumer<Right<E, S>> right) {
            right.accept(this);
        }

        @Override
        public <T> T match(F<Left<E, S>, ? extends T> left, F<Right<E, S>, ? extends T> right) {
            return right.apply(this);
        }

        @Override
        public <T> Either<T, S> mapLeft(F<? super E, ? extends T> f) {
            return (Either<T, S>) this;
        }

        @Override
        public <T> Either<E, T> map(F<? super S, ? extends T> f) {
            return Either.right(f.apply(value));
        }

        @Override
        public <C> Either<E, C> apply(Either<E, F<S, C>> ef) {
            return ef.map(f -> f.apply(value));
        }

        @Override
        public <T> Either<E, T> flatMap(F<? super S, Either<E, T>> f) {
            return f.apply(value);
        }
    }
    
    class ApplyBuilder {
        public static class _2<E, A, B> {
            private final Either<E, A> va;
            private final Either<E, B> vb;

            _2(Either<E, A> va, Either<E, B> vb) {
                this.va = va;
                this.vb = vb;
            }

            public <R> Either<E, R> map(F<A, F<B, R>> f) {
                return vb.apply(va.map(f));
            }

            public <R> Either<E, R> map(Functions.F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Either<E, C> vc) {
                return new _3<C>(vc);
            }

            public class _3<C> {
                private final Either<E, C> vc;

                private _3(Either<E, C> vc) {
                    this.vc = vc;
                }

                public <R> Either<E, R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), vc);
                }

                public <R> Either<E, R> map(Functions.F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Either<E, D> vd) {
                    return new _4<D>(vd);
                }

                public class _4<D> {
                    private final Either<E, D> vd;

                    private _4(Either<E, D> vd) {
                        this.vd = vd;
                    }

                    public <R> Either<E, R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), vd);
                    }

                    public <R> Either<E, R> map(Functions.F4<A, B, C, D, R> f) {
                        return map(f.curry());
                    }

                    public <G> _5<G> and(Either<E, G> ee) {
                        return new _5<G>(ee);
                    }

                    public class _5<G> {
                        private final Either<E, G> ee;

                        private _5(Either<E, G> ee) {
                            this.ee = ee;
                        }

                        public <R> Either<E, R> map(F<A, F<B, F<C, F<D, F<G, R>>>>> f) {
                            return ap(_4.this.map(f), ee);
                        }

                        public <R> Either<E, R> map(Functions.F5<A, B, C, D, G, R> f) {
                            return map(f.curry());
                        }

                        public <H> _6<H> and(Either<E, H> eg) {
                            return new _6<H>(eg);
                        }

                        public class _6<H> {
                            private final Either<E, H> eg;

                            private _6(Either<E, H> eg) {
                                this.eg = eg;
                            }

                            public <R> Either<E, R> map(F<A, F<B, F<C, F<D, F<G, F<H, R>>>>>> f) {
                                return ap(_5.this.map(f), eg);
                            }

                            public <R> Either<E, R> map(Functions.F6<A, B, C, D, G, H, R> f) {
                                return map(f.curry());
                            }
                        }
                    }
                }
            }
        }
    }
}
