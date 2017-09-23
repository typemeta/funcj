package io.typemeta.funcj.parser;

import io.typemeta.funcj.data.*;

import java.util.Objects;

/**
 * A reference to a parser.
 * <p>
 * A reference to a {link Parser}. At creation the reference The reference is eventually
 * Typically used to allow parsers with circular
 * dependencies to be constructed.
 * @param <I> input stream symbol type
 * @param <A> parser result type
 */
public class Ref<I, A> implements Parser<I, A> {

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

    Ref(Parser<I, A> p) {
        this.impl = Objects.requireNonNull(impl);
    }

    Ref() {
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
