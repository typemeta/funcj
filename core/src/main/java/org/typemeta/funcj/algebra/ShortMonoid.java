package org.typemeta.funcj.algebra;

public class ShortMonoid implements Monoid<Short> {
    @Override
    public Short zero() {
        return 0;
    }

    @Override
    public Short combine(Short x, Short y) {
        return (short)(x + y);
    }
}
