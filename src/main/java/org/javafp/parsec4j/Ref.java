package org.javafp.parsec4j;

import org.javafp.data.Lazy;
import org.javafp.util.Unit;

import java.util.Objects;

public class Ref<I, A> implements Parser<I, A> {

    public static <I, A> Ref<I, A> of() {
        return new Ref<I, A>();
    }

    public static <I, A> Ref<I, A> of(Parser<I, A> p) {
        return new Ref<I, A>(p);
    }

    private enum Null implements Parser<Unit, Unit> {
        INSTANCE {
            @Override
            public Lazy<Boolean> acceptsEmpty() {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }

            @Override
            public Lazy<SymSet<Unit>> firstSet() {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }

            public Result<Unit, Unit> parse(Input<Unit> in, SymSet<Unit> follow) {
                throw new RuntimeException("Uninitialised lazy Parser reference");
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
    public Lazy<Boolean> acceptsEmpty() {
        return () -> impl.acceptsEmpty().apply();
    }

    @Override
    public Lazy<SymSet<I>> firstSet() {
        return () -> impl.firstSet().apply();
    }

    @Override
    public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
        return impl.parse(in, follow);
    }
}
