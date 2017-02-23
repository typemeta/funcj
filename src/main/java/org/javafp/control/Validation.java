package org.javafp.control;

import org.javafp.data.IList;
import org.javafp.util.Functions;
import org.javafp.util.Functions.F;

public interface Validation<E, T> {
    static <E, T> Validation<E, T> success(T result) {
        return new Success<E, T>(result);
    }

    static <E, T> Validation<E, T> failure(IList<E> errors) {
        return new Failure<E, T>(errors);
    }

    <U> Validation<E, U> map(F<T, U> f);

    class Success<E, T> implements Validation<E, T> {
        public final T result;

        public Success(T result) {
            this.result = result;
        }

        @Override
        public <U> Validation<E, U> map(F<T, U> f) {
            return null;
        }
    }

    class Failure<E, T> implements Validation<E, T> {
        public final IList<E> errors;

        public Failure(IList<E> errors) {
            this.errors = errors;
        }

        @Override
        public <U> Validation<E, U> map(F<T, U> f) {
            return null;
        }
    }
}
