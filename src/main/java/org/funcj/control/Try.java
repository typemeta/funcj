package org.funcj.control;

import org.funcj.data.IList;
import org.funcj.util.Functions.*;
import org.funcj.util.FunctionsEx;

import java.util.*;
import java.util.function.Consumer;

/**
 * Simple monadic wrapper for computations which result in either a successfully computed value
 * or an error.
 * Try is effectively a discriminated union of {@code Success} (which wraps a value)
 * and {@code Failure} (which wraps an exception).
 * @param <T> successful result type
 */
public interface Try<T> {

    /**
     * Create a {@code Success} value that wraps a successful result.
     * @param value successful result to be wrapped
     * @param <T> successful result type
     * @return a {@code Success} value
     */
    static <T> Try<T> success(T value) {
        return new Success<T>(value);
    }

    /**
     * Create a {@code Failure} value that wraps a error result.
     * @param error error result
     * @param <T> successful result type
     * @return a {@code Failure} value
     */
    static <T> Try<T> failure(Exception error) {
        return new Failure<T>(error);
    }

    /**
     * Create a {@code Try} value from a function which either yields a result or throws.
     * @param f function which may throw
     * @param <T> successful result type
     * @return {@code Try} value which wraps the function result
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
     * @param tf function wrapped in a {@code Try}
     * @param ta function argument wrapped in a {@code Try}
     * @param <A> function argument type
     * @param <B> function return type
     * @return the result of applying the function to the argument, wrapped in a {@code Try}
     */
    static <A, B> Try<B> ap(Try<F<A, B>> tf, Try<A> ta) {
        return ta.apply(tf);
    }

    /**
     * Applicative function application.
     * @param f function
     * @param ta function argument wrapped in a {@code Try}
     * @param <A> function argument type
     * @param <B> function return type
     * @return the result of applying the function to the argument, wrapped in a {@code Try}
     */
    static <A, B> Try<B> ap(F<A, B> f, Try<A> ta) {
        return ta.map(f);
    }

    /**
     * Standard applicative traversal.
     * @param lt list of {@code Try} values
     * @param f function to be applied to each {@code Try} in the list
     * @param <T> type of list elements
     * @param <U> type wrapped by the {@code Try} returned by the function
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <T, U> Try<IList<U>> traverse(IList<T> lt, F<T, Try<U>> f) {
        return lt.foldRight(
                (t, tlt) -> f.apply(t).apply(tlt.map(l -> l::add)),
                success(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     * @param lt list of {@code Try} values
     * @param <T> type of list elements
     * @return a {@code Try} which wraps an {@link org.funcj.data.IList} of values
     */
    static <T> Try<IList<T>> sequence(IList<Try<T>> lt) {
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
     * Either return the wrapped value if it's a {@code Success}, otherwise return the supplied default value.
     * @param defaultValue value to be returned if this is a failure value.
     * @return the success result value if it's a {@code Success}, otherwise return the supplied default value.
     */
    T getOrElse(T defaultValue);

    /**
     * Either return the wrapped value if it's a {@code Success}, otherwise throw the result exception.
     * @return the wrapped value if it's a {@code Success}
     * @throws Exception if the wrapped value is a {@code Failure}
     */
    T getOrThrow() throws Exception;

    /**
     * Similar to getOrThrow but will throw a RuntimeException.
     * @return the wrapped value if it's a {@code Success}
     */
    T get();

    /**
     * Push the result to a {@link java.util.function.Consumer}.
     * @param success consumer to be applied to {@code Success} values
     * @param failure consumer to be applied to {@code Failure} values
     */
    void handle(Consumer<Success<T>> success, Consumer<Failure<T>> failure);

    /**
     * Map a function over the value.
     * @param success function to be applied to {@code Success} values
     * @param failure function to be applied to {@code Failure} values
     * @param <R> return type of functions
     * @return the result of applying the function
     */
    <R> R match(F<Success<T>, ? extends R> success, F<Failure<T>, ? extends R> failure);

    /**
     * Functor function application.
     * If this is a success then apply the function to the value.
     */
    <R> Try<R> map(F<? super T, ? extends R> f);

    /**
     * Applicative function application.
     */
    <R> Try<R> apply(Try<F<T, R>> tf);

    /**
     * Monadic bind/flatMap.
     * If this is a success then apply the function to the value and return the result,
     * Otherwise return the failure result.
     */
    <R> Try<R> flatMap(F<? super T, Try<R>> f);

    /**
     * Variant of flatMap which ignores the supplied value.
     */
    default <R> Try<R> flatMap(F0<Try<R>> f) {
        return flatMap(u -> f.apply());
    }

    /**
     * Convert the value into an Optional.
     */
    Optional<T> asOptional();

    /**
     * Successful result type, which wraps the result value.
     */
    final class Success<T> implements Try<T> {

        public final T value;

        private Success(T value) {
            this.value = Objects.requireNonNull(value);
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
        public T getOrThrow() throws Exception {
            return value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void handle(Consumer<Success<T>> success, Consumer<Failure<T>> failure) {
            success.accept(this);
        }

        @Override
        public <R> R match(F<Success<T>, ? extends R> success, F<Failure<T>, ? extends R> failure) {
            return success.apply(this);
        }

        @Override
        public <R> Try<R> map(F<? super T, ? extends R> f) {
            return success(f.apply(value));
        }

        @Override
        public <R> Try<R> apply(Try<F<T, R>> tf) {
            return tf.map(f -> f.apply(value));
        }

        @Override
        public <R> Try<R> flatMap(F<? super T, Try<R>> f) {
            return f.apply(value);
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.of(value);
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == null)
                return false;

            if (!(obj instanceof Success<?>))
                return false;

            final Success<?> rhs = (Success<?>)obj;

            return value.equals(rhs.value);
        }
    }

    /**
     * Unsuccessful result type. Wraps the result exception.
     */
    final class Failure<T> implements Try<T> {

        public final Exception error;

        private Failure(Exception error) {
            this.error = Objects.requireNonNull(error);
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
        public T getOrThrow() throws Exception {
            throw error;
        }

        @Override
        public T get() {
            throw new RuntimeException(error);
        }

        @Override
        public void handle(Consumer<Success<T>> success, Consumer<Failure<T>> failure) {
            failure.accept(this);
        }

        @Override
        public <R> R match(F<Success<T>, ? extends R> success, F<Failure<T>, ? extends R> failure) {
            return failure.apply(this);
        }

        @Override
        public <R> Try<R> map(F<? super T, ? extends R> f) {
            return cast();
        }

        @Override
        public <R> Try<R> apply(Try<F<T, R>> tf) {
            return cast();
        }

        @Override
        public <R> Try<R> flatMap(F<? super T, Try<R>> f) {
            return cast();
        }

        @Override
        public Optional<T> asOptional() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "Failure(" + error + ")";
        }

        @Override
        public int hashCode() {
            return error.hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == null)
                return false;

            if (!(obj instanceof Failure<?>))
                return false;

            final Failure<?> rhs = (Failure<?>)obj;

            // In general the equals() method for Exception classes isn't implemented,
            // which means we get object equality. This is rarely useful so here
            // we instead compare the string representations.
            return error.toString().equals(rhs.error.toString());
        }

        private <U> Try<U> cast() {
            return (Try<U>) this;
        }
    }
}
