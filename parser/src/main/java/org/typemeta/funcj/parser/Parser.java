package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.*;
import org.typemeta.funcj.tuples.Tuple2;

import java.util.*;
import java.util.stream.Stream;

import static org.typemeta.funcj.parser.Parser.pure;
import static org.typemeta.funcj.parser.Utils.*;

/**
 * A parser is essentially a function from an input stream to a parse {@link Result}.
 * The {@code Parser} type along with the {@code pure} and {@code ap} functions constitute an applicative functor.
 * @param <I>       the input stream symbol type
 * @param <A>       the parser result type
 */
public interface Parser<I, A> {

    /**
     * Construct an uninitialised parser reference object.
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          the uninitialised parser reference
     */
    static <I, A> Ref<I, A> ref() {
        return new Ref<I, A>();
    }

    /**
     * Construct a parser reference object from a parser.
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @param p         the parser
     * @return          the initialised parser reference
     */
    static <I, A> Ref<I, A> ref(Parser<I, A> p) {
        return new Ref<I, A>(p);
    }

    /**
     * Applicative unit/pure function.
     * Construct a parser that always returns the given value, without consuming any input.
     * @param a         the value
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that always returns the given value
     */
    static <I, A> Parser<I, A> pure(A a) {
        return new ParserImpl<I, A>(LTRUE, SymSet::empty) {
            @Override
            public Result<I, A> apply(Input<I> in, SymSet<I> follow) {
                return Result.success(a, in);
            }
        };
    }

    /**
     * Construct a parser that, if {@code pf} succeeds, yielding a function {@code f},
     * and if {@code pa} succeeds, yielding a value {@code a},
     * then it returns the result of applying function {@code f} to value {@code a}.
     * Otherwise, if {@code pf} fails then the parser returns the failure,
     * else if {@code pa} fails then it returns that failure.
     * @param pf        the parser that returns a function result
     * @param pa        the parser that returns a value result
     * @param <I>       the input stream symbol type
     * @param <A>       the input type of the function
     * @param <B>       the return type of the function
     * @return          a parser that returns the result of applying the parsed function to the parsed value
     */
    static <I, A, B>
    Parser<I, B> ap(Parser<I, F<A, B>> pf, Parser<I, A> pa) {
        return new ParserImpl<I, B>(
            Utils.and(pf.acceptsEmpty(), pa.acceptsEmpty()),
            combine(pf.acceptsEmpty(), pf.firstSet(), pa.firstSet())
        ) {
            @Override
            public Result<I, B> apply(Input<I> in, SymSet<I> follow) {
                final SymSet<I> followF =
                        combine(
                                pa.acceptsEmpty().apply(),
                                pa.firstSet().apply(),
                                follow);

                final Result<I, F<A, B>> r = pf.apply(in, followF);

                if (r.isSuccess()) {
                    final Result.Success<I, F<A, B>> succ = (Result.Success<I, F<A, B>>) r;
                    final Input<I> next = succ.next();
                    if (!pa.acceptsEmpty().apply()) {
                        if (next.isEof()) {
                            return failureEof(pa, next);
                        } else if (!pa.firstSet().apply().matches(next.get())) {
                            return failure(pa, next);
                        }
                    }

                    final Result<I, A> r2 = pa.apply(next, follow);
                    return r2.map(succ.value());
                } else {
                    return ((Result.Failure<I, F<A, B>>) r).cast();
                }
            }
        };
    }

    /**
     * Construct a parser that, if {@code pa} succeeds, yielding a function {@code a},
     * then it returns the result of applying function {@code f} to value {@code a}.
     * If {@code pa} fails then the parser returns the failure.
     * @param f         the function
     * @param pa        the parser that returns a value result
     * @param <I>       the input stream symbol type
     * @param <A>       the input type of the function
     * @param <B>       the return type of the function
     * @return          a parser that returns the result of applying the function to the parsed value
     */
    static <I, A, B>
    Parser<I, B> ap(F<A, B> f, Parser<I, A> pa) {
        return ap(pure(f), pa);
    }

