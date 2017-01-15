package org.javafp.data;

import org.javafp.data.Functions.F;

public interface Try<T> {
    static <T> Try<T> success(T value) {
        return new Success<T>(value);
    }

    static <T> Try<T> failure(Exception error) {
        return new Failure<T>(error);
    }

    static <T> Try<T> of(FunctionsEx.F0<T> f) {
        try {
            return new Success<T>(f.apply());
        } catch (Exception ex) {
            return new Failure<T>(ex);
        }
    }

    T getOrThrow() throws Exception;

    <U> U match(F<Success<T>, U> success, F<Failure<T>, U> failure);

    <U> Try<U> map(F<T, U> f);

    <U> Try<U> flatMap(F<T, Try<U>> f);

    public class Success<T> implements Try<T> {
        public final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public T getOrThrow() {
            return value;
        }

        @Override
        public <U> U match(F<Success<T>, U> success, F<Failure<T>, U> failure) {
            return success.apply(this);
        }

        @Override
        public <U> Try<U> map(F<T, U> f) {
            return new Success<U>(f.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(F<T, Try<U>> f) {
            return f.apply(value);
        }
    }

    public class Failure<T> implements Try<T> {
        public final Exception error;

        public Failure(Exception error) {
            this.error = error;
        }

        public <U> Failure<U> cast() {
            return (Failure<U>)this;
        }

        @Override
        public T getOrThrow() throws Exception {
            throw error;
        }

        @Override
        public <U> U match(F<Success<T>, U> success, F<Failure<T>, U> failure) {
            return failure.apply(this);
        }

        @Override
        public <U> Try<U> map(F<T, U> f) {
            return cast();
        }

        @Override
        public <U> Try<U> flatMap(F<T, Try<U>> f) {
            return cast();
        }
    }
}
