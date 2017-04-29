package org.funcj.parsec4j;

import org.funcj.util.Functions.*;

import java.util.function.Consumer;

/**
 * A parse result.
 * @param <I>
 * @param <A>
 */
public interface Result<I, A> {
    static <I, A> Result<I, A> success(A result, Input<I> next) {
        return new Success<I, A>(result, next);
    }

    static <I, A> Result<I, A> failure(Input<I> in, SymSet<I> expected) {
        return new Failure<I, A>(in, expected);
    }

    boolean isSuccess();

    A getOrThrow();

    <B> Result<I, B> map(F<A, B> f);

    void handle(Consumer<Success<I, A>> success, Consumer<Failure<I, A>> failure);

    <B> B match(F<Success<I, A>, B> success, F<Failure<I, A>, B> failure);

    class Success<I, A> implements Result<I, A> {
        private final A value;
        private final Input<I> next;

        public Success(A value, Input<I> next) {
            this.value = value;
            this.next = next;
        }

        public A value() {
            return value;
        }

        public Input<I> next() {
            return next;
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
            return 31 * value.hashCode() + next.position().hashCode();
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
        public <B> Result<I, B> map(F<A, B> f) {
            return success(f.apply(value), next);
        }

        @Override
        public void handle(Consumer<Success<I, A>> success, Consumer<Failure<I, A>> failure) {
            success.accept(this);
        }

        @Override
        public <B> B match(F<Success<I, A>, B> success, F<Failure<I, A>, B> failure) {
            return success.apply(this);
        }
    }

    class Failure<I, A> implements Result<I, A> {
        private final Input<I> input;
        private final SymSet<I> expected;

        public Failure(Input<I> input, SymSet<I> expected) {
            this.input = input;
            this.expected = expected;
        }

        public Input<I> input() {
            return input;
        }

        public SymSet<I> expected() {
            return expected;
        }

        @Override
        public String toString() {
            return "Failure{" +
                "input=" + input +
                ", expected=" + expected +
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
            throw new RuntimeException("Failure at position " + input.position());
        }

        @Override
        public <B> Result<I, B> map(F<A, B> f) {
            return this.cast();
        }

        @Override
        public void handle(Consumer<Success<I, A>> success, Consumer<Failure<I, A>> failure) {
            failure.accept(this);
        }

        @Override
        public <B> B match(F<Success<I, A>, B> success, F<Failure<I, A>, B> failure) {
            return failure.apply(this);
        }
    }
}
