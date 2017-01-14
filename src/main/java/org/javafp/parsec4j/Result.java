package org.javafp.parsec4j;

import org.javafp.data.Functions;

/**
 * A parse result.
 * @param <I>
 * @param <A>
 */
public interface Result<I, A> {
    static <I, A> Result<I, A> success(A result, Input<I> rest) {
        return new Success<I, A>(result, rest);
    }

    static <I, A> Result<I, A> failure(Input<I> input) {
        return new Failure<I, A>(input);
    }

    boolean isSuccess();

    A getOrThrow();

    <B> Result<I, B> map(Functions.F<A, B> f);

    <B> B match(Functions.F<Success<I, A>, B> success, Functions.F<Failure<I, A>, B> failure);

    class Success<I, A> implements Result<I, A> {
        public final A value;
        public final Input<I> tail;

        public Success(A value, Input<I> tail) {
            this.value = value;
            this.tail = tail;
        }

        @Override
        public String toString() {
            return "Success{" +
                "value=" + value +
                ", tail=" + tail +
                '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;

            Success<?, ?> rhsT = (Success<?, ?>) rhs;

            return tail == rhsT.tail && value.equals(rhsT.value);
        }

        @Override
        public int hashCode() {
            return 31 * value.hashCode() + tail.hashCode();
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
            return success(f.apply(value), tail);
        }

        @Override
        public <B> B match(Functions.F<Success<I, A>, B> success, Functions.F<Failure<I, A>, B> failure) {
            return success.apply(this);
        }
    }

    class Failure<I, A> implements Result<I, A> {
        public final Input<I> input;

        public Failure(Input<I> input) {
            this.input = input;
        }

        @Override
        public String toString() {
            return "Failure{" +
                "input=" + input +
                '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;

            Failure<?, ?> rhsT = (Failure<?, ?>) rhs;

            return input == rhsT.input;
        }

        public <T> Failure<I, T> cast() {
            return (Failure<I, T>) this;
        }

        @Override
        public int hashCode() {
            return input.hashCode();
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public A getOrThrow() {
            throw new RuntimeException("Failure at position " + input);
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
