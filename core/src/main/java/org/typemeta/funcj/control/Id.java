package org.typemeta.funcj.control;

import org.typemeta.funcj.functions.Functions.F;

import java.util.Objects;

/**
 * The identity monad.
 * @param <T>       the wrapped value type
 */
public class Id<T> {

    public static <T> Id<T> pure(T value) {
        return new Id<>(value);
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
    public static <A, B> Id<B> tailRecM(A a, F<A, Id<Either<A, B>>> f) {
        while (true) {
            final Either<A, B> e = f.apply(a).value;
            if (e instanceof Either.Left) {
                final Either.Left<A, B> left = (Either.Left<A, B>)e;
                a = left.value;
            } else {
                final Either.Right<A, B> right = (Either.Right<A, B>)e;
                return Id.pure(right.value);
            }
        }
    }

    public final T value;

    public Id(T value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String toString() {
        return "Id{" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id<?> id = (Id<?>) o;
        return Objects.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public <U> Id<U> map(F<T, U> f) {
        return new Id<>(f.apply(value));
    }

    public <U> Id<U> app(Id<F<T, U>> idF) {
        return Id.pure(idF.value.apply(this.value));
    }
    
    public <U> Id<U> flatMap(F<T, Id<U>> f) {
        return f.apply(value);
    }
}
