package org.typemeta.funcj.tuples;

import org.typemeta.funcj.functions.Functions;

import java.util.Comparator;
import java.util.Objects;

/**
 * A 7-tuple of values.
 * <p>
 * Null values are not allowed.
 * @param <A>       the first value type
 * @param <B>       the second value type
 * @param <C>       the third value type
 * @param <D>       the fourth value type
 * @param <E>       the fifth value type
 * @param <F>       the sixth value type
 * @param <G>       the seventh value type
 */
final public class Tuple7<A, B, C, D, E, F, G> {
    /**
     * Create a new {@code Tuple7} comprised of the supplied values
     * @param a         the first value
     * @param b         the second value
     * @param c         the third value
     * @param d         the fourth value
     * @param e         the fifth value
     * @param f         the sixth value
     * @param g         the seventh value
     * @param <A>       the first value type
     * @param <B>       the second value type
     * @param <C>       the third value type
     * @param <D>       the fourth value type
     * @param <E>       the fifth value type
     * @param <F>       the sixth value type
     * @param <G>       the seventh value type
     * @return          the new {@code Tuple7}
     * @throws          NullPointerException if any of the tuple values are null
     */
    public static <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G> of(A a, B b, C c, D d, E e, F f, G g) {
        return new Tuple7<>(a, b, c, d, e, f, g);
    }

