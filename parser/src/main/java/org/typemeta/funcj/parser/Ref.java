package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.Lazy;

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

    private enum Uninitialised implements Parser<Object, Object> {
        INSTANCE {

            @Override
            public Lazy<Boolean> acceptsEmpty() {
                throw error();
            }

            @Override
            public Lazy<SymSet<Object>> firstSet() {
                throw error();
            }

            public Result<Object, Object> apply(Input<Object> in, SymSet<Object> follow) {
                throw error();
            }
        };

        private static RuntimeException error() {
            return new RuntimeException("Uninitialised lazy Parser reference");
        }

        @SuppressWarnings("unchecked")
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
    public Result<I, A> apply(Input<I> in, SymSet<I> follow) {
        return impl.apply(in, follow);
    }
}
