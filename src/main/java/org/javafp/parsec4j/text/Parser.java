package org.javafp.parsec4j.text;

import org.javafp.parsec4j.SymSet;
import org.javafp.util.*;
import org.javafp.util.Functions.*;
import org.javafp.data.*;

import java.util.*;

import static org.javafp.util.Functions.F2.curry;

/**
 * A parser is essentially a function from an input stream to a Result.
 * @param <A> Parse result type
 */
public interface Parser<A> {

    boolean acceptsEmpty();

    SymSet<Chr> firstSet();

    Result<A> parse(Input in, int pos);

    default Result<A> run(Input in) {
        return this.andL(eof()).parse(in, 0);
    }

    static <A> Parser<A> pure(A a) {
        return new ParserImpl<A>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<Chr> firstSet() {
                return SymSet.empty();
            }

            @Override
            public Result<A> parse(Input in, int pos) {
                return Result.success(a, pos);
            }
        };
    }

    static <A>
    Parser<A> pure(F0<A> fa) {
        return pure(fa.apply());
    }

    default <B> Parser<B> map(F<A, B> f) {
        return new ParserImpl<B>() {
            @Override
            public boolean acceptsEmptyCalc() {
                return Parser.this.acceptsEmpty();
            }

            @Override
            protected SymSet<Chr> firstSetCalc() {
                return Parser.this.firstSet();
            }

            @Override
            public Result<B> parse(Input in, int pos) {
                return Parser.this.parse(in, pos).map(f);
            }
        };
    }

    default Parser<A> or(Parser<A> rhs) {
        return new ParserImpl<A>() {
            @Override
            public boolean acceptsEmptyCalc() {
                return Parser.this.acceptsEmpty() || rhs.acceptsEmpty();
            }

            @Override
            protected SymSet<Chr> firstSetCalc() {
                return Parser.this.firstSet().union(rhs.firstSet());
            }

            @Override
            public Result<A> parse(Input in, int pos) {
                if (in.isEof(pos)) {
                    if (Parser.this.acceptsEmpty()) {
                        return Parser.this.parse(in, pos);
                    } else if (rhs.acceptsEmpty()) {
                        return rhs.parse(in, pos);
                    } else {
                        return Result.failure(pos);
                    }
                } else {
                    final Chr c = Chr.valueOf(in.at(pos));
                    if (Parser.this.firstSet().matches(c)) {
                        return Parser.this.parse(in, pos);
                    } else if (rhs.firstSet().matches(c)) {
                        return rhs.parse(in, pos);
                    } else {
                        if (Parser.this.acceptsEmpty()) {
                            final Result<A> r = Parser.this.parse(in, pos);
                            if (r.isSuccess()) {
                                return r;
                            }
                        }

                        if (rhs.acceptsEmpty()) {
                            final Result<A> r = rhs.parse(in, pos);
                            if (r.isSuccess()) {
                                return r;
                            }
                        }

                        return Result.failure(pos);
                    }
                }
            }
        };
    }

    default <B> Parser<Tuple2<A, B>> product(Parser<B> pb) {
        return ap(this.map(curry(Tuple2::new)), pb);
    }

    default <B> Parser<A> andL(Parser<B> pb) {
        return this.and(pb).map(F2.first());
    }

    default <B> Parser<B> andR(Parser<B> pb) {
        return this.and(pb).map(F2.second());
    }

    default <B> ApplyBuilder._2<A, B> and(Parser<B> pb) {
        return new ApplyBuilder._2<A, B>(this, pb);
    }

    default Parser<A> chainl1(Parser<Op2<A>> op) {
        final Parser<IList<Op<A>>> plf =
            many(op.and(this)
                .map((f, y) -> x -> f.apply(x, y)));
        return this.and(plf)
            .map((a, lf) -> lf.foldl((acc, f) -> f.apply(acc), a));
    }

    static <A> Parser<A> fail() {
        return new ParserImpl<A>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<Chr> firstSet() {
                return SymSet.empty();
            }

            @Override
            public Result<A> parse(Input in, int pos) {
                return Result.failure(pos);
            }
        };
    }

    static <A, B>
    Parser<B> ap(Parser<F<A, B>> pf, Parser<A> pa) {
        return new ParserImpl<B>() {
            @Override
            public boolean acceptsEmptyCalc() {
                return pf.acceptsEmpty() && pa.acceptsEmpty();
            }

            @Override
            protected SymSet<Chr> firstSetCalc() {
                if (pf.acceptsEmpty()) {
                    return pf.firstSet().union(pa.firstSet());
                } else {
                    return pf.firstSet();
                }
            }

            @Override
            public Result<B> parse(Input in, int pos) {
                return pf.parse(in, pos)
                    .match(
                        succ -> pa.parse(in, succ.next).map(succ.value),
                        fail -> fail.cast()
                    );
            }
        };
    }

    static <A, B>
    Parser<B> ap(F<A, B> f, Parser<A> pa) {
        return new ParserImpl<B>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            protected SymSet<Chr> firstSetCalc() {
                return pa.firstSet();
            }

            @Override
            public Result<B> parse(Input in, int pos) {
                return pa.parse(in, pos).map(f);
            }
        };
    }

    static <A, B> F<Parser<A>, Parser<B>> liftA(F<A, B> f) {
        return a -> a.map(f);
    }

    static <A, B, C> F<Parser<A>, F<Parser<B>, Parser<C>>> liftA2(F<A, F<B, C>> f) {
        return a -> b -> ap(a.map(f), b);
    }

    static Parser<Unit> eof() {
        return new ParserImpl<Unit>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<Chr> firstSetCalc() {
                return SymSet.empty();
            }

            @Override
            public Result<Unit> parse(Input in, int pos) {
                return in.isEof(pos) ?
                    Result.success(Unit.UNIT, pos) :
                    Result.failure(pos);
            }
        };
    }

    static Parser<Chr> satisfy(Predicate<Chr> pred) {
        return new ParserImpl<Chr>() {
            @Override
            public boolean acceptsEmpty() {
                return false;
            }

            @Override
            protected SymSet<Chr> firstSetCalc() {
                return SymSet.pred(pred);
            }

            @Override
            public Result<Chr> parse(Input in, int pos) {
                if (!in.isEof(pos)) {
                    final Chr c = Chr.valueOf(in.at(pos));
                    if (pred.apply(c)) {
                        return Result.success(c, pos+1);
                    }
                }

                return Result.failure(pos);
            }
        };
    }

    static Parser<Chr> value(char val) {
        return value(val, Chr.valueOf(val));
    }

    static <A> Parser<A> value(char val, A res) {
        return new ParserImpl<A>() {
            @Override
            public boolean acceptsEmpty() {
                return false;
            }

            @Override
            protected SymSet<Chr> firstSetCalc() {
                return SymSet.value(Chr.valueOf(val));
            }

            @Override
            public Result<A> parse(Input in, int pos) {
                if (!in.isEof(pos)) {
                    final char c = in.at(pos);
                    if (c == val) {
                        return Result.success(res, pos+1);
                    }
                }

                return Result.failure(pos);
            }
        };
    }

    static Parser<Chr> any() {
        return new ParserImpl<Chr>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<Chr> firstSetCalc() {
                return SymSet.all();
            }

            @Override
            public Result<Chr> parse(Input in, int pos) {
                return in.isEof(pos) ?
                    Result.failure(pos) :
                    Result.success(Chr.valueOf(in.at(pos)), pos+1);
            }
        };
    }

    static <A> Parser<IList<A>> many(Parser<A> p) {
        return Impl.many(p).map(IList::reverse);
    }

    static <A> Parser<IList.NonEmpty<A>> many1(Parser<A> p) {
        return p.and(Impl.many(p))
            .map(a -> l -> l.reverse().add(a));
    }

    static <A> Parser<Unit> skipMany(Parser<A> p) {
        return Impl.many(p).map(u -> Unit.UNIT);
    }

    static <A, SEP> Parser<IList<A>> sepBy(Parser<A> p, Parser<SEP> sep) {
        return sepBy1(p, sep).or(pure(IList.nil()));
    }

    static <A, SEP> Parser<IList<A>> sepBy1(Parser<A> p, Parser<SEP> sep) {
        return p.and(many(sep.andR(p)))
            .map(a -> l -> l.add(a));
    }

    static <A> Parser<Optional<A>> optional(Parser<A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }

    static <A, OPEN, CLOSE> Parser<A> between(
            Parser<OPEN> open,
            Parser<CLOSE> close,
            Parser<A> p) {
        return open.andR(p).andL(close);
    }

    static <A> Parser<A> choice(Parser<A>... ps) {
        return choice(IList.ofArray(ps));
    }

    static <A> Parser<A> choice(IList<Parser<A>> ps) {
        if (ps.tail().isEmpty()) {
            return ps.head();
        } else {
            return ps.head().or(choice(ps.tail()));
        }
    }
}