    /**
     * Standard applicative traversal.
     * <p>
     * Equivalent to <pre>sequence(lt.map(f))</pre>.
     * @param lt        the list of values
     * @param f         the function to be applied to each value in the list
     * @param <I>       the error type
     * @param <T>       the type of list elements
     * @param <U>       the type wrapped by the {@code Try} returned by the function
     * @return          a {@code Parser} which wraps an {@link IList} of values
     */
    static <I, T, U> Parser<I, IList<U>> traverse(IList<T> lt, F<T, Parser<I, U>> f) {
        return lt.foldRight(
                (t, plu) -> ap(plu.map(lu -> lu::add), f.apply(t)),
                pure(IList.empty())
        );
    }

    /**
     * Standard applicative sequencing.
     * <p>
     * Translate a {@link IList} of {@code Parser} into a {@code Parser} of an {@code IList},
     * by composing each consecutive {@code Parser} using the {@link Parser#ap(Parser, Parser)} method.
     * @param lpt       the list of {@code Parser} values
     * @param <I>       the error type
     * @param <T>       the value type of the {@code Parser}s in the list
     * @return          a {@code Parser} which wraps an {@link IList} of values
     */
    static <I, T> Parser<I, IList<T>> sequence(IList<Parser<I, T>> lpt) {
        return lpt.foldRight(
                (pt, plt) -> ap(plt.map(lt -> lt::add), pt),
                pure(IList.empty())
        );
    }

    /**
     * Variation of {@link Parser#sequence(IList)} for {@link Stream}.
     * @param spt       the stream of {@code Parser} values
     * @param <E>       the error type
     * @param <T>       the value type of the {@code Parser}s in the stream
     * @return          a {@code Parser} which wraps an {@link Stream} of values
     */
    static <E, T> Parser<E, Stream<T>> sequence(Stream<Parser<E, T>> spt) {
        final Iterator<Parser<E, T>> iter = spt.iterator();
        Parser<E, IList<T>> plt = pure(IList.empty());
        while (iter.hasNext()) {
            final Parser<E, T> pt = iter.next();
            plt = ap(plt.map(lt -> lt::add), pt);
        }
        return plt.map(IList::stream);
    }

    /**
     * Apply this parser to the input stream. Fail if eof isn't reached.
     * @param in        the input stream
     * @return          the parser result
     */
    default Result<I, A> parse(Input<I> in) {
        final Parser<I, A> parserAndEof = this.andL(Combinators.eof());
        if (acceptsEmpty().apply()) {
            return parserAndEof.apply(in, SymSet.empty());
        } else if (in.isEof()) {
            return failureEof(this, in);
        } else if (firstSet().apply().matches(in.get())) {
            return parserAndEof.apply(in, SymSet.empty());
        } else {
            return failure(this, in);
        }
    }

    /**
     * Indicate whether this parser accepts the empty symbol.
     * @return          a lazy wrapper for true iff the parser accepts the empty symbol
     */
    Lazy<Boolean> acceptsEmpty();

    /**
     * The First Set for this parser.
     * @return          a lazy symbol set
     */
    Lazy<SymSet<I>> firstSet();

    /**
     * Apply this parser to the input stream.
     * Note: If this parser is being used as a standalone parser,
     * then call {@link Parser#parse(Input)} to parse an input.
     * @param in        the input stream
     * @param follow    the dynamic follow set
     * @return          the parse result
     */
    Result<I, A> apply(Input<I> in, SymSet<I> follow);

    /**
     * Apply this parser to the input stream.
     * Note: If this parser is being used as a standalone parser,
     * then call {@link Parser#parse(Input)} to parse an input.
     * @param in        the input stream
     * @return          the parser result
     */
    default Result<I, A> apply(Input<I> in) {
        return this.apply(in, SymSet.empty());
    }

    @SuppressWarnings("unchecked")
    default <B> Parser<I, B> cast() {
        return (Parser<I, B>)this;
    }

    /**
     * Construct a parser that, if this parser succeeds then returns the result
     * of applying the function {@code f} to the result,
     * otherwise return the failure.
     * @param f         the function to be mapped over this parser
     * @param <B>       the function return type
     * @return          a parser that returns {@code f} mapped over this parser's result
     */
    default <B> Parser<I, B> map(F<A, B> f) {
        return new ParserImpl<I, B>(
            Parser.this.acceptsEmpty(),
            Parser.this.firstSet()
        ) {
            @Override
            public Result<I, B> apply(Input<I> in, SymSet<I> follow) {
                return Parser.this.apply(in, follow).map(f);
            }
        };
    }

