package org.javafp.parsec4j;

import org.javafp.data.Lazy;
import org.javafp.util.Functions;

class LazyParser<I, CTX extends Parser.Context<I>, A> implements Parser<I, CTX, A> {

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> of(Functions.F0<Parser<I, CTX, A>> parserSupplier) {
        return new LazyParser<I, CTX, A>(parserSupplier);
    }

    private final Functions.F0<Parser<I, CTX, A>> parserSupplier;

    LazyParser(Functions.F0<Parser<I, CTX, A>> parserSupplier) {
        this.parserSupplier = parserSupplier;
    }

    @Override
    public Lazy<Boolean> acceptsEmpty() {
        return () -> parserSupplier.apply().acceptsEmpty().apply();
    }

    @Override
    public Lazy<SymSet<I>> firstSet() {
        return () -> parserSupplier.apply().firstSet().apply();
    }

    @Override
    public Result<I, A> parse(CTX ctx, int pos, SymSet<I> follow) {
        return parserSupplier.apply().parse(ctx, pos, follow);
    }
}
