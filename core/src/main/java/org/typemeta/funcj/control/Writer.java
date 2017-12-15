package org.typemeta.funcj.control;

import org.typemeta.funcj.algebra.Monoid;
import org.typemeta.funcj.functions.Functions;

public abstract class Writer<T, W> {
    static <T, W> Writer<T, W> pure(T value, Monoid<W> monoid) {
        return new Writer<T, W>(value, monoid.zero()) {
            @Override
            Monoid<W> monoid() {
                return monoid;
            }
        };
    }

    abstract Monoid<W> monoid();

    public final T value;
    public final W written;

    protected Writer(T value, W written) {
        this.value = value;
        this.written = written;
    }

    public <U> Writer<U, W> flatMap(Functions.F<T, Writer<U, W>> fw) {
        final Writer<U, W> wu = fw.apply(value);
        return new Writer<U, W>(wu.value, monoid().combine(written, wu.written)) {
            @Override
            Monoid<W> monoid() {
                return Writer.this.monoid();
            }
        };
    }
}
