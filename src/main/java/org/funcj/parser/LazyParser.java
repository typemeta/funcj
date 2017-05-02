package org.funcj.parser;

import org.funcj.data.Lazy;
import org.funcj.util.Functions;

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
    public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
        return parserSupplier.apply().parse(in, follow);
    }
}