    /**
     * Return a {@link Comparator} which can be used for {@code Tuple7},
     * when both type parameters implement {@link Comparable}.
     * @param <A>       the first value type
     * @param <B>       the second value type
     * @param <C>       the third value type
     * @param <D>       the fourth value type
     * @param <E>       the fifth value type
     * @param <F>       the sixth value type
     * @param <G>       the seventh value type
     * @return          a {@link Comparator} which can be used for {@code Tuple7} values
     */
    public static <
            A extends Comparable<A>,
            B extends Comparable<B>,
            C extends Comparable<C>,
            D extends Comparable<D>,
            E extends Comparable<E>,
            F extends Comparable<F>,
            G extends Comparable<G>> Comparator<Tuple7<A, B, C, D, E, F, G>> comparator() {
        return (lhs, rhs) -> {
            final int cmp1 = lhs._1.compareTo(rhs._1);
            if (cmp1 != 0) {
                return cmp1;
            } else {
                final int cmp2 = lhs._2.compareTo(rhs._2);
                if (cmp2 != 0) {
                    return cmp2;
                } else {
                    final int cmp3 = lhs._3.compareTo(rhs._3);
                    if (cmp3 != 0) {
                        return cmp3;
                    } else {
                        final int cmp4 = lhs._4.compareTo(rhs._4);
                        if (cmp4 != 0) {
                            return cmp4;
                        } else {
                            final int cmp5 = lhs._5.compareTo(rhs._5);
                            if (cmp5 != 0) {
                                return cmp5;
                            } else {
                                final int cmp6 = lhs._6.compareTo(rhs._6);
                                if (cmp6 != 0) {
                                    return cmp6;
                                } else {
                                    return lhs._7.compareTo(rhs._7);
                                }
                            }
                        }
                    }
                }
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
     * The third value type
     */
    public final C _3;

    /**
     * The fourth value type
     */
    public final D _4;

    /**
     * The fifth value type
     */
    public final E _5;

    /**
     * The sixth value type
     */
    public final F _6;

    /**
     * The seventh value type
     */
    public final G _7;

    /**
     * Create a new {@code Tuple7} comprised of the supplied values
     * @param a         the first value
     * @param b         the second value
     * @param c         the third value
     * @param d         the fourth value
     * @param e         the fifth value
     * @param f         the sixth value
     * @param g         the seventh value
     * @throws          NullPointerException if any of the tuple values is null
     */
    public Tuple7(A a, B b, C c, D d, E e, F f, G g) {
        _1 = Objects.requireNonNull(a);
        _2 = Objects.requireNonNull(b);
        _3 = Objects.requireNonNull(c);
        _4 = Objects.requireNonNull(d);
        _5 = Objects.requireNonNull(e);
        _6 = Objects.requireNonNull(f);
        _7 = Objects.requireNonNull(g);
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
     * Return the third value.
     * @return          the third value
     */
    public C get3() {
        return _3;
    }

    /**
     * Return the fourth value.
     * @return          the fourth value
     */
    public D get4() {
        return _4;
    }

    /**
     * Return the fifth value.
     * @return          the fifth value
     */
    public E get5() {
        return _5;
    }

    /**
     * Return the sixth value.
     * @return          the sixth value
     */
    public F get6() {
        return _6;
    }

    /**
     * Return the seventh value.
     * @return          the seventh value
     */
    public G get7() {
        return _7;
    }

    /**
     * Return a new {@code Tuple7} which is a copy of this one,
     * but with the first value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<T, B, C, D, E, F, G> with1(T t) {
        return Tuple7.of(t, _2, _3, _4, _5, _6, _7);
    }

    /**
     * Return a new {@code Tuple7} which is a copy of this one,
     * but with the second value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, T, C, D, E, F, G> with2(T t) {
        return Tuple7.of(_1, t, _3, _4, _5, _6, _7);
    }

    /**
     * Return a new {@code Tuple7} which is a copy of this one,
     * but with the third value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, T, D, E, F, G> with3(T t) {
        return Tuple7.of(_1, _2, t, _4, _5, _6, _7);
    }

    /**
     * Return a new {@code Tuple7} which is a copy of this one,
     * but with the fourth value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, T, E, F, G> with4(T t) {
        return Tuple7.of(_1, _2, _3, t, _5, _6, _7);
    }

    /**
     * Return a new {@code Tuple7} which is a copy of this one,
     * but with the fifth value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, D, T, F, G> with5(T t) {
        return Tuple7.of(_1, _2, _3, _4, t, _6, _7);
    }

    /**
     * Return a new {@code Tuple7} which is a copy of this one,
     * but with the sixth value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, D, E, T, G> with6(T t) {
        return Tuple7.of(_1, _2, _3, _4, _5, t, _7);
    }

    /**
     * Return a new {@code Tuple7} which is a copy of this one,
     * but with the seventh value replaced with the supplied argument {@code t}
     * @param t         the replacement value
     * @param <T>       the replacement value type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, D, E, F, T> with7(T t) {
        return Tuple7.of(_1, _2, _3, _4, _5, _6, t);
    }

    /**
     * Apply a 7-ary function to the values within this {@code Tuple7},
     * and return the result.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the result of applying the function
     */
    public <T> T applyFrom(Functions.F7<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? extends T> f) {
        return f.apply(_1, _2, _3, _4, _5, _6, _7);
    }

    /**
     * Create a new {@code Tuple7} which is a copy of this one,
     * but with the first value replaced with the result of applying the supplied function
     * to the first value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<T, B, C, D, E, F, G> map1(Functions.F<? super A, ? extends T> f) {
        return of(f.apply(_1), _2, _3, _4, _5, _6, _7);
    }

    /**
     * Create a new {@code Tuple7} which is a copy of this one,
     * but with the second value replaced with the result of applying the supplied function
     * to the second value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, T, C, D, E, F, G> map2(Functions.F<? super B, ? extends T> f) {
        return of(_1, f.apply(_2), _3, _4, _5, _6, _7);
    }

    /**
     * Create a new {@code Tuple7} which is a copy of this one,
     * but with the third value replaced with the result of applying the supplied function
     * to the third value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, T, D, E, F, G> map3(Functions.F<? super C, ? extends T> f) {
        return of(_1, _2, f.apply(_3), _4, _5, _6, _7);
    }

    /**
     * Create a new {@code Tuple7} which is a copy of this one,
     * but with the fourth value replaced with the result of applying the supplied function
     * to the fourth value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, T, E, F, G> map4(Functions.F<? super D, ? extends T> f) {
        return of(_1, _2, _3, f.apply(_4), _5, _6, _7);
    }

    /**
     * Create a new {@code Tuple7} which is a copy of this one,
     * but with the fifth value replaced with the result of applying the supplied function
     * to the fifth value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, D, T, F, G> map5(Functions.F<? super E, ? extends T> f) {
        return of(_1, _2, _3, _4, f.apply(_5), _6, _7);
    }

    /**
     * Create a new {@code Tuple7} which is a copy of this one,
     * but with the sixth value replaced with the result of applying the supplied function
     * to the sixth value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, D, E, T, G> map6(Functions.F<? super F, ? extends T> f) {
        return of(_1, _2, _3, _4, _5, f.apply(_6), _7);
    }

    /**
     * Create a new {@code Tuple7} which is a copy of this one,
     * but with the seventh value replaced with the result of applying the supplied function
     * to the seventh value.
     * @param f         the function
     * @param <T>       the function return type
     * @return          the new {@code Tuple7}
     */
    public <T> Tuple7<A, B, C, D, E, F, T> map7(Functions.F<? super G, ? extends T> f) {
        return of(_1, _2, _3, _4, _5, _6, f.apply(_7));
    }

    @Override
    public String toString() {
        return "(" + _1 + ',' + _2 + ',' + _3 + ',' + _4 + ',' + _5 + ',' + _6 +  ')';
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (rhs == null || getClass() != rhs.getClass()) return false;

        Tuple7<?, ?, ?, ?, ?, ?, ?> rhsT = (Tuple7<?, ?, ?, ?, ?, ?, ?>) rhs;

        return _1.equals(rhsT._1) &&
            _2.equals(rhsT._2) &&
            _3.equals(rhsT._3) &&
            _4.equals(rhsT._4) &&
            _5.equals(rhsT._5) &&
            _6.equals(rhsT._6) &&
            _7.equals(rhsT._7);
    }

    @Override
    public int hashCode() {
        int result = _1.hashCode();
        result = 31 * result + _2.hashCode();
        result = 31 * result + _3.hashCode();
        result = 31 * result + _4.hashCode();
        result = 31 * result + _5.hashCode();
        result = 31 * result + _6.hashCode();
        result = 31 * result + _7.hashCode();
        return result;
    }
}
