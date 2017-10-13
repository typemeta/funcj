package org.typemeta.funcj.tuples;

import org.typemeta.funcj.functions.Functions.*;

import java.util.*;

/**
 * A 2-tuple of values.
 * @param <A>       the first value type
 * @param <B>       the second value type
 */
final public class Tuple2<A, B> {
    /**
     * Create a new {@code Tuple2} comprised of the supplied values.
     * @param a         the first value
     * @param b         the second value
     * @param <A>       the first value type
     * @param <B>       the second value type
     * @return          the new {@code Tuple2}
     * @throws          NullPointerException if any of the tuple values is null
     */
    public static <A, B> Tuple2<A, B> of(A a, B b) {
        return new Tuple2<>(a, b);
    }

    /**
     * Return a {@link Comparator} which can be used for {@code Tuple2},
     * when both type parameters implement {@link Comparable}.
     * @param <A>       the first value type
     * @param <B>       the second value type
     * @return          a {@link Comparator} which can be used for {@code Tuple2} values
     */
    public static <A extends Comparable<A>, B extends Comparable<B>> Comparator<Tuple2<A, B>> comparator() {
        return (lhs, rhs) -> {
            final int cmp1 = lhs._1.compareTo(rhs._1);
            if (cmp1 != 0) {
                return cmp1;
            } else {
                return lhs._2.compareTo(rhs._2);
            }
        };
    }

    /**
     * The first value type
     */
    public final A _1;

    /**
     * The second value type
     */
    public final B _2;

    /**
     * Create a new {@code Tuple2} comprised of the supplied values.
     * @param a         the first value
     * @param b         the second value
     * @throws          NullPointerException if any of the tuple values is null
     */
    public Tuple2(A a, B b) {
        _1 = Objects.requireNonNull(a);
        _2 = Objects.requireNonNull(b);
    }

    /**
     * Return the first value.
     * @return          the first value
     */
    public A get1() {
        return _1;
    }

    /**
     * Return the second value.
     * @return          the second value
     */
    public B get2() {
        return _2;
    }

    /**
     * Return a new {@code Tuple2} which is a copy of this one,
     * but with the first value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple2}
     */
    public <T> Tuple2<T, B> with1(T t) {
        return of(t, _2);
    }


    /**
     * Return a new {@code Tuple2} which is a copy of this one,
     * but with the second value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple2}
     */
    public <T> Tuple2<A, T> with2(T t) {
        return of(_1, t);
    }

    /**
     * Apply a binary function to the values within this {@code Tuple2},
     * and return the result.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the result of applying the function
     */
    public <T> T apply(F2<? super A, ? super B, ? extends T> f) {
        return f.apply(_1, _2);
    }

    /**
     * Create a new {@code Tuple2} which is a copy of this one,
     * but with the first value replaced with the result of applying the supplied function
     * to the first value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple2}
     */
    public <T> Tuple2<T, B> map1(F<? super A, ? extends T> f) {
        return of(f.apply(_1), _2);
    }

    /**
     * Create a new {@code Tuple2} which is a copy of this one,
     * but with the second value replaced with the result of applying the supplied function
     * to the second value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple2}
     */
    public <T> Tuple2<A, T> map2(F<? super B, ? extends T> f) {
        return of(_1, f.apply(_2));
    }

    @Override
    public String toString() {
        return "(" + _1 + ',' + _2 + ')';
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (rhs == null || getClass() != rhs.getClass()) return false;

        Tuple2<?, ?> rhsT = (Tuple2<?, ?>) rhs;

        return _1.equals(rhsT._1) && _2.equals(rhsT._2);
    }

    @Override
    public int hashCode() {
        return 31 * _1.hashCode() + _2.hashCode();
    }
}
