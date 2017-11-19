package org.typemeta.funcj.algebra;

public class DoubleMonoid implements Monoid<Double> {
    @Override
    public Double zero() {
        return 0d;
    }

    @Override
    public Double combine(Double x, Double y) {
        return x + y;
    }
}