    /**
     * Construct a parser which returns the result of either this parser or,
     * if it fails, then the result of the {@code rhs} parser.
     * @param rhs       the second parser to attempt
     * @param <B>       the rhs parser result type
     * @return          a parser which returns the result of either this parser or the {@code rhs} parser.
     */
    @SuppressWarnings("unchecked")
    default <B extends A> Parser<I, A> or(Parser<I, B> rhs) {
        return new ParserImpl<I, A>(
            Utils.or(Parser.this.acceptsEmpty(), rhs.acceptsEmpty()),
            union(Parser.this.firstSet(), rhs.firstSet())
        ) {
            @Override
            public Result<I, A> apply(Input<I> in, SymSet<I> follow) {
                if (in.isEof()) {
                    if (Parser.this.acceptsEmpty().apply()) {
                        return Parser.this.apply(in, follow);
                    } else if (rhs.acceptsEmpty().apply()) {
                        return (Result<I, A>)rhs.apply(in, follow);
                    } else {
                        return failureEof(this, in);
                    }
                } else {
                    final I next = in.get();
                    if (Parser.this.firstSet().apply().matches(next)) {
                        return Parser.this.apply(in, follow);
                    } else if (rhs.firstSet().apply().matches(next)) {
                        return (Result<I, A>)rhs.apply(in, follow);
                    } else if (follow.matches(next)) {
                        if (Parser.this.acceptsEmpty().apply()) {
                            return Parser.this.apply(in, follow);
                        } else if (rhs.acceptsEmpty().apply()) {
                            return (Result<I, A>)rhs.apply(in, follow);
                        }
                    }
                    return failure(this, in);
                }
            }
        };
    }

    /**
     * Combine this parser with another to form a builder which accumulates the parse results.
     * @param pb        the second parser
     * @param <B>       the result type of second parser
     * @return          an {@link ApplyBuilder} which accumulates the parse results.
     */
    default <B> ApplyBuilder._2<I, A, B> and(Parser<I, B> pb) {
        return new ApplyBuilder._2<I, A, B>(this, pb);
    }

    /**
     * Combine this parser with another to form a parser which applies two parsers,
     * and if they are both successful
     * throws away the result of the right-hand parser,
     * and returns the result of the left-hand parser
     * @param pb        the second parser
     * @param <B>       the result type of second parser
     * @return          a parser that applies two parsers consecutively and returns the result of the first
     */
    default <B> Parser<I, A> andL(Parser<I, B> pb) {
        return this.and(pb).map(F2.first());
    }

    /**
     * Combine this parser with another to form a parser which applies two parsers,
     * and if they are both successful
     * throws away the result of the left-hand parser
     * and returns the result of the right-hand parser
     * @param pb        the second parser
     * @param <B>       the result type of second parser
     * @return          a parser that applies two parsers consecutively and returns the result of the second
     */
    default <B> Parser<I, B> andR(Parser<I, B> pb) {
        return this.and(pb).map(F2.second());
    }

    /**
     * A parser which repeatedly applies this parser until it fails,
     * and then returns an {@link IList} of the results.
     * If this parser fails on the first attempt then the parser succeeds,
     * with an empty list of results.
     * @return          a parser which applies this parser zero or more times until it fails
     */
    default Parser<I, IList<A>> many() {
        // We want to provide an alert at construction time if the caller attempts to create a many
        // parser from one that accepts empty (which would lead to an infinite loop at parsing time).
        // But, an unitialise Ref will throw an exception if we call acceptsEmpty,
        // so for that particular case we have to skip the check.
        if (Utils.ifClass(Ref.class, this).map(Ref::initialised).orElse(true)
                && acceptsEmpty().apply()) {
            throw new RuntimeException("Cannot construct a many parser from one that accepts empty");
        }

        // We use an iterative implementation, in favour of a more concise recursive solution,
        // for performance, and to avoid StackOverflowExceptions.
        return new ParserImpl<I, IList<A>>(LTRUE, this.firstSet()) {
            @Override
            public Result<I, IList<A>> apply(Input<I> in, SymSet<I> follow) {
                IList<A> acc = IList.of();
                final SymSet<I> follow2 = follow.union(Parser.this.firstSet().apply());
                while (true) {
                    if (!in.isEof()) {
                        final I i = in.get();
                        if (Parser.this.firstSet().apply().matches(i)) {
                            final Result<I, A> r = Parser.this.apply(in, follow2);
                            if (r.isSuccess()) {
                                final Result.Success<I, A> succ = (Result.Success<I, A>) r;
                                acc = acc.add(succ.value());
                                in = succ.next();
                                continue;
                            } else {
                                return ((Result.Failure<I, A>)r).cast();
                            }
                        }
                    }
                    return Result.success(acc.reverse(), in);
                }
            }
        };
    }

