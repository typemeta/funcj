package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.*;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.util.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * Tagged union type over two types.
 * <p>
 * A value of {@code Either<A, B>} is either
 * the sub-type {@code Either.Left<A, B>} which wraps a value of type A, or
 * the sub-type {@code Either.Right<A, B>} which wraps a value of type B.
 * <p>
 * Either has right-bias, meaning that map, apply and flatMap operate on the
 * {@code Right} value and bypass the {@code Left} value.
 * <p>
 * Null values are not allowed.
 * @param <E>       the left-hand type (typically an error type)
 * @param <S>       the right-hand type (typically a success type)
 */
public interface Either<E, S> {
    /**
     * Construct a left value.
     * @param value     the value to wrap
     * @param <E>       the left-hand type
     * @param <S>       the right-hand type
     * @return          a {@code Left} value
     * @throws          NullPointerException if {@code value} is null
     */
    static <E, S> Either<E, S> left(E value) {
        return new Left<E, S>(value);
    }

    /**
     * Construct a right value.
     * @param value     the value to wrap
     * @param <E>       the left-hand type
     * @param <S>       the right-hand type
     * @return          a {@code Right} value
     * @throws          NullPointerException if {@code value} is null
     */
    static <E, S> Either<E, S> right(S value) {
        return new Right<E, S>(value);
    }

    /**
     * Create a {@code Either} value from a function which either yields a result or throws.
     * @param f         the function which may throw
     * @param <E>       the exception result type
     * @param <S>       the successful result type
     * @return          a {@code Either} value which wraps the function result
     */
    @SuppressWarnings("unchecked")
    static <E extends Exception, S> Either<E, S> of(FunctionsGenEx.F0<S, E> f) {
        try {
            return right(f.apply());
        } catch (Exception ex) {
            return left((E)ex);
        }
    }

