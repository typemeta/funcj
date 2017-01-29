package org.javafp.data;

import org.javafp.util.Functions.F;

/**
 * Validated contains either a successful result or a list of errors
 * @param <E> Error type
 * @param <T> Successful result type
 */
public interface Validated<E, T> {
    static <E, T> Validated<E, T> success(T value) {
        return new Success<E, T>(value);
    }

    static <E, T> Validated<E, T> failure(E error) {
        return new Failure<E, T>(IList.of(error));
    }

    static <E, T> Validated<E, T> failure(IList<E> errors) {
        return new Failure<E, T>(errors);
    }

    static <E, T, U> Validated<E, U> apply(Validated<E, F<T, U>> vf, Validated<E, T> vt) {
        return vt.apply(vf);
    }

    /**
     * Standard applicative traversal.
     */
    static <E, T, U> Validated<E, IList<U>> traverse(IList<T> lt, F<T, Validated<E, U>> f) {
        return lt.foldr(
            (vt, vlu) -> f.apply(vt).apply(vlu.apply(l -> l::add)),
            success(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     */
    static <E, T> Validated<E, IList<T>> sequence(IList<Validated<E, T>> lvt) {
        return lvt.foldr(
            (vt, vlt) -> vt.apply(vlt.apply(l -> l::add)),
            success(IList.nil())
        );
    }

    <U> U match(F<Success<E, T>, U> success, F<Failure<E, T>, U> failure);

    <U> Validated<E, U> map(F<T, U> f);

    <U> Validated<E, U> apply(Validated<E, F<T, U>> vf);

    default <U> Validated<E, U> apply(F<T, U> f) {
        return apply(success(f));
    }

    <U> Validated<E, U> flatMap(F<T, Validated<E, U>> f);

    final class Success<E, T> implements Validated<E, T> {
        public final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public <U> U match(F<Success<E, T>, U> success, F<Failure<E, T>, U> failure) {
            return success.apply(this);
        }

        @Override
        public <U> Validated<E, U> map(F<T, U> f) {
            return success(f.apply(value));
        }

        @Override
        public <U> Validated<E, U> apply(Validated<E, F<T, U>> vf) {
            return vf.map(f -> f.apply(value));
        }

        @Override
        public <U> Validated<E, U> flatMap(F<T, Validated<E, U>> f) {
            return f.apply(value);
        }
    }

    final class Failure<E, T> implements Validated<E, T> {
        public final IList<E> errors;

        public Failure(IList<E> errors) {
            this.errors = errors;
        }

        @Override
        public <U> U match(F<Success<E, T>, U> success, F<Failure<E, T>, U> failure) {
            return failure.apply(this);
        }

        @Override
        public <U> Validated<E, U> map(F<T, U> f) {
            return cast();
        }

        @Override
        public <U> Validated<E, U> apply(Validated<E, F<T, U>> vf) {
            return vf.<Validated<E, U>>match(
                succ -> cast(),
                fail -> Validated.failure(IList.concat(errors, fail.errors))
            );
        }

        @Override
        public <U> Validated<E, U> flatMap(F<T, Validated<E, U>> f) {
            return cast();
        }

        <U> Failure<E, U> cast() {
            return (Failure<E, U>) this;
        }
    }
}
