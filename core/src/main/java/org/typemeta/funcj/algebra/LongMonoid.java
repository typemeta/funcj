package org.typemeta.funcj.algebra;

public class LongMonoid implements Monoid<Long> {
    @Override
    public Long zero() {
        return 0l;
    }

    @Override
    public Long combine(Long x, Long y) {
        return x + y;
    }
}
