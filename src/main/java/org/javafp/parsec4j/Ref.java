package org.javafp.parsec4j;

import org.javafp.data.Unit;

import java.util.Objects;

public class Ref<I, A> implements P<I, A> {

    public static <I, A> org.javafp.parsec4j.Ref<I, A> of() {
        return new org.javafp.parsec4j.Ref<I, A>();
    }

    public static <I, A> org.javafp.parsec4j.Ref<I, A> of(P<I, A> p) {
        return new org.javafp.parsec4j.Ref<I, A>(p);
    }

    private enum Null implements P<Unit, Unit> {
        INSTANCE {
            public Result<Unit, Unit> parse(Input<Unit> input) {
                throw new RuntimeException("Null Parser Reference");
            }
        };

        static <I, A> P<I, A> of() {
            return (P<I, A>) INSTANCE;
        }
    }

    private P<I, A> impl;

    private Ref(P<I, A> p) {
        this.impl = Objects.requireNonNull(impl);
    }

    private Ref() {
        this.impl = Null.of();
    }

    public P<I, A> set(P<I, A> impl) {
        if (this.impl != Null.INSTANCE) {
            throw new IllegalStateException("Ref is already initialised");
        } else {
            this.impl = Objects.requireNonNull(impl);
            return this;
        }
    }

    @Override
    public Result<I, A> parse(Input<I> input) {
        return impl.parse(input);
    }

    @Override
    public boolean accepts(Input<I> input) {
        return impl.accepts(input);
    }


    @Override
    public boolean accepts(I token) {
        return impl.accepts(token);
    }
}
