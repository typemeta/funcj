package org.typemeta.funcj.control;

import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.functions.Functions.*;
import org.typemeta.funcj.functions.SideEffect;
import org.typemeta.funcj.util.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * A type representing a value which may be either present or absent.
 * <p>
 * A {@code Option<T>} value is either
 * the sub-type {@code Option.None<T>} which indicates an absent value, or
 * the sub-type {@code Option.Some<T>} which wraps a value of type T.
 * <p>
 * Option is biased towards the {@code Option.Some<T>}, meaning that map, apply and flatMap operate on the
 * {@code Some} value and bypass the {@code None} value.
 * <p>
 * Null values are not allowed.
 * @param <T>       the successful result type
 */
public interface Option<T> {

    /**
     * Create a {@code Some} value that wraps a successful result.
     * @param value     the successful result to be wrapped
     * @param <T>       the successful result type
     * @return          a {@code Some} value
     * @throws          NullPointerException if {@code value} is null
     */
    static <T> Option<T> some(T value) {
        return new Some<T>(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Option<T> none() {
        return (Option<T>) None.INSTANCE;
    }

    static <T> Option<T> fromOptional(Optional<T> opt) {
        return opt.map(Option::some).orElse(none());
    }

    static <T> Option<T> ofNullable(T value) {
        return value == null ?
                none() :
                some(value);
    }

    /**
     * Applicative function application.
     * @param tf        the function wrapped in a {@code Option}
     * @param ta        the function argument wrapped in a {@code Option}
     * @param <A>       the function argument type
     * @param <B>       the function return type
     * @return          the result of applying the function to the argument, wrapped in a {@code Option}
     */
    static <A, B> Option<B> ap(Option<F<A, B>> tf, Option<A> ta) {
        return ta.app(tf);
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param lt        the list of values
     * @param fou       the function to be applied to each value in the list
     * @param <T>       the type of list elements
     * @param <U>       the type wrapped by the {@code Option} returned by the function
     * @return          a {@code Option} which wraps an {@link IList} of values
     */
    static <T, U> Option<IList<U>> traverse(IList<T> lt, F<T, Option<U>> fou) {
        return lt.foldRight(
                (t, olu) -> olu.app(fou.apply(t).map(b -> lu -> lu.add(b))),
                some(IList.empty())
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
     * @return          a {@code Option} which wraps an {@link List} of values
     */
    static <T, U> Option<List<U>> traverse(List<T> lt, F<T, Option<U>> f) {
        return sequence(Functors.map(f, lt));
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code Option} into a {@code Option} of an {@code IList},
     * by composing each successive {@code Option} using the {@link Option#app(Option)} method.
     * @param lot       the list of {@code Option} values
     * @param <T>       the value type of the {@code Option}s in the list
     * @return          a {@code Option} which wraps an {@link IList} of values
     */
    static <T> Option<IList<T>> sequence(IList<Option<T>> lot) {
        return lot.foldRight(
            (ot, olt) -> olt.app(ot.map(t -> lt -> lt.add(t))),
                some(IList.empty())
        );
    }

   /**
     * Variation of {@link Try#sequence(IList)} for a {@link List}.
     * @param lot       the list of {@code Option} values
     * @param <T>       the value type of the {@code Option}s in the stream
     * @return          a {@code Option} which wraps an {@link Stream} of values
     */
    static <T> Option<List<T>> sequence(List<Option<T>> lot) {
        final Option<List<T>> res = Folds.foldRight(
                (ot, olt) -> olt.app(ot.map(t -> lt -> {lt.add(t); return lt;})),
                some(new ArrayList<>(lot.size())),
                lot
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
     * @param <A>       the starting value type
     * @param <B>       the final value type
     * @return          the final value
     */
    static <A, B> Option<B> tailRecM(A a, F<A, Option<Either<A, B>>> f) {
        while (true) {
            final Option<Either<A, B>> oe = f.apply(a);
            if (oe instanceof Option.None) {
                return none();
            } else {
                final Option.Some<Either<A, B>> ose = (Option.Some<Either<A, B>>)oe;
                if (ose.value instanceof Either.Left) {
                    final Either.Left<A, B> left = (Either.Left<A, B>) ose.value;
                    a = left.value;
                } else {
                    final Either.Right<A, B> right = (Either.Right<A, B>) ose.value;
                    return Option.some(right.value);
                }
            }
        }
    }

    /**
     * Indicates if this is a {@code Some} value.
     * @return          true if this value is a {@code Some} value
     */
    boolean isPresent();

    /**
     * Downgrade this value into an {@link Optional}.
     * @return          a populated {@code Optional} value if this is a {Code Some} value,
     *                  otherwise an empty {@code Optional}
     */
    Optional<T> asOptional();

    /**
     * Either return the wrapped value if it's a {@code Some}, otherwise return the given default value.
     * @param defaultValue value to be returned if this is a failure value.
     * @return          the success result value if it's a {@code Some}, otherwise return the given default value.
     */
    T orElseGet(T defaultValue);

    /**
     * Return the wrapped value if it's a {@code Success}, otherwise throw the result exception.
     * @return          the wrapped value if it's a {@code Success}
     */
    T orElseThrow();

    /**
     * Return the wrapped value if it's a {@code Some},
     * otherwise throw the exception provided by calling {@code exSupp}.
     * @param exSupp    the exception supplier
     * @param <X>       the exception type
     * @return          the wrapped value if this is a {@code Some}
     * @throws X        if no value is present
     */
    <X extends Throwable> T orElseThrow(F0<X> exSupp) throws X;

    T orElse(T value);

    T orElseGet(F0<T> getter);

    /**
     * Return the wrapped value if it's a {@code Some}, otherwise throw a RuntimeException.
     * @return          the wrapped value if this is a {@code Some}
     */
    T get();

    /**
     * Push the result to a {@link SideEffect.F}.
     * @param noneF     the side-effect to be applied to {@code None} values
     * @param someF     the side-effect to be applied to {@code Some} values
     */
    void handle(SideEffect.F0 noneF, SideEffect.F<Some<T>> someF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param noneF     the function to be applied to the {@code None} value
     * @param someF     the function to be applied to the {@code Some} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R match(F0<? extends R> noneF, F<Some<T>, ? extends R> someF);

    /**
     * Apply one of two functions to this value, according to the type of value.
     * @param noneF     the function to be applied to the {@code None} value
     * @param someF     the function to be applied to the {@code Some} value
     * @param <R>       the return type of functions
     * @return          the result of applying either function
     */
    <R> R fold(F0<? extends R> noneF, F<? super T, ? extends R> someF);

    /**
     * Functor function application.
     * If this value is a {@code Some} then apply the function to the value,
     * otherwise if this is a {@code None} then leave it untouched.
     * @param f         the function to be applied
     * @param <U>       the function return type
     * @return          a {@code Option} that wraps the function result, or the original none
     */
    <U> Option<U> map(F<? super T, ? extends U> f);

    /**
     * Applicative function application (inverted).
     * If the {@code tf} parameter is a {@code Some} value and this is a {@code Some} value,
     * then apply the function wrapped in the {@code tf} to this.
     * @param tf        the function wrapped in a {@code Option}
     * @param <U>       the return type of function
     * @return          a {@code Option} wrapping the result of applying the function, or a {@code None} value
     */
    <U> Option<U> app(Option<F<T, U>> tf);

    /**
     * Monadic bind/flatMap.
     * If this is a {@code Some} then apply the function to the value and return the result,
     * otherwise return the {@code None} result.
     * @param f         the function to be applied
     * @param <U>       the type parameter to the {@code Option} returned by the function
     * @return          the result of combining this value with the function {@code f}
     */
    <U> Option<U> flatMap(F<? super T, Option<U>> f);

    /**
     * Variant of flatMap which ignores this value.
     * @param f         the function to be invoked
     * @param <U>       the type parameter to the {@code Option} returned by the function
     * @return          the result of combining this value with the function {@code f}
     */
    default <U> Option<U> flatMap(F0<Option<U>> f) {
        return flatMap(u -> f.apply());
    }

    /**
     * Apply a side-effect operation to this value
     * If this value is a {@code Some} then apply the function to the value,
     * otherwise do nothing.
     * @param f         the function to be applied
     */
    void forEach(SideEffect.F<? super T> f);

    /**
     * Builder API for chaining together n {@code Option}s,
     * and applying an n-ary function at the end.
     * @param tb        the next {@code Option} value to chain
     * @param <U>       the successful result type for next {@code Option}
     * @return          the next builder
     */
    default <U> ApplyBuilder._2<T, U> and(Option<U> tb) {
        return new ApplyBuilder._2<T, U>(this, tb);
    }

    /**
     * Some type.
     * @param <T>       the successful result type
     */
    final class Some<T> implements Option<T> {

        public final T value;

        private Some(T value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (!(obj instanceof Option.Some<?>)) {
                return false;
            } else {
                final Some<?> rhs = (Some<?>) obj;
                return value.equals(rhs.value);
            }
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public T orElseGet(T defaultValue) {
            return value;
        }

        @Override
        public T orElseThrow() {
            return value;
        }

        @Override
        public T orElse(T value) {
            return this.value;
        }

        @Override
        public T orElseGet(F0<T> getter) {
            return this.value;
        }

        @Override
        public <X extends Throwable> T orElseThrow(F0<X> exSupp) throws X {
            return value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void handle(SideEffect.F0 noneF, SideEffect.F<Some<T>> someF) {
            someF.apply(this);
        }

        @Override
        public <R> R match(F0<? extends R> noneF, F<Some<T>, ? extends R> someF) {
            return someF.apply(this);
        }

        @Override
        public <R> R fold(F0<? extends R> noneF, F<? super T, ? extends R> someF) {
            return someF.apply(value);
        }

        @Override
        public <U> Option<U> map(F<? super T, ? extends U> f) {
            return some(f.apply(value));
        }

        @Override
        public <U> Option<U> app(Option<F<T, U>> tf) {
            return tf.map(f -> f.apply(value));
        }

        @Override
        public <U> Option<U> flatMap(F<? super T, Option<U>> f) {
            return f.apply(value);
        }

        @Override
        public void forEach(SideEffect.F<? super T> f) {
            f.apply(value);
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.of(value);
        }
    }

    /**
     * None type
     * @param <T>       the successful result type
     */
    final class None<T> implements Option<T> {

        static final None<Object> INSTANCE = new None<Object>();

        private None() {
        }

        @Override
        public String toString() {
            return "None";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else {
                return obj instanceof Option.None<?>;
            }
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public T orElseGet(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T orElseThrow() {
            throw new NoSuchElementException("Option.orElseThrow() called on Option.None");
        }

        @Override
        public T orElse(T value) {
            return value;
        }

        @Override
        public T orElseGet(F0<T> getter) {
            return getter.apply();
        }

        @Override
        public <X extends Throwable> T orElseThrow(F0<X> exSupp) throws X {
            throw exSupp.apply();
        }

        @Override
        public T get() {
            throw new RuntimeException("Option.orElseThrow() called on Option.None");
        }

        @Override
        public void handle(SideEffect.F0 noneF, SideEffect.F<Some<T>> someF) {
            noneF.apply();
        }

        @Override
        public <R> R match(F0<? extends R> noneF, F<Some<T>, ? extends R> someF) {
            return noneF.apply();
        }

        @Override
        public <R> R fold(F0<? extends R> noneF, F<? super T, ? extends R> someF) {
            return noneF.apply();
        }

        @Override
        public <U> Option<U> map(F<? super T, ? extends U> f) {
            return cast();
        }

        @Override
        public <U> Option<U> app(Option<F<T, U>> tf) {
            return this.cast();
        }

        @Override
        public <U> Option<U> flatMap(F<? super T, Option<U>> f) {
            return cast();
        }

        @Override
        public void forEach(SideEffect.F<? super T> f) {
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        private <U> Option<U> cast() {
            return (Option<U>) this;
        }
    }

    class ApplyBuilder {
        public static class _2<A, B> {
            private final Option<A> ta;
            private final Option<B> tb;

            _2(Option<A> ta, Option<B> tb) {
                this.ta = ta;
                this.tb = tb;
            }

            public <R> Option<R> map(F<A, F<B, R>> f) {
                return tb.app(ta.map(f));
            }

            public <R> Option<R> map(F2<A, B, R> f) {
                return map(f.curry());
            }

            public <C> _3<C> and(Option<C> tc) {
                return new _3<C>(tc);
            }

            public class _3<C> {
                private final Option<C> tc;

                private _3(Option<C> tc) {
                    this.tc = tc;
                }

                public <R> Option<R> map(F<A, F<B, F<C, R>>> f) {
                    return ap(_2.this.map(f), tc);
                }

                public <R> Option<R> map(F3<A, B, C, R> f) {
                    return map(f.curry());
                }

                public <D> _4<D> and(Option<D> td) {
                    return new _4<D>(td);
                }

                public class _4<D> {
                    private final Option<D> td;

                    private _4(Option<D> td) {
                        this.td = td;
                    }

                    public <R> Option<R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                        return ap(_3.this.map(f), td);
                    }

                    public <R> Option<R> map(F4<A, B, C, D, R> f) {
                        return map(f.curry());
                    }

                    public <E> _5<E> and(Option<E> te) {
                        return new _5<E>(te);
                    }

                    public class _5<E> {
                        private final Option<E> te;

                        private _5(Option<E> te) {
                            this.te = te;
                        }

                        public <R> Option<R> map(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
                            return ap(_4.this.map(f), te);
                        }

                        public <R> Option<R> map(F5<A, B, C, D, E, R> f) {
                            return map(f.curry());
                        }

                        public <G> _6<G> and(Option<G> tg) {
                            return new _6<G>(tg);
                        }

                        public class _6<G> {
                            private final Option<G> tg;

                            private _6(Option<G> tg) {
                                this.tg = tg;
                            }

                            public <R> Option<R> map(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
                                return ap(_5.this.map(f), tg);
                            }

                            public <R> Option<R> map(F6<A, B, C, D, E, G, R> f) {
                                return map(f.curry());
                            }
                        }
                    }
                }
            }
        }
    }
}
