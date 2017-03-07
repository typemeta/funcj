package org.javafp.control;

import org.javafp.data.IList;
import org.javafp.util.Functions.*;

import java.util.*;

/**
 * Standard tagged union type over two types.
 * A value of Either&lt;A, B&gt; wraps either a value of type A or a value of type B.
 * Either has right-bias, meaning that map, apply and flatMap operate on the
 * Right value and bypass the Left value.
 * @param <A> left-hand type (possibly an error type)
 * @param <B> right-hand type (possibly a success type)
 */
public interface Either<A, B> {
    static <A, B> Either<A, B> left(A value) {
        return new Left<A, B>(value);
    }

    static <A, B> Either<A, B> right(B value) {
        return new Right<A, B>(value);
    }

    /**
     * Standard applicative traversal.
     */
    static <T, A, B> Either<A, IList<B>> traverse(IList<T> lt, F<T, Either<A, B>> f) {
        return lt.foldr(
            (t, elt) -> f.apply(t).apply(elt.apply(l -> l::add)),
            right(IList.nil())
        );
    }

    /**
     * Standard applicative sequencing.
     */
    static <A, B> Either<A, IList<B>> sequence(IList<Either<A, B>> le) {
        return le.foldr(
            (et, elt) -> et.apply(elt.apply(l -> l::add)),
            right(IList.nil())
        );
    }

    boolean isLeft();

    boolean isRight();

    Optional<A> left();

    Optional<B> right();

    <T> T match(F<Left<A, B>, ? extends T> left, F<Right<A, B>, ? extends T> right);

    <T> Either<T, B> mapLeft(F<? super A, ? extends T> f);

    <T> Either<A, T> map(F<? super B, ? extends T> f);

    <C> Either<A, C> apply(Either<A, F<B, C>> ef);

    default <C> Either<A, C> apply(F<B, C> f) {
        return apply(right(f));
    }

    <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f);

    final class Right<A, B> implements Either<A, B> {
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
        public <T> Either<A, T> map(F<? super B, ? extends T> f) {
            return Either.<A, T>right(f.apply(value));
        }

        @Override
        public <C> Either<A, C> apply(Either<A, F<B, C>> ef) {
            return ef.map(f -> f.apply(value));
        }

        @Override
        public <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f) {
            return f.apply(value);
        }
    }

    final class Left<A, B> implements Either<A, B> {
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
        public <T> Either<A, T> map(F<? super B, ? extends T> f) {
            return cast();
        }

        @Override
        public <C> Either<A, C> apply(Either<A, F<B, C>> ef) {
            return cast();
        }

        @Override
        public <T> Either<A, T> flatMap(F<? super B, Either<A, T>> f) {
            return cast();
        }

        private <C> Either<A, C> cast() {
            return (Either<A, C>) this;
        }
    }
}
