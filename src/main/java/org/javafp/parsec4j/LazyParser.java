package org.javafp.parsec4j;

import org.javafp.data.Lazy;
import org.javafp.util.Functions;

class LazyParser<I, A> implements Parser<I, A> {

    static <I, A> Parser<I, A> of(Functions.F0<Parser<I, A>> parserSupplier) {
        return new LazyParser<I, A>(parserSupplier);
    }

    private final Functions.F0<Parser<I, A>> parserSupplier;

    LazyParser(Functions.F0<Parser<I, A>> parserSupplier) {
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
    public Result<I, A> parse(Input<I> in, int pos, SymSet<I> follow) {
        return parserSupplier.apply().parse(in, pos, follow);
    }
}
