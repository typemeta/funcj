package org.javafp.data;

import org.javafp.data.Functions.F;

import java.util.Objects;

public class Tuple3<A, B, C> {
    public static <A, B, C> Tuple3<A, B, C> of(A _1, B _2, C _3) {
        return new Tuple3<A, B, C>(_1, _2, _3);
    }

    public final A _1;
    public final B _2;
    public final C _3;

    public Tuple3(A a, B b, C c) {
        _1 = Objects.requireNonNull(a);
        _2 = Objects.requireNonNull(b);
        _3 = Objects.requireNonNull(c);
    }

    public A get1() {
        return _1;
    }

    public B get2() {
        return _2;
    }

    public C get3() {
        return _3;
    }

    public <T> Tuple3<T, B, C> map1(F<A, T> f) {
        return of(f.apply(_1), _2, _3);
    }

    public <T> Tuple3<A, T, C> map2(F<B, T> f) {
        return of(_1, f.apply(_2), _3);
    }

    public <T> Tuple3<A, B, T> map3(F<C, T> f) {
        return of(_1, _2, f.apply(_3));
    }

    @Override
    public String toString() {
        return "(" + _1 + ',' + _2 + ')';
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (rhs == null || getClass() != rhs.getClass()) return false;

        Tuple3<?, ?, ?> rhsT = (Tuple3<?, ?, ?>) rhs;

        return _1.equals(rhsT._1) &&
            _2.equals(rhsT._2) &&
            _3.equals(rhsT._3);
    }

    @Override
    public int hashCode() {
        int result = _1.hashCode();
        result = 31 * result + _2.hashCode();
        result = 31 * result + _3.hashCode();
        return result;
    }
}
