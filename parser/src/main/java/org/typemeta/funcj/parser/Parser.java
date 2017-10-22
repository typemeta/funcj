package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.*;
import org.typemeta.funcj.functions.Functions.*;

import java.util.Iterator;
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
     * @return          the initialised parser reference
     */
    static <I, A> Ref<I, A> ref(Parser<I, A> p) {
        return new Ref<I, A>(p);
    }

    /**
     * Applicative unit/pure function.
     * Construct a parser that always returns the supplied value, without consuming any input.
     * @param a         the value
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that always returns the supplied value
     */
    static <I, A> Parser<I, A> pure(A a) {
        return new ParserImpl<I, A>(LTRUE, SymSet::empty) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
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
            public Result<I, B> parse(Input<I> in, SymSet<I> follow) {
                final SymSet<I> followF =
                        combine(
                                pa.acceptsEmpty().apply(),
                                pa.firstSet().apply(),
                                follow);

                final Result<I, F<A, B>> r = pf.parse(in, followF);

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

                    final Result<I, A> r2 = pa.parse(next, follow);
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
                pure(IList.nil())
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
                pure(IList.nil())
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
        Parser<E, IList<T>> plt = pure(IList.nil());
        while (iter.hasNext()) {
            final Parser<E, T> pt = iter.next();
            plt = ap(plt.map(lt -> lt::add), pt);
        }
        return plt.map(IList::stream);
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
     * @param in        the input stream
     * @param follow    the dynamic follow set
     * @return          the parse result
     */
    Result<I, A> parse(Input<I> in, SymSet<I> follow);

    /**
     * Apply this parser to the input stream.
     * @param in        the input stream
     * @return          the parser result
     */
    default Result<I, A> parse(Input<I> in) {
        return this.parse(in, SymSet.empty());
    }

    /**
     * Apply this parser to the input stream. Fail if eof isn't reached.
     * @param in        the input stream
     * @return          the parser result
     */
    default Result<I, A> run(Input<I> in) {
        final Parser<I, A> parserAndEof = this.andL(Combinators.eof());
        if (acceptsEmpty().apply()) {
            return parserAndEof.parse(in, SymSet.empty());
        } else if (in.isEof()) {
            return failureEof(this, in);
        } else if (firstSet().apply().matches(in.get())) {
            return parserAndEof.parse(in, SymSet.empty());
        } else {
            return failure(this, in);
        }
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
            public Result<I, B> parse(Input<I> in, SymSet<I> follow) {
                return Parser.this.parse(in, follow).map(f);
            }
        };
    }

    /**
     * Construct a parser which returns the result of either this parser or,
     * if it fails, then the result of the {@code rhs} parser.
     * @param rhs       the second parser to attempt
     * @return          a parser which returns the result of either this parser or the {@code rhs} parser.
     */
    default Parser<I, A> or(Parser<I, A> rhs) {
        return new ParserImpl<I, A>(
            Utils.or(Parser.this.acceptsEmpty(), rhs.acceptsEmpty()),
            union(Parser.this.firstSet(), rhs.firstSet())
        ) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                if (in.isEof()) {
                    if (Parser.this.acceptsEmpty().apply()) {
                        return Parser.this.parse(in, follow);
                    } else if (rhs.acceptsEmpty().apply()) {
                        return rhs.parse(in, follow);
                    } else {
                        return failureEof(this, in);
                    }
                } else {
                    final I next = in.get();
                    if (Parser.this.firstSet().apply().matches(next)) {
                        return Parser.this.parse(in, follow);
                    } else if (rhs.firstSet().apply().matches(next)) {
                        return rhs.parse(in, follow);
                    } else if (follow.matches(next)) {
                        if (Parser.this.acceptsEmpty().apply()) {
                            return Parser.this.parse(in, follow);
                        } else if (rhs.acceptsEmpty().apply()) {
                            return rhs.parse(in, follow);
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
}
