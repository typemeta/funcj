package org.typemeta.funcj.control;

import org.typemeta.funcj.algebra.Monoid;
import org.typemeta.funcj.functions.Functions;

public interface Writer<T, W> {
    class Base<T, W> implements Writer<T, W> {
        final T value;
        final Monoid<W> monoid;
        final W written;

        public Base(T value, Monoid<W> monoid, W written) {
            this.value = value;
            this.monoid = monoid;
            this.written = written;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public Monoid<W> monoid() {
            return monoid;
        }

        @Override
        public W written() {
            return written;
        }
    }

    static <T, W> Writer<T, W> pure(T value, Monoid<W> monoid) {
        return new Base<T, W>(value, monoid, monoid.zero());
    }

    Monoid<W> monoid();

    T value();

    W written();

    default <U> Writer<U, W> flatMap(Functions.F<T, Writer<U, W>> fw) {
        final Writer<U, W> wu = fw.apply(value());
        return new Base<U, W>(wu.value(), monoid(), monoid().combine(written(), wu.written()));
    }
}
