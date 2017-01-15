package org.javafp.data;

import org.javafp.data.Functions.*;
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

    A leftOrThrow();

    B rightOrThrow();

    <T> T map(F<? super A, ? extends T> left, F<? super B, ? extends T> right);

    <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f);

    Either<B, A> swap();

    class Left<A, B> implements Either<A, B> {
        private final A value;

        Left(A value) {
            this.value = value;
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
        public A leftOrThrow() {
            return value;
        }

        @Override
        public B rightOrThrow() {
            throw new RuntimeException("Right value is not present");
        }

        @Override
        public <T> T map(F<? super A, ? extends T> left, F<? super B, ? extends T> right) {
            return left.apply(value);
        }

        @Override
        public <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f) {
            return Either.left(value);
        }

        @Override
        public Either<B, A> swap() {
            return Either.right(value);
        }
    }

    class Right<A, B> implements Either<A, B> {
        private final B value;

        Right(B value) {
            this.value = value;
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
        public A leftOrThrow() {
            throw new RuntimeException("Left value is not present");
        }

        @Override
        public B rightOrThrow() {
            return value;
        }

        @Override
        public <T> T map(F<? super A, ? extends T> left, F<? super B, ? extends T> right) {
            return right.apply(value);
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