    /**
     * Applicative function application.
     * @param ef        the function wrapped in a {@code Either}
     * @param eb        the function argument wrapped in a {@code Either}
     * @param <E>       the left-hand type
     * @param <S>       the right-hand type
     * @param <T>       the function return type
     * @return          the result of applying the function to the argument, wrapped in a {@code Either}
     */
    static <E, S, T> Either<E, T> ap(Either<E, F<S, T>> ef, Either<E, S> eb) {
        return eb.app(ef);
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(ls.map(f))</pre>.
     * @param ls        the list of values
     * @param f         the function to be applied to each value in the list
     * @param <E>       the left-hand type of the {@code Either} returned by the function
     * @param <S>       the type of list elements
     * @param <T>       the right-hand type of the {@code Either} returned by the function
     * @return          a {@code Either} which wraps an {@link IList} of values
     */
    static <E, S, T> Either<E, IList<T>> traverse(IList<S> ls, F<S, Either<E, T>> f) {
        return ls.foldRight(
            (s, elt) -> elt.app(f.apply(s).map(b -> l -> l.add(b))),
            right(IList.nil())
        );
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param ls        the list of values
     * @param f         the function to be applied to each value in the list
     * @param <E>       the left-hand type of the {@code Either} returned by the function
     * @param <S>       the type of list elements
     * @param <T>       the right-hand type of the {@code Either} returned by the function
     * @return          a {@code Either} which wraps an {@link List} of values
     */
    static <E, S, T> Either<E, List<T>> traverse(List<S> ls, F<S, Either<E, T>> f) {
        return sequence(Functors.map(f, ls));
    }

    /**
     * Standard applicative sequencing.
     * @param les       the list of {@code Try} values
     * @param <E>       the left-hand type
     * @param <S>       the right-hand type of the {@code Either}s in the list
     * @return          a {@code Try} which wraps an {@link IList} of values
     */
    static <E, S> Either<E, IList<S>> sequence(IList<Either<E, S>> les) {
        return les.foldRight(
                (es, els) -> els.app(es.map(a -> l -> l.add(a))),
                right(IList.nil())
        );
    }

    /**
     * Variation of {@link Either#sequence(IList)} for a {@link List}.
     * @param let       the list of {@code Validated} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Validated}s in the stream
     * @return          a {@code Validated} which wraps an {@link Stream} of values
     */
    static <E, T> Either<E, List<T>> sequence(List<Either<E, T>> let) {
        final Either<E, List<T>> res = Folds.foldRight(
                (et, elt) -> elt.app(et.map(t -> lt -> {lt.add(t); return lt;})),
                right(new ArrayList<>(let.size())),
                let
        );
        return res.map(l -> {Collections.reverse(l); return l;});
    }

    /**
     * Repeatedly call the function {@code f} until it returns {@code Either.Right}.
     * <p>
     * Call the function {@code f} and if it returns a right value then return the wrapped value,
     * otherwise extract the value and call {@code f} again.
     * @param a         the starting value
     * @param f         the function
     * @param <E>       the left-hand type
     * @param <A>       the starting value type
     * @param <B>       the final value type
     * @return          the final value
     */
    @SuppressWarnings("unchecked")
    static <E, A, B> Either<E, B> tailRecM(A a, F<A, Either<E, Either<A, B>>> f) {
        while (true) {
            final Either<E, Either<A, B>> ee = f.apply(a);
            if (ee instanceof Either.Left) {
                return ((Either.Left)ee).cast();
            } else {
                final Either.Right<E, Either<A, B>> re = (Either.Right<E, Either<A, B>>)ee;
                if (re.value instanceof Either.Left) {
                    final Either.Left<A, B> left = (Either.Left<A, B>) re.value;
                    a = left.value;
                } else {
                    final Either.Right<A, B> right = (Either.Right<A, B>) re.value;
                    return Either.right(right.value);
                }
            }
        }
    }

    /**
     * {@code Kleisli} models composable operations that return an {@code Either}.
     * @param <E>       the left-hand type
     * @param <T>       the input type
     * @param <U>       the value type of the returned {@code Either} type
     */
    @FunctionalInterface
    interface Kleisli<E, T, U> {
        /**
         * Construct a {@code Kleisli} value from a function.
         * @param f         the function
         * @param <E>       the left-hand type
         * @param <T>       the input type
         * @param <U>       the value type of the returned {@code Either} value
         * @return          the new {@code Kleisli}
         */
        static <E, T, U> Kleisli<E, T, U> of(F<T, Either<E, U>> f) {
            return f::apply;
        }

        /**
         * Apply this {@code Kleisli} operation
         * @param t         the input value
         * @return          the result of the operation
         */
        Either<E, U> apply(T t);

        /**
         * Compose this {@code Kleisli} with another by applying this one first,
         * then the other.
         * @param kUV       the {@code Kleisli} to be applied after this one
         * @param <V>       the second {@code Kleisli}'s return type
         * @return          the composed {@code Kleisli}
         */
        default <V> Kleisli<E, T, V> andThen(Kleisli<E, U, V> kUV) {
            return t -> this.apply(t).flatMap(kUV::apply);
        }

        /**
         * Compose this {@code Kleisli} with another by applying the other one first,
         * and then this one.
         * @param kST       the {@code Kleisli} to be applied after this one
         * @param <S>       the first {@code Kleisli}'s input type
         * @return          the composed {@code Kleisli}
         */
        default <S> Kleisli<E, S, U> compose(Kleisli<E, S, T> kST) {
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
        default <V> Kleisli<E, T, V> map(F<U, V> f) {
            return t -> this.apply(t).map(f);
        }
    }

    /**
     * Indicates if this is a {@code Left} value.
     * @return          true if this value is a {@code Left} value
     */
    boolean isLeft();

    /**
     * Indicates if this is a {@code Right} value.
     * @return          true if this value is a {@code Right} value
     */
    boolean isRight();

    /**
     * If this is a {@code Left} value then return the contained value,
     * otherwise throw an exception.
     * @return          the {@code Left} value
     * @throws          RuntimeException if this is a {@code Right} value
     */
    E left();

    /**
     * If this is a {@code Right} value then return the contained value,
     * otherwise throw an exception.
     * @return          the {@code Right} value
     * @throws          RuntimeException if this is a {@code Left} value
     */
    S right();

    /**
     * Either return the wrapped value if it's a {@code Right},
     * otherwise return the given default value.
     * @param defaultValue value to be returned if this is a {@code Left} value.
     * @return          the success result value if it's a {@code Right},
     *                  otherwise return the given default value.
     */
    S getOrElse(S defaultValue);

    /**
     * Return the wrapped value if it's a {@code Right}, otherwise throw an exception.
     * @return          the wrapped value if it's a {@code Right}
     */
    S getOrThrow();

    /**
     * Push the result to a {@link SideEffect.F}.
     * @param leftF     the side-effect to be applied to the {@code Left} value
     * @param rightF    the side-effect to be applied to the {@code Right} value
     */
    void handle(SideEffect.F<Left<E, S>> leftF, SideEffect.F<Right<E, S>> rightF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param leftF     the function to be applied if this is a {@code Left} value
     * @param rightF    the function to be applied if this is a  {@code Right} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R match(F<Left<E, S>, ? extends R> leftF, F<Right<E, S>, ? extends R> rightF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param leftF     the function to be applied if this is a {@code Left} value
     * @param rightF    the function to be applied if this is a  {@code Right} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R fold(F<E, ? extends R> leftF, F<S, ? extends R> rightF);

    /**
     * Functor function application.
     * If this value is a {@code Right} then apply the function to the value,
     * otherwise if this is a {@code Left} then leave it untouched.
     * @param f         the function to be applied
     * @param <T>       the function return type
     * @return          an {@code Either} that wraps the function result, or the original {@code Left} value
     */
    <T> Either<E, T> map(F<? super S, ? extends T> f);

    /**
     * Function application for {@code Left} values.
     * If this value is a {@code Left} then apply the function to the value,
     * otherwise if this is a {@code Right} then leave it untouched.
     * @param f         the function to be applied
     * @param <T>       the function return type
     * @return          an {@code Either} that wraps the function result, or the original {@code Right} value
     */
    <T> Either<T, S> mapLeft(F<? super E, ? extends T> f);

    /**
     * Applicative function application (inverted).
     * If the {@code ef} parameter is a {@code Right} value and this is a {@code Right} value,
     * then apply the function wrapped in the {@code ef} to this.
     * @param ef        the function wrapped in an {@code Either}
     * @param <T>       the return type of function
     * @return          a {@code Either} wrapping the result of applying the function, or a {@code Left} value
     */
    <T> Either<E, T> app(Either<E, F<S, T>> ef);

    /**
     * Monadic bind/flatMap.
     * If this is a {@code Right} then apply the function to the value and return the result,
     * otherwise return the {@code Left} result.
     * @param f         the function to be applied
     * @param <T>       the type parameter to the {@code Either} returned by the function
     * @return          the result of combining this value with the function {@code f}
     */
    <T> Either<E, T> flatMap(F<? super S, Either<E, T>> f);

    /**
     * Apply a side-effect operation to this value
     * If this value is a {@code Right} then apply the function to the value,
     * otherwise do nothing.
     * @param f         the function to be applied
     */
    void forEach(SideEffect.F<? super S> f);

    /**
     * Builder API for chaining together n {@code Either}s,
     * and applying an n-ary function at the end.
     * @param eb        the next {@code Either} value to chain
     * @param <T>       the successful result type for next {@code Either}
     * @return          the next builder
     */
    default <T> ApplyBuilder._2<E, S, T> and(Either<E, T> eb) {
        return new ApplyBuilder._2<E, S, T>(this, eb);
    }

    /**
     * Left value.
     * @param <E>       the left-hand type
     * @param <S>       the right-hand type
     */
    final class Left<E, S> implements Either<E, S> {
        public final E value;

        private Left(E value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public String toString() {
            return "Left{value=" + value + '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                Left<?, ?> left = (Left<?, ?>) rhs;
                return Objects.equals(value, left.value);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public E left() {
            return value;
        }

        @Override
        public S right() {
            throw new RuntimeException("Either.right() called on a Either.Left value");
        }

        @Override
        public S getOrElse(S defaultValue) {
            return defaultValue;
        }

        @Override
        public S getOrThrow() {
            throw new RuntimeException("getOrThrow() called on a Left value");
        }

        @Override
        public void handle(SideEffect.F<Left<E, S>> leftF, SideEffect.F<Right<E, S>> rightF) {
            leftF.apply(this);
        }

        @Override
        public <T> T match(F<Left<E, S>, ? extends T> leftF, F<Right<E, S>, ? extends T> rightF) {
            return leftF.apply(this);
        }

        @Override
        public <R> R fold(F<E, ? extends R> leftF, F<S, ? extends R> rightF) {
            return leftF.apply(value);
        }

        @Override
        public <T> Either<T, S> mapLeft(F<? super E, ? extends T> f) {
            return Either.left(f.apply(value));
        }

        @Override
        public <T> Either<E, T> map(F<? super S, ? extends T> f) {
            return cast();
        }

        @Override
        public <C> Either<E, C> app(Either<E, F<S, C>> ef) {
            return ef.match(
                    left -> left.cast(),
                    right -> this.cast()
            );
        }

        @Override
        public <T> Either<E, T> flatMap(F<? super S, Either<E, T>> f) {
            return cast();
        }

        @Override
        public void forEach(SideEffect.F<? super S> f) {
        }

        @SuppressWarnings("unchecked")
        private <C> Either<E, C> cast() {
            return (Either<E, C>) this;
        }
    }

    /**
     * Right value.
     * @param <E>       the left-hand type
     * @param <S>       the right-hand type
     */
    final class Right<E, S> implements Either<E, S> {
        public final S value;

        private Right(S value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public String toString() {
            return "Right{value=" + value + '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                Right<?, ?> right = (Right<?, ?>) rhs;
                return Objects.equals(value, right.value);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public E left() {
            throw new RuntimeException("Either.left() called on a Either.Right value");
        }

        @Override
        public S right() {
            return value;
        }

        @Override
        public S getOrElse(S defaultValue) {
            return value;
        }

        @Override
        public S getOrThrow() {
            return value;
        }

        @Override
        public void handle(SideEffect.F<Left<E, S>> leftF, SideEffect.F<Right<E, S>> rightF) {
            rightF.apply(this);
        }

        @Override
        public <T> T match(F<Left<E, S>, ? extends T> leftF, F<Right<E, S>, ? extends T> rightF) {
            return rightF.apply(this);
        }

        @Override
        public <R> R fold(F<E, ? extends R> leftF, F<S, ? extends R> rightF) {
            return rightF.apply(value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Either<T, S> mapLeft(F<? super E, ? extends T> f) {
            return (Either<T, S>) this;
        }

        @Override
        public <T> Either<E, T> map(F<? super S, ? extends T> f) {
            return Either.right(f.apply(value));
        }

        @Override
        public <C> Either<E, C> app(Either<E, F<S, C>> ef) {
            return ef.map(f -> f.apply(value));
        }

        @Override
        public <T> Either<E, T> flatMap(F<? super S, Either<E, T>> f) {
            return f.apply(value);
        }

        @Override
        public void forEach(SideEffect.F<? super S> f) {
            f.apply(value);
        }
    }

    class ApplyBuilder {
        public static class _2<E, A, B> {
            private final Either<E, A> ea;
            private final Either<E, B> eb;

            _2(Either<E, A> ea, Either<E, B> eb) {
                this.ea = ea;
                this.eb = eb;
            }

            public <R> Either<E, R> map(F<A, F<B, R>> f) {
                return eb.app(ea.map(f));
            }

            public <R> Either<E, R> map(Functions.F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Either<E, C> ec) {
                return new _3<C>(ec);
            }

            public class _3<C> {
                private final Either<E, C> ec;

                private _3(Either<E, C> ec) {
                    this.ec = ec;
                }

                public <R> Either<E, R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), ec);
                }

                public <R> Either<E, R> map(Functions.F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Either<E, D> ed) {
                    return new _4<D>(ed);
                }

                public class _4<D> {
                    private final Either<E, D> ed;

                    private _4(Either<E, D> ed) {
                        this.ed = ed;
                    }

                    public <R> Either<E, R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), ed);
                    }

                    public <R> Either<E, R> map(Functions.F4<A, B, C, D, R> f) {
                        return map(f.curry());
                    }

                    public <G> _5<G> and(Either<E, G> eg) {
                        return new _5<G>(eg);
                    }

                    public class _5<G> {
                        private final Either<E, G> eg;

                        private _5(Either<E, G> eg) {
                            this.eg = eg;
                        }

                        public <R> Either<E, R> map(F<A, F<B, F<C, F<D, F<G, R>>>>> f) {
                            return ap(_4.this.map(f), eg);
                        }

                        public <R> Either<E, R> map(Functions.F5<A, B, C, D, G, R> f) {
                            return map(f.curry());
                        }

                        public <H> _6<H> and(Either<E, H> eh) {
                            return new _6<H>(eh);
                        }

                        public class _6<H> {
                            private final Either<E, H> eh;

                            private _6(Either<E, H> eh) {
                                this.eh = eh;
                            }

                            public <R> Either<E, R> map(F<A, F<B, F<C, F<D, F<G, F<H, R>>>>>> f) {
                                return ap(_5.this.map(f), eh);
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
