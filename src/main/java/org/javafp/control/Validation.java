package org.javafp.control;

import org.javafp.data.IList;
import org.javafp.util.Functions.F;

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

    void handle(Consumer<Success<E, T>> success, Consumer<Failure<E, T>> failure);

    <U> U match(F<Success<E, T>, U> success, F<Failure<E, T>, U> failure);

    <U> Validation<E, U> map(F<T, U> f);

    <U> Validation<E, U> apply(Validation<E, F<T, U>> vf);

    <U> Validation<E, U> flatMap(F<T, Validation<E, U>> f);

    class Success<E, T> implements Validation<E, T> {
        public final T value;

        public Success(T value) {
            this.value = value;
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
}
