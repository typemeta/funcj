package org.javafp.parsec4j;

import org.javafp.data.Unit;
import org.javafp.parsec4j.Parser.Context;

import java.util.Objects;

public class Ref<I, CTX extends Parser.Context<I>, A>
        implements Parser<I, CTX, A> {

    public static <I, CTX extends Context<I>, A> Ref<I, CTX, A> of() {
        return new org.javafp.parsec4j.Ref<I, CTX, A>();
    }

    public static <I, CTX extends Context<I>, A> Ref<I, CTX, A> of(Parser<I, CTX, A> p) {
        return new org.javafp.parsec4j.Ref<I, CTX, A>(p);
    }

    private enum Null implements Parser<Unit, Context<Unit>, Unit> {
        INSTANCE {
            public Result<Unit, Unit> parse(Context<Unit> ctx, int pos) {
                throw new RuntimeException("Null Parser Reference");
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
    public Result<I, A> parse(CTX ctx, int pos) {
        return impl.parse(ctx, pos);
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
