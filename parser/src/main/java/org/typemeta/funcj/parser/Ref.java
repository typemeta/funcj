package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.*;

import java.util.Objects;

/**
 * A reference to a {@link Parser}.
 * <p>
 * At creation the reference is typically uninitialised.
 * Any of the {@code Parser} methods will throw if invoked on an uninitialised {@code Ref}.
 * It is subsequently initialised (via the {@link Ref#set(Parser)} method) with a {@code Parser}
 * {@code Ref} is typically used to allow parsers for grammars with circular
 * dependencies to be constructed.
 * @param <I>       input stream symbol type
 * @param <A>       parser result type
 */
public class Ref<I, A> implements Parser<I, A> {

    private enum Uninitialised implements Parser<Unit, Unit> {
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
        this.impl = Uninitialised.of();
    }

    /**
     * Initialise this reference
     * @param impl      the parser
     * @return          this parser
     */
    public Parser<I, A> set(Parser<I, A> impl) {
        if (this.impl != Uninitialised.INSTANCE) {
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
