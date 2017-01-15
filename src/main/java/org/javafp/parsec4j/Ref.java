package org.javafp.parsec4j;

import org.javafp.data.Unit;

import java.util.Objects;

public class Ref<I, A> implements Parser<I, A> {

    public static <I, A> org.javafp.parsec4j.Ref<I, A> of() {
        return new org.javafp.parsec4j.Ref<I, A>();
    }

    public static <I, A> org.javafp.parsec4j.Ref<I, A> of(Parser<I, A> p) {
        return new org.javafp.parsec4j.Ref<I, A>(p);
    }

    private enum Null implements Parser<Unit, Unit> {
        INSTANCE {
            public Result<Unit, Unit> parse(Input<Unit> input, int pos) {
                throw new RuntimeException("Null Parser Reference");
            }
        };

        static <I, A> Parser<I, A> of() {
            return (Parser<I, A>) INSTANCE;
        }
    }

    private Parser<I, A> impl;

    private Ref(Parser<I, A> p) {
        this.impl = Objects.requireNonNull(impl);
    }

    private Ref() {
        this.impl = Null.of();
    }

    public Parser<I, A> set(Parser<I, A> impl) {
        if (this.impl != Null.INSTANCE) {
            throw new IllegalStateException("Ref is already initialised");
        } else {
            this.impl = Objects.requireNonNull(impl);
            return this;
        }
    }

    @Override
    public Result<I, A> parse(Input<I> input, int pos) {
        return impl.parse(input, pos);
    }

    @Override
    public boolean accepts(Input<I> input, int pos) {
        return impl.accepts(input, pos);
    }


    @Override
    public boolean accepts(I token) {
        return impl.accepts(token);
    }
}
