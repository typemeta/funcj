package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.Lazy;

/**
 * Base class for {@code Parser} implementations.
 * @param <I>           the input stream symbol type
 * @param <A>           the parser result type
 */
abstract class ParserImpl<I, A> implements Parser<I, A> {

    private final Lazy<Boolean> acceptsEmpty;

    private final Lazy<SymSet<I>> firstSet;

    ParserImpl(Lazy<Boolean> acceptsEmpty, Lazy<SymSet<I>> firstSet) {
        this.acceptsEmpty = acceptsEmpty;
        this.firstSet = firstSet;
    }

    public Lazy<Boolean> acceptsEmpty() {
        return acceptsEmpty;
    }

    public Lazy<SymSet<I>> firstSet() {
        return firstSet;
    }

    @Override
    public String toString() {
        return "parser{" +
            "empty=" + acceptsEmpty.apply() +
            ";first=" + firstSet.apply() +
            '}';
    }
}
