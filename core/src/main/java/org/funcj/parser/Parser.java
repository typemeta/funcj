package org.funcj.parser;

import org.funcj.data.*;
import org.funcj.util.Functions.*;

import java.util.Optional;

import static org.funcj.parser.Parser.pure;
import static org.funcj.parser.ParserUtils.*;
import static org.funcj.util.Functions.F2.curry;

/**
 * A parser is essentially a function from an input stream to a parse {@link org.funcj.parser.Result}.
 * The {@code Parser} type along with the {@code pure} and {@code ap} functions constitute an applicative functor.
 * @param <I> input stream symbol type
 * @param <A> parse result type
 */
public interface Parser<I, A> {

    /**
     * Indicate whether this parser accepts the empty symbol.
     * @return a lazy wrapper for true iff the parser accepts the empty symbol
     */
    Lazy<Boolean> acceptsEmpty();

    /**
     * The First Set for this parser.
     * @return lazy symbol set
     */
    Lazy<SymSet<I>> firstSet();

    /**
     * Apply this parser to the input stream.
     * @param in input stream
     * @param follow dynamic follow set
     * @return the parse result
     */
    Result<I, A> parse(Input<I> in, SymSet<I> follow);

    /**
     * Apply this parser to the input stream.
     * @param in input stream
     * @return the parser result
     */
    default Result<I, A> parse(Input<I> in) {
        return this.parse(in, SymSet.empty());
    }

