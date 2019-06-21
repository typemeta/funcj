package org.typemeta.funcj.control;

import org.typemeta.funcj.algebra.Monoid;
import org.typemeta.funcj.functions.Functions;

public interface WriterM<W, A> {
    class Base<W, A> implements WriterM<W, A> {
        final Monoid<W> monoid;
        final A value;
        final W written;

        public Base(Monoid<W> monoid, A value, W written) {
            this.monoid = monoid;
            this.value = value;
            this.written = written;
        }

        @Override
        public Monoid<W> monoid() {
            return monoid;
        }

        @Override
        public A value() {
            return value;
        }

        @Override
        public W written() {
            return written;
        }
    }

    static <W, A> WriterM<W, A> of(Monoid<W> monoid, A value, W written) {
        return new Base<>(monoid, value, written);
    }

    static <W, A> WriterM<W, A> pure(Monoid<W> monoid, A value) {
        return new Base<>(monoid, value, monoid.zero());
    }

    Monoid<W> monoid();

    A value();

    W written();

    default <B> WriterM<W, B> writer(B value, W written) {
        return new Base<>(monoid(), value, written);
    }

    default <B> WriterM<W, B> flatMap(Functions.F<A, WriterM<W, B>> fb) {
        final WriterM<W, B> wb = fb.apply(value());
        return writer(wb.value(), monoid().combine(written(), wb.written()));
    }

    default <B> WriterM<W, B> app(WriterM<W, Functions.F<A, B>> wtrF) {
        final B b = wtrF.value().apply(this.value());
        final W w = wtrF.monoid().combine(wtrF.written(), this.written());
        return writer(b, w);
    }

    default <B> WriterM<W, B> map(Functions.F<A, B> f) {
        return writer(f.apply(value()), written());
    }
}