    /**
     * A parser which repeatedly applies this parser until the end parser succeeds,
     * and then returns an {@link IList} of the results.
     * @param end       the end parser
     * @param <B>       the result type of the end parser
     * @return          a parser which applies this parser zero or more times until end succeeds
     */
    @SuppressWarnings("unchecked")
    default <B> Parser<I, IList<A>> manyTill(Parser<I, B> end) {
        return new ParserImpl<I, IList<A>>(
                end.acceptsEmpty(),
                union(Parser.this.firstSet(), end.firstSet())) {
            @Override
            public Result<I, IList<A>> apply(Input<I> in, SymSet<I> follow) {
                IList<A> acc = IList.of();
                final SymSet<I> follow2 = combine(end.acceptsEmpty().apply(), end.firstSet().apply(), follow);
                while (true) {
                    if (!in.isEof()) {
                        final I i = in.get();
                        if (end.firstSet().apply().matches(i)) {
                            final Result<I, B> r = end.apply(in, follow);
                            if (r.isSuccess()) {
                                final Result.Success<I, B> succ = (Result.Success<I, B>) r;
                                in = succ.next();
                            } else {
                                return ((Result.Failure<I, A>)r).cast();
                            }
                        } else if (Parser.this.firstSet().apply().matches(i)) {
                            final Result<I, A> r = Parser.this.apply(in, follow2);
                            if (r.isSuccess()) {
                                final Result.Success<I, A> succ = (Result.Success<I, A>) r;
                                acc = acc.add(succ.value());
                                in = succ.next();
                                continue;
                            } else {
                                return ((Result.Failure<I, A>)r).cast();
                            }
                        }
                    }
                    return Result.success(acc.reverse(), in);
                }
            }
        };
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param ps        the list of parsers
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    static <I, A>
    Parser<I, A> choice(IList.NonEmpty<Parser<I, A>> ps) {
        // We use an iterative implementation for performance, and to avoid StackOverflowExceptions.
        // The more concise recursive equivalent is ps.foldLeft1(Parser::or)
        return new ParserImpl<I, A>(
                ps.map(Parser::acceptsEmpty).foldLeft1(Utils::or),
                ps.map(Parser::firstSet).foldLeft1(Utils::union)
        ) {
            @Override
            public Result<I, A> apply(Input<I> in, SymSet<I> follow) {
                if (in.isEof()) {
                    for (Parser<I, A> p : ps) {
                        if (p.acceptsEmpty().apply()) {
                            return p.apply(in, follow);
                        }
                    }
                    return failureEof(this, in);
                } else {
                    final I next = in.get();
                    for (Parser<I, A> p : ps) {
                        if (p.firstSet().apply().matches(next)) {
                            return p.apply(in, follow);
                        }
                    }
                    if (follow.matches(next)) {
                        for (Parser<I, A> p : ps) {
                            if (p.acceptsEmpty().apply()) {
                                return p.apply(in, follow);
                            }
                        }
                    }
                    return failure(this, in);
                }
            }
        };
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param ps        the list of parsers
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    @SafeVarargs
    static <I, A>
    Parser<I, A> choice(Parser<I, A>... ps) {
        if (ps.length == 0) {
            throw new RuntimeException("Cannot construct a choice from an empty list of parsers");
        } else {
            return choice((IList.NonEmpty<Parser<I, A>>)IList.ofArray(ps));
        }
    }

    /**
     * A parser which applies this parser one or more times until it fails,
     * and then returns an {@link IList} of the results.
     * Note, if this parser fails on the first attempt then the parser fails.
     * @return          a parser which applies this parser repeatedly until it fails
     */
    default Parser<I, IList.NonEmpty<A>> many1() {
        return this.and(this.many())
                .map(a -> l -> l.add(a));
    }

    /**
     * A parser which applies this parser zero or more times until it fails,
     * and throws away the results.
     * Note, if this parser fails on the first attempt then the parser succeeds.
     * @return          a parser which applies this parser repeatedly until it fails
     */
    default Parser<I, Unit> skipMany() {
        return this.many()
                .map(u -> Unit.UNIT);
    }

    /**
     * A parser which applies this parser zero or more times until it fails,
     * alternating with calls to the {@code sep} parser.
     * The results of this parser are collected in a {@link IList}
     * and returned by the parser.
     * @param sep       the separator parser
     * @param <SEP>     the separator type
     * @return          a parser which applies this parser zero or more times alternated with {@code sep}
     */
    default <SEP> Parser<I, IList<A>> sepBy(Parser<I, SEP> sep) {
        // the cast is needed so both branches of the or return the same type
        return this.sepBy1(sep).map(l -> (IList<A>) l)
                .or(Parser.pure(IList.empty()));
    }

    /**
     * A parser which applies this parser one or more times until it fails,
     * alternating with calls to the {@code sep} parser.
     * The results of this parser are collected in a {@link IList}
     * and returned by the parser.
     * @param sep       the separator parser
     * @param <SEP>     the separator type
     * @return          a parser which applies this parser one or more times alternated with {@code sep}
     */
    default <SEP> Parser<I, IList.NonEmpty<A>> sepBy1(Parser<I, SEP> sep) {
        return this.and(sep.andR(this).many())
                .map(a -> l -> l.add(a));
    }

    /**
     * A parser that applies this parser, and, if it succeeds,
     * returns the result wrapped in an {@link Optional},
     * otherwise returns an empty {@code Optional}.
     * @return          an optional parser
     */
    default Parser<I, Optional<A>> optional() {
        return this.map(Optional::of)
                .or(Parser.pure(Optional.empty()));
    }

    /**
     * A parser for expressions with enclosing symbols.
     * <p>
     * A parser which applies the {@code open} parser, then this parser,
     * and then {@code close} parser.
     * If all three succeed then the result of this parser is returned.
     * @param open      the open symbol parser
     * @param close     the close symbol parser
     * @param <OPEN>    the open parser result type
     * @param <CLOSE>   the close parser result type
     * @return          a parser for expressions with enclosing symbols
     */
    default <OPEN, CLOSE>
    Parser<I, A> between(
            Parser<I, OPEN> open,
            Parser<I, CLOSE> close) {
        return open.andR(this).andL(close);
    }

    /**
     * A parser for an operand, followed by zero or more operands that are separated by operators.
     * The operators are right-associative.
     * @param op        the parser for the operator
     * @param a         the value to return if there are no operands
     * @return          a parser for operator expressions
     */

    default Parser<I, A> chainr(Parser<I, Op2<A>> op, A a) {
        return this.chainr1(op).or(pure(a));
    }

    /**
     * A parser for an operand, followed by one or more operands that are separated by operators.
     * The operators are right-associative.
     * @param op        the parser for the operator
     * @return          a parser for operator expressions
     */
    default Parser<I, A> chainr1(Parser<I, Op2<A>> op) {
        return this.and(
                op.and(this)
                        .map(Tuple2::of)
                        .many()
        ).map(Utils::reduce);
    }

    /**
     * A parser for an operand, followed by zero or more operands that are separated by operators.
     * The operators are left-associative.
     * This can, for example, be used to eliminate left recursion
     * which typically occurs in expression grammars.
     * @param op        the parser for the operator
     * @param a         the value to return if there are no operands
     * @return          a parser for operator expressions
     */
    default Parser<I, A> chainl(Parser<I, Op2<A>> op, A a) {
        return this.chainl1(op).or(pure(a));
    }

    /**
     * A parser for an operand, followed by one or more operands that are separated by operators.
     * The operators are left-associative.
     * This can, for example, be used to eliminate left recursion
     * which typically occurs in expression grammars.
     * @param op        the parser for the operator
     * @return          a parser for operator expressions
     */
    default Parser<I, A> chainl1(Parser<I, Functions.Op2<A>> op) {
        final Parser<I, Functions.Op<A>> plo =
                op.and(this)
                        .map((f, y) -> x -> f.apply(x, y));
        return this.and(plo.many())
                .map((a, lf) -> lf.foldLeft((acc, f) -> f.apply(acc), a));
    }
}
