package org.typemeta.funcj.algebra;

public class IntMonoid implements Monoid<Integer> {
    @Override
    public Integer zero() {
        return 0;
    }

    @Override
    public Integer combine(Integer x, Integer y) {
        return x + y;
    }
}
