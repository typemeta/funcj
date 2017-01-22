package org.javafp.parsec4j.text;

import org.javafp.data.Unit;

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
            public Result<Unit> parse(Input input, int pos) {
                throw new RuntimeException("Null Parser Reference");
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
    public Result<A> parse(Input input, int pos) {
        return impl.parse(input, pos);
    }
}