    /**
     * Apply this parser to the input stream. Fail if eof isn't reached.
     * @param in input stream
     * @return the parser result
     */
    default Result<I, A> run(Input<I> in) {
        final Parser<I, A> parserAndEof = this.andL(eof());
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
     * Applicative unit/pure function.
     * Construct a parser that always returns the supplied value, without consuming any input.
     * @param a value
     * @param <I> input stream symbol type
     * @param <A> parse result type
     * @return a parser that always returns the supplied value
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
     * Functor map operation.
     * If this parser succeeds then apply the function {@code f} to the result,
     * otherwise return the failure.
     * @param f function to be mapped over this parser
     * @param <B> function return type
     * @return a parser that returns {@code f} mapped over this parser's result
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
     * Applicative application.
     * Construct a parser that, if {@code pf} succeeds, yielding a function {@code f},
     * and if {@code pa} succeeds, yielding a value {@code a},
     * then it returns the result of applying function {@code f} to value {@code a}.
     * Otherwise, if {@code pf} fails then the parser returns the failure,
     * else if {@code pa} fails then it returns that failure.
     * @param pf parser that returns a function result
     * @param pa parser that returns a value result
     * @param <I> input stream symbol type
     * @param <A> input type of the function
     * @param <B> return type of the function
     * @return a parser that returns the result of applying the parsed function to the parsed value
     */
    static <I, A, B>
    Parser<I, B> ap(Parser<I, F<A, B>> pf, Parser<I, A> pa) {
        return new ParserImpl<I, B>(
            ParserUtils.and(pf.acceptsEmpty(), pa.acceptsEmpty()),
            ParserUtils.combine(pf.acceptsEmpty(), pf.firstSet(), pa.firstSet())
        ) {
            @Override
            public Result<I, B> parse(Input<I> in, SymSet<I> follow) {
                final SymSet<I> followF =
                    ParserUtils.combine(
                        pa.acceptsEmpty().apply(),
                        pa.firstSet().apply(),
                        follow);

                final Result<I, F<A, B>> r = pf.parse(in, followF);

                if (r.isSuccess()) {
                    final Result.Success<I, F<A, B>> succ = (Result.Success<I, F<A, B>>) r;
                    final Result<I, A> r2 = pa.parse(succ.next(), follow);
                    return r2.map(succ.value());
                } else {
                    return ((Result.Failure<I, F<A, B>>) r).cast();
                }
            }
        };
    }

    /**
     * Applicative application.
     * Construct a parser that, if {@code pa} succeeds, yielding a function {@code a},
     * then it returns the result of applying function {@code f} to value {@code a}.
     * If {@code pa} fails then the parser returns the failure.
     * @param f a function
     * @param pa parser that returns a value result
     * @param <I> input stream symbol type
     * @param <A> input type of the function
     * @param <B> return type of the function
     * @return a parser that returns the result of applying the function to the parsed value
     */
    static <I, A, B>
    Parser<I, B> ap(F<A, B> f, Parser<I, A> pa) {
        return ap(pure(f), pa);
    }

    /**
     * Alternative.
     * Construct a parser which returns the result of either this parser or,
     * if it fails, then the result of the {@code rhs} parser.
     * @param rhs alternative parser
     * @return a parser which returns the result of either this parser or the {@code rhs} parser.
     */
    default Parser<I, A> or(Parser<I, A> rhs) {
        return new ParserImpl<I, A>(
            ParserUtils.or(Parser.this.acceptsEmpty(), rhs.acceptsEmpty()),
            ParserUtils.union(Parser.this.firstSet(), rhs.firstSet())
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

    default <B> Parser<I, Tuple2<A, B>> product(Parser<I, B> pb) {
        return ap(this.map(curry(Tuple2::new)), pb);
    }

    default <B> Parser<I, A> andL(Parser<I, B> pb) {
        return this.and(pb).map(F2.first());
    }

    default <B> Parser<I, B> andR(Parser<I, B> pb) {
        return this.and(pb).map(F2.second());
    }

    default <B> ApplyBuilder._2<I, A, B> and(Parser<I, B> pb) {
        return new ApplyBuilder._2<I, A, B>(this, pb);
    }

    default Parser<I, A> chainl1(Parser<I, Op2<A>> op) {
        final Parser<I, IList<Op<A>>> plf =
            many(op.and(this)
                .map((f, y) -> x -> f.apply(x, y)));
        return this.and(plf)
            .map((a, lf) -> lf.foldLeft((acc, f) -> f.apply(acc), a));
    }

    static <I, A> Parser<I, A> fail() {
        return new ParserImpl<I, A>(() -> true, SymSet::empty) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                return failure(this, in);
            }
        };
    }

    static <I, A, B>
    F<Parser<I, A>, Parser<I, B>> liftA(F<A, B> f) {
        return a -> a.map(f);
    }

    static <I, A, B, C>
    F<Parser<I, A>, F<Parser<I, B>, Parser<I, C>>> liftA2(F<A, F<B, C>> f) {
        return a -> b -> ap(a.map(f), b);
    }

    static <I> Parser<I, Unit> eof() {
        return new ParserImpl<I, Unit>(LTRUE, SymSet::empty) {
            @Override
            public Result<I, Unit> parse(Input<I> in, SymSet<I> follow) {
                return in.isEof() ?
                    Result.success(Unit.UNIT, in) :
                    failure(this, in);
            }
        };
    }

    static <I> Parser<I, I> satisfy(String name, Predicate<I> pred) {
        return new ParserImpl<I, I>(LFALSE, () -> SymSet.pred(name, pred)) {
            @Override
            public Result<I, I> parse(Input<I> in, SymSet<I> follow) {
                return Result.success(in.get(), in.next());
            }
        };
    }

    static <I> Parser<I, I> value(I val) {
        return value(val, val);
    }

    static <I, A> Parser<I, A> value(I val, A res) {
        return new ParserImpl<I, A>(LFALSE, () -> SymSet.value(val)) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                return Result.success(res, in.next());
            }
        };
    }

    static <I> Parser<I, I> any() {
        return new ParserImpl<I, I>(LTRUE, SymSet::all) {
            @Override
            public Result<I, I> parse(Input<I> in, SymSet<I> follow) {
                return in.isEof() ?
                    failureEof(this, in) :
                    Result.success(in.get(), in.next());
            }
        };
    }

    static <I, A>
    Parser<I, IList<A>> many(Parser<I, A> p) {
        return new ParserImpl<I, IList<A>>(LTRUE, p.firstSet()) {
            @Override
            public Result<I, IList<A>> parse(Input<I> in, SymSet<I> follow) {
                IList<A> acc = IList.of();
                final SymSet<I> follow2 = follow.union(p.firstSet().apply());
                while (true) {
                    if (!in.isEof()) {
                        final I i = in.get();
                        if (firstSet().apply().matches(i)) {
                            final Result<I, A> r = p.parse(in, follow2);
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

    static <I, A>
    Parser<I, IList.NonEmpty<A>> many1(Parser<I, A> p) {
        return p.and(many(p))
            .map(a -> l -> l.add(a));
    }

    static <I, A>
    Parser<I, Unit> skipMany(Parser<I, A> p) {
        return many(p).map(u -> Unit.UNIT);
    }

    static <I, A, SEP>
    Parser<I, IList<A>> sepBy(Parser<I, A> p, Parser<I, SEP> sep) {
        return sepBy1(p, sep).or(pure(IList.nil()));
    }

    static <I, A, SEP>
    Parser<I, IList<A>> sepBy1(Parser<I, A> p, Parser<I, SEP> sep) {
        return p.and(many(sep.andR(p)))
            .map(a -> l -> l.add(a));
    }

    static <I, A>
    Parser<I, Optional<A>> optional(Parser<I, A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }

    static <I, A, OPEN, CLOSE>
    Parser<I, A> between(
            Parser<I, OPEN> open,
            Parser<I, CLOSE> close,
            Parser<I, A> p) {
        return open.andR(p).andL(close);
    }

    static <I, A>
    Parser<I, A> choice(Parser<I, A>... ps) {
        return choice(IList.ofArray(ps));
    }

    static <I, A>
    Parser<I, A> choice(IList<Parser<I, A>> ps) {
        return ps.foldLeft1(Parser::or);
    }
}

/**
 * Base class for {@code Parser} implementations.
 * @param <I> input stream symbol type
 * @param <A> parse result type
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