abstract class ParserImpl<A> implements Parser<A> {

    private Boolean acceptsEmpty;

    private SymSet<Chr> firstSet;

    protected ParserImpl() {
        this.acceptsEmpty = null;
        this.firstSet = null;
    }

    public boolean acceptsEmpty() {
        if (acceptsEmpty == null) {
            acceptsEmpty = Objects.requireNonNull(acceptsEmptyCalc());
        }
        return acceptsEmpty;
    }

    public SymSet<Chr> firstSet() {
        if (firstSet == null) {
            firstSet = Objects.requireNonNull(firstSetCalc());
        }
        return firstSet;
    }

    protected boolean acceptsEmptyCalc() {
        throw new IllegalStateException("acceptsEmptyCalc() not implemented");
    }

    protected SymSet<Chr> firstSetCalc() {
        throw new IllegalStateException("firstSetCalc() not implemented");
    }
}

abstract class Impl {
    static <A> ParserImpl<IList<A>> many(Parser<A> p) {
        return new ParserImpl<IList<A>>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            protected SymSet<Chr> firstSetCalc() {
                return p.firstSet();
            }

            @Override
            public Result<IList<A>> parse(Input in, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<A> r = p.parse(in, pos);
                    if (r.isSuccess()) {
                        final Result.Success<A> succ = (Result.Success<A>) r;
                        acc = acc.add(succ.value);
                        pos = succ.next;
                    } else {
                        return Result.success(acc, pos);
                    }
                }
            }
        };
    }
}
