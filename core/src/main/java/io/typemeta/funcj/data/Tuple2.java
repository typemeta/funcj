package io.typemeta.funcj.data;

import io.typemeta.funcj.util.Functions.F;

import java.util.Objects;

/**
 * A 2-tuple of values
 * @param <A> 1st value type
 * @param <B> 2nd value type
 */
final public class Tuple2<A, B> {
    public static <A, B> Tuple2<A, B> of(A _1, B _2) {
        return new Tuple2<A, B>(_1, _2);
    }

    public final A _1;
    public final B _2;

    public Tuple2(A a, B b) {
        _1 = Objects.requireNonNull(a);
        _2 = Objects.requireNonNull(b);
    }

    public A get1() {
        return _1;
    }

    public B get2() {
        return _2;
    }

    public <T> Tuple2<T, B> with1(T t) {
        return Tuple2.of(t, _2);
    }

    public <T> Tuple2<A, T> with2(T t) {
        return Tuple2.of(_1, t);
    }

    public <T> Tuple2<T, B> map1(F<? super A, ? extends T> f) {
        return of(f.apply(_1), _2);
    }

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
