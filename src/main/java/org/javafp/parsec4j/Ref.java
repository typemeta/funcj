package org.javafp.parsec4j;

import org.javafp.data.Lazy;
import org.javafp.parsec4j.Parser.Context;
import org.javafp.util.Unit;

import java.util.Objects;

public class Ref<I, CTX extends Context<I>, A>
        implements Parser<I, CTX, A> {

    public static <I, CTX extends Context<I>, A> Ref<I, CTX, A> of() {
        return new Ref<I, CTX, A>();
    }

    public static <I, CTX extends Context<I>, A> Ref<I, CTX, A> of(Parser<I, CTX, A> p) {
        return new Ref<I, CTX, A>(p);
    }

    private enum Null implements Parser<Unit, Context<Unit>, Unit> {
        INSTANCE {
            @Override
            public Lazy<Boolean> acceptsEmpty() {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }

            @Override
            public Lazy<SymSet<Unit>> firstSet() {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }

            public Result<Unit, Unit> parse(Context<Unit> ctx, int pos, SymSet<Unit> follow) {
                throw new RuntimeException("Uninitialised lazy Parser reference");
            }
        };

        static <I, CTX extends Context<I>, A> Parser<I, CTX, A> of() {
            return (Parser<I, CTX, A>) INSTANCE;
        }
    }

    private Parser<I, CTX, A> impl;

    private Ref(Parser<I, CTX, A> p) {
        this.impl = Objects.requireNonNull(impl);
    }

    private Ref() {
        this.impl = Null.of();
    }

    public Parser<I, CTX, A> set(Parser<I, CTX, A> impl) {
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
    public Result<I, A> parse(CTX ctx, int pos, SymSet<I> follow) {
        return impl.parse(ctx, pos, follow);
    }
}
