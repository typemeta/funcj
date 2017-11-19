package org.typemeta.funcj.algebra;

public class FloatMonoid implements Monoid<Float> {
    @Override
    public Float zero() {
        return 0f;
    }

    @Override
    public Float combine(Float x, Float y) {
        return x + y;
    }
}
