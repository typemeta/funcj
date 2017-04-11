package org.javafp.parsec4j.text;

import org.javafp.parsec4j.SymSet;
import org.javafp.util.*;

import java.util.Objects;

public class Ref<A> implements Parser<A> {

    public static <A> Ref<A> of() {
        return new Ref<A>();
    }

    public static <A> Ref<A> of(Parser<A> p) {
        return new Ref<A>(p);
    }

    private enum Null implements Parser<Unit> {
        INSTANCE {
            @Override
            public boolean acceptsEmpty() {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }

            @Override
            public SymSet<Chr> firstSet() {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }

            public Result<Unit> parse(Input in, int pos) {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }
        };

        static <A> Parser<A> of() {
            return (Parser<A>) INSTANCE;
        }
    }

    private Parser<A> impl;

    private Ref(Parser<A> p) {
        this.impl = Objects.requireNonNull(impl);
    }

    private Ref() {
        this.impl = Null.of();
    }

    public Parser<A> set(Parser<A> impl) {
        if (this.impl != Null.INSTANCE) {
            throw new IllegalStateException("Ref is already initialised");
        } else {
            this.impl = Objects.requireNonNull(impl);
            return this;
        }
    }

    @Override
    public boolean acceptsEmpty() {
        return impl.acceptsEmpty();
    }

    @Override
    public SymSet<Chr> firstSet() {
        return impl.firstSet();
    }

    @Override
    public Result<A> parse(Input in, int pos) {
        return impl.parse(in, pos);
    }
}
