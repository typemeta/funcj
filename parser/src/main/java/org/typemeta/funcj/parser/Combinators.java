package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.tuples.Tuple2;

import static org.typemeta.funcj.parser.Utils.*;

/**
 * Combinators provides functions for combining parsers to form new parsers.
 */
public abstract class Combinators {

    /**
     * A parser that always fails.
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that always fails.
     */
    public static <I, A> Parser<I, A> fail() {
        return new ParserImpl<I, A>(LTRUE, SymSet::empty) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                return failure(this, in);
            }
        };
    }

    /**
     * A parser that always fails.
     * @param msg       the failure message
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that always fails.
     */
    public static <I, A> Parser<I, A> fail(String msg) {
        return new ParserImpl<I, A>(LTRUE, SymSet::empty) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                return failure(msg, in);
            }
        };
    }

    /**
     * A parser that succeeds if the end of the input has been reached.
     * @param <I>       the input stream symbol type
     * @return          a parser that succeeds iff we are at the end of the input.
     */
    public static <I> Parser<I, Unit> eof() {
        return new ParserImpl<I, Unit>(LTRUE, SymSet::empty) {
            @Override
            public Result<I, Unit> parse(Input<I> in, SymSet<I> follow) {
                return in.isEof() ?
                        Result.success(Unit.UNIT, in) :
                        failure(this, in);
            }
        };
    }

    /**
     * A parser that succeeds if the next input symbol equals the supplied {@code value},
     * and returns the value.
     * @param val       the value expected by the parser
     * @param <I>       the input stream symbol type
     * @return          as parser that succeeds if the next input symbol equals the supplied {@code value}
     */
    public static <I> Parser<I, I> value(I val) {
        return value(val, val);
    }

    /**
     * A parser that succeeds if the next inout symbol equals the supplied {@code value},
     * and returns the supplied {@code res} value.
     * @param val       the value expected by the parser
     * @param res       the value returned by the parser
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that succeeds if the next input symbol equals the supplied {@code value}
     */
    public static <I, A> Parser<I, A> value(I val, A res) {
        return new ParserImpl<I, A>(LFALSE, () -> SymSet.value(val)) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                return Result.success(res, in.next());
            }
        };
    }

    /**
     * A parser that succeeds if the next input symbol satisfies the supplied predicate.
     * @param name      a name for the parser (used for error messages)
     * @param pred      the predicate to be applied to the next input
     * @param <I>       the input stream symbol type
     * @return          a parser that succeeds if the next input symbol satisfies the supplied predicate.
     */
    public static <I> Parser<I, I> satisfy(String name, Functions.Predicate<I> pred) {
        return new ParserImpl<I, I>(LFALSE, () -> SymSet.pred(name, pred)) {
            @Override
            public Result<I, I> parse(Input<I> in, SymSet<I> follow) {
                return Result.success(in.get(), in.next());
            }
        };
    }

    /**
     * A parser that succeeds on any input symbol, and returns that symbol.
     * @param <I>       the input stream symbol type
     * @return          a parser that succeeds on any input symbol
     */
    public static <I> Parser<I, I> any() {
        return new ParserImpl<I, I>(LTRUE, SymSet::all) {
            @Override
            public Result<I, I> parse(Input<I> in, SymSet<I> follow) {
                return in.isEof() ?
                        failureEof(this, in) :
                        Result.success(in.get(), in.next());
            }
        };
    }

    /**
     * A parser that succeeds on any input symbol, and returns that symbol.
     * @param <I>       the input stream symbol type
     * @param clazz     dummy p[aram for type inference of generic type {@code I}
     * @return          a parser that succeeds on any input symbol
     */
    public static <I> Parser<I, I> any(Class<I> clazz) {
        return any();
    }

    /**
     * Combine two parser to form a parser which applies both parsers,
     * and if they are both successful then returns a {@link Tuple2} of the results.
     * @param pa        the first parser
     * @param pb        the second parser
     * @param <I>       the input stream symbol type
     * @param <A>       the result type of first parser
     * @param <B>       the result type of second parser
     * @return          a parser that applies two parsers consecutively and returns the pair of values
     */
    public static <I, A, B> Parser<I, Tuple2<A, B>> product(Parser<I, A> pa, Parser<I, B> pb) {
        return pa.and(pb).map(Tuple2::of);
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param ps        the var-arg list of parsers
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    @SafeVarargs
    public static <I, A>
    Parser<I, A> choice(Parser<I, A>... ps) {
        return choice((IList.NonEmpty<Parser<I, A>>) IList.ofArray(ps));
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param p1        the first parser
     * @param p2        the second parser
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    public static <I, A>
    Parser<I, A> choice(Parser<I, ? extends A> p1, Parser<I, ? extends A> p2) {
        return choice(IList.<Parser<I, A>>of(p1.cast(), p2.cast()));
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param p1        the first parser
     * @param p2        the second parser
     * @param p3        the third parser
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    public static <I, A>
    Parser<I, A> choice(Parser<I, ? extends A> p1, Parser<I, ? extends A> p2, Parser<I, ? extends A> p3) {
        return choice(IList.<Parser<I, A>>of(p1.cast(), p2.cast(), p3.cast()));
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param p1        the first parser
     * @param p2        the second parser
     * @param p3        the third parser
     * @param p4        the fourth parser
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    public static <I, A>
    Parser<I, A> choice(
            Parser<I, ? extends A> p1,
            Parser<I, ? extends A> p2,
            Parser<I, ? extends A> p3,
            Parser<I, ? extends A> p4) {
        return choice(IList.<Parser<I, A>>of(p1.cast(), p2.cast(), p3.cast(), p4.cast()));
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param p1        the first parser
     * @param p2        the second parser
     * @param p3        the third parser
     * @param p4        the fourth parser
     * @param p5        the fifth parser
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    public static <I, A>
    Parser<I, A> choice(
            Parser<I, ? extends A> p1,
            Parser<I, ? extends A> p2,
            Parser<I, ? extends A> p3,
            Parser<I, ? extends A> p4,
            Parser<I, ? extends A> p5) {
        return choice(IList.<Parser<I, A>>of(p1.cast(), p2.cast(), p3.cast(), p4.cast(), p5.cast()));
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param p1        the first parser
     * @param p2        the second parser
     * @param p3        the third parser
     * @param p4        the fourth parser
     * @param p5        the fifth parser
     * @param p6        the sixth parser
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    public static <I, A>
    Parser<I, A> choice(
            Parser<I, ? extends A> p1,
            Parser<I, ? extends A> p2,
            Parser<I, ? extends A> p3,
            Parser<I, ? extends A> p4,
            Parser<I, ? extends A> p5,
            Parser<I, ? extends A> p6) {
        return choice(IList.<Parser<I, A>>of(p1.cast(), p2.cast(), p3.cast(), p4.cast(), p5.cast(), p6.cast()));
    }

    /**
     * A parser that attempts one or more parsers in turn and returns the result
     * of the first that succeeds, or else fails.
     * @param ps        the list of parsers
     * @param <I>       the input stream symbol type
     * @param <A>       the parser result type
     * @return          a parser that attempts one or more parsers in turn
     */
    public static <I, A>
    Parser<I, A> choice(IList.NonEmpty<Parser<I, A>> ps) {
        // We use an iterative implementation for performance, and to avoid StackOverflowExceptions.
        // The more concise recursive equivalent is ps.foldLeft1(Parser::or)
        return new ParserImpl<I, A>(
                ps.map(Parser::acceptsEmpty).foldLeft1(Utils::or),
                ps.map(Parser::firstSet).foldLeft1(Utils::union)
        ) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                if (in.isEof()) {
                    for (Parser<I, A> p : ps) {
                        if (p.acceptsEmpty().apply()) {
                            return p.parse(in, follow);
                        }
                    }
                    return failureEof(this, in);
                } else {
                    final I next = in.get();
                    for (Parser<I, A> p : ps) {
                        if (p.firstSet().apply().matches(next)) {
                            return p.parse(in, follow);
                        }
                    }
                    if (follow.matches(next)) {
                        for (Parser<I, A> p : ps) {
                            if (p.acceptsEmpty().apply()) {
                                return p.parse(in, follow);
                            }
                        }
                    }
                    return failure(this, in);
                }
            }
        };
    }

}
