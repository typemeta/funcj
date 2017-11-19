package org.typemeta.funcj.algebra;

public class StringMonoid implements Monoid<String> {
    @Override
    public String zero() {
        return "";
    }

    @Override
    public String combine(String x, String y) {
        return (x + y);
    }
}
