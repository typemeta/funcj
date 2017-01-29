package org.javafp.parsec4j;

import org.javafp.util.Functions;

/**
 * A parse result.
 * @param <I>
 * @param <A>
 */
public interface Result<I, A> {
    static <I, A> Result<I, A> success(A result, int next) {
        return new Success<I, A>(result, next);
    }

    static <I, A> Result<I, A> failure(int pos) {
        return new Failure<I, A>(pos);
    }

    boolean isSuccess();

    A getOrThrow();

    <B> Result<I, B> map(Functions.F<A, B> f);

    <B> B match(Functions.F<Success<I, A>, B> success, Functions.F<Failure<I, A>, B> failure);

    class Success<I, A> implements Result<I, A> {
        public final A value;
        public final int next;

        public Success(A value, int next) {
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Success{" +
                "value=" + value +
                ", next=" + next +
                '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;

            Success<?, ?> rhsT = (Success<?, ?>) rhs;

            return next == rhsT.next && value.equals(rhsT.value);
        }

        @Override
        public int hashCode() {
            return 31 * value.hashCode() + next;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public A getOrThrow() {
            return value;
        }

        @Override
        public <B> Result<I, B> map(Functions.F<A, B> f) {
            return success(f.apply(value), next);
        }

        @Override
        public <B> B match(Functions.F<Success<I, A>, B> success, Functions.F<Failure<I, A>, B> failure) {
            return success.apply(this);
        }
    }

    class Failure<I, A> implements Result<I, A> {
        public final int pos;

        public Failure(int pos) {
            this.pos = pos;
        }

        @Override
        public String toString() {
            return "Failure{" +
                "pos=" + pos +
                '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;

            Failure<?, ?> rhsT = (Failure<?, ?>) rhs;

            return pos == rhsT.pos;
        }

        public <T> Failure<I, T> cast() {
            return (Failure<I, T>) this;
        }

        @Override
        public int hashCode() {
            return pos;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public A getOrThrow() {
            throw new RuntimeException("Failure at position " + pos);
        }

        @Override
        public <B> Result<I, B> map(Functions.F<A, B> f) {
            return this.cast();
        }

        @Override
        public <B> B match(Functions.F<Success<I, A>, B> success, Functions.F<Failure<I, A>, B> failure) {
            return failure.apply(this);
        }
    }
}
