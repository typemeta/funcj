package org.javafp.data;

import org.javafp.data.Functions.*;

import java.util.*;

/**
 * Standard tagged union type over two types.
 * A value of Either&lt;A, B&gt; wraps either a value of type A or a value of type B.
 * In order to act as a monad Either has right-bias,
 * meaning that the monadic flatMap method acts on the Right value and simply propagates the Left value.
 * @param <A> left-hand type
 * @param <B> right-hand type
 */
public interface Either<A, B> {
    static <A, B> Either<A, B> left(A value) {
        return new Left<A, B>(value);
    }

    static <A, B> Either<A, B> right(B value) {
        return new Right<A, B>(value);
    }

    boolean isLeft();

    boolean isRight();

    Optional<A> left();

    Optional<B> right();

    <T> T match(F<Left<A, B>, ? extends T> left, F<Right<A, B>, ? extends T> right);

    <T> Either<T, B> mapLeft(F<? super A, ? extends T> f);

    <T> Either<A, T> mapRight(F<? super B, ? extends T> f);

    <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f);

    Either<B, A> swap();

    class Left<A, B> implements Either<A, B> {
        public final A value;

        private Left(A value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public Optional<A> left() {
            return Optional.of(value);
        }

        @Override
        public Optional<B> right() {
            return Optional.empty();
        }

        @Override
        public <T> T match(F<Left<A, B>, ? extends T> left, F<Right<A, B>, ? extends T> right) {
            return left.apply(this);
        }

        @Override
        public <T> Either<T, B> mapLeft(F<? super A, ? extends T> f) {
            return Either.<T, B>left(f.apply(value));
        }

        @Override
        public <T> Either<A, T> mapRight(F<? super B, ? extends T> f) {
            return (Either<A, T>) this;
        }

        @Override
        public <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f) {
            return (Either<A, T>) this;
        }

        @Override
        public Either<B, A> swap() {
            return Either.right(value);
        }
    }

    class Right<A, B> implements Either<A, B> {
        public final B value;

        private Right(B value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public Optional<A> left() {
            return Optional.empty();
        }

        @Override
        public Optional<B> right() {
            return Optional.of(value);
        }

        @Override
        public <T> T match(F<Left<A, B>, ? extends T> left, F<Right<A, B>, ? extends T> right) {
            return right.apply(this);
        }

        @Override
        public <T> Either<T, B> mapLeft(F<? super A, ? extends T> f) {
            return (Either<T, B>) this;
        }

        @Override
        public <T> Either<A, T> mapRight(F<? super B, ? extends T> f) {
            return Either.right(f.apply(value));
        }

        @Override
        public <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f) {
            return f.apply(value);
        }

        @Override
        public Either<B, A> swap() {
            return Either.left(value);
        }
    }
}
