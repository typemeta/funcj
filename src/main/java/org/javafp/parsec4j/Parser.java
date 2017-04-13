package org.javafp.parsec4j;

import org.javafp.data.*;
import org.javafp.util.Functions.*;
import org.javafp.util.Unit;

import java.util.*;

import static org.javafp.util.Functions.F2.curry;

/**
 * A parser is essentially a function from an input stream to a Result.
 * The ParserImpl type along with the pure and map functions constitute an applicative functor.
 * @param <I> Input stream symbol type.
 * @param <A> Parse result type
 */
public interface Parser<I, CTX extends Parser.Context<I>, A> {

    interface Context<I> {
        Input<I> input();

        default boolean isEof(int pos) {
            return input().isEof(pos);
        }

        default I at(int pos) {
            return input().at(pos);
        }
    }

    class Ctx<I> implements Context<I> {
        private final Input<I> input;

        public Ctx(Input<I> input) {
            this.input = input;
        }

        @Override
        public Input<I> input() {
            return input;
        }
    }

    boolean acceptsEmpty();

    SymSet<I> firstSet();

    Result<I, A> parse(CTX ctx, int pos);

    default Result<I, A> run(CTX ctx) {
        return this.andL(eof()).parse(ctx, 0);
    }

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> pure(A a) {
        return new ParserImpl<I, CTX, A>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<I> firstSet() {
                return SymSet.empty();
            }

            @Override
            public Result<I, A> parse(CTX ctx, int pos) {
                return Result.success(a, pos);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, A> pure(F0<A> fa) {
        return pure(fa.apply());
    }

    default <B> Parser<I, CTX, B> map(F<A, B> f) {
        return new ParserImpl<I, CTX, B>() {
            @Override
            public boolean acceptsEmptyCalc() {
                return Parser.this.acceptsEmpty();
            }

            @Override
            protected SymSet<I> firstSetCalc() {
                return Parser.this.firstSet();
            }

            @Override
            public Result<I, B> parse(CTX ctx, int pos) {
                return Parser.this.parse(ctx, pos).map(f);
            }
        };
    }

    default Parser<I, CTX, A> or(Parser<I, CTX, A> rhs) {
        return new ParserImpl<I, CTX, A>() {
            @Override
            public boolean acceptsEmptyCalc() {
                return Parser.this.acceptsEmpty() || rhs.acceptsEmpty();
            }

            @Override
            protected SymSet<I> firstSetCalc() {
                return Parser.this.firstSet().union(rhs.firstSet());
            }


            @Override
            public Result<I, A> parse(CTX ctx, int pos) {
                final Input<I> in = ctx.input();
                if (ctx.isEof(pos)) {
                    if (Parser.this.acceptsEmpty()) {
                        return Parser.this.parse(ctx, pos);
                    } else if (rhs.acceptsEmpty()) {
                        return rhs.parse(ctx, pos);
                    } else {
                        return Result.failure(pos);
                    }
                } else {
                    final I i = in.at(pos);
                    if (Parser.this.firstSet().matches(i)) {
                        return Parser.this.parse(ctx, pos);
                    } else if (rhs.firstSet().matches(i)) {
                        return rhs.parse(ctx, pos);
                    } else {
                        if (Parser.this.acceptsEmpty()) {
                            final Result<I, A> r = Parser.this.parse(ctx, pos);
                            if (r.isSuccess()) {
                                return r;
                            }
                        }

                        if (rhs.acceptsEmpty()) {
                            final Result<I, A> r = rhs.parse(ctx, pos);
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

    default <B> Parser<I, CTX, Tuple2<A, B>> product(Parser<I, CTX, B> pb) {
        return ap(this.map(curry(Tuple2::new)), pb);
    }

    default <B> Parser<I, CTX, A> andL(Parser<I, CTX, B> pb) {
        return this.and(pb).map(F2.first());
    }

    default <B> Parser<I, CTX, B> andR(Parser<I, CTX, B> pb) {
        return this.and(pb).map(F2.second());
    }

    default <B> ApplyBuilder._2<I, CTX, A, B> and(Parser<I, CTX, B> pb) {
        return new ApplyBuilder._2<I, CTX, A, B>(this, pb);
    }

    default Parser<I, CTX, A> chainl1(Parser<I, CTX, Op2<A>> op) {
        final Parser<I, CTX, IList<Op<A>>> plf =
            many(op.and(this)
                .map((f, y) -> x -> f.apply(x, y)));
        return this.and(plf)
            .map((a, lf) -> lf.foldl((acc, f) -> f.apply(acc), a));
    }

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> fail() {
        return new ParserImpl<I, CTX, A>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<I> firstSet() {
                return SymSet.empty();
            }

            @Override
            public Result<I, A> parse(CTX ctx, int pos) {
                return Result.failure(pos);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A, B>
    Parser<I, CTX, B> ap(Parser<I, CTX, F<A, B>> pf, Parser<I, CTX, A> pa) {
        return new ParserImpl<I, CTX, B>() {
            @Override
            public boolean acceptsEmptyCalc() {
                return pf.acceptsEmpty() && pa.acceptsEmpty();
            }

            @Override
            protected SymSet<I> firstSetCalc() {
                if (pf.acceptsEmpty()) {
                    return pf.firstSet().union(pa.firstSet());
                } else {
                    return pf.firstSet();
                }
            }

            @Override
            public Result<I, B> parse(CTX ctx, int pos) {
                return pf.parse(ctx, pos)
                    .match(
                        succ -> pa.parse(ctx, succ.next).map(succ.value),
                        fail -> fail.cast()
                    );
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A, B>
    Parser<I, CTX, B> ap(F<A, B> f, Parser<I, CTX, A> pa) {
        return new ParserImpl<I, CTX, B>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            protected SymSet<I> firstSetCalc() {
                return pa.firstSet();
            }

            @Override
            public Result<I, B> parse(CTX ctx, int pos) {
                return pa.parse(ctx, pos).map(f);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A, B>
    F<Parser<I, CTX, A>, Parser<I, CTX, B>> liftA(F<A, B> f) {
        return a -> a.map(f);
    }

    static <I, CTX extends Parser.Context<I>, A, B, C>
    F<Parser<I, CTX, A>, F<Parser<I, CTX, B>, Parser<I, CTX, C>>> liftA2(F<A, F<B, C>> f) {
        return a -> b -> ap(a.map(f), b);
    }

    static <I, CTX extends Parser.Context<I>> Parser<I, CTX, Unit> eof() {
        return new ParserImpl<I, CTX, Unit>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<I> firstSetCalc() {
                return SymSet.empty();
            }

            @Override
            public Result<I, Unit> parse(CTX ctx, int pos) {
                return ctx.input().isEof(pos) ?
                    Result.success(Unit.UNIT, pos) :
                    Result.failure(pos);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>> Parser<I, CTX, I> satisfy(Predicate<I> pred) {
        return new ParserImpl<I, CTX, I>() {
            @Override
            public boolean acceptsEmpty() {
                return false;
            }

            @Override
            protected SymSet<I> firstSetCalc() {
                return SymSet.pred(pred);
            }

            @Override
            public Result<I, I> parse(CTX ctx, int pos) {
                if (!ctx.input().isEof(pos)) {
                    final I i = ctx.input().at(pos);
                    if (pred.apply(i)) {
                        return Result.success(i, pos+1);
                    }
                }

                return Result.failure(pos);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>> Parser<I, CTX, I> value(I val) {
        return value(val, val);
    }

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> value(I val, A res) {
        return new ParserImpl<I, CTX, A>() {
            @Override
            public boolean acceptsEmpty() {
                return false;
            }

            @Override
            protected SymSet<I> firstSetCalc() {
                return SymSet.value(val);
            }

            @Override
            public Result<I, A> parse(CTX ctx, int pos) {
                if (!ctx.input().isEof(pos)) {
                    final I i = ctx.input().at(pos);
                    if (i.equals(val)) {
                        return Result.success(res, pos+1);
                    }
                }

                return Result.failure(pos);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>> Parser<I, CTX, I> any() {
        return new ParserImpl<I, CTX, I>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            public SymSet<I> firstSetCalc() {
                return SymSet.all();
            }

            @Override
            public Result<I, I> parse(CTX ctx, int pos) {
                return ctx.input().isEof(pos) ?
                    Result.failure(pos) :
                    Result.success(ctx.input().at(pos), pos+1);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, IList<A>> many(Parser<I, CTX, A> p) {
        return Impl.many(p).map(IList::reverse);
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, IList.NonEmpty<A>> many1(Parser<I, CTX, A> p) {
        return p.and(Impl.many(p))
            .map(a -> l -> l.reverse().add(a));
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, Unit> skipMany(Parser<I, CTX, A> p) {
        return Impl.many(p).map(u -> Unit.UNIT);
    }

    static <I, CTX extends Parser.Context<I>, A, SEP>
    Parser<I, CTX, IList<A>> sepBy(Parser<I, CTX, A> p, Parser<I, CTX, SEP> sep) {
        return sepBy1(p, sep).or(pure(IList.nil()));
    }

    static <I, CTX extends Parser.Context<I>, A, SEP>
    Parser<I, CTX, IList<A>> sepBy1(Parser<I, CTX, A> p, Parser<I, CTX, SEP> sep) {
        return p.and(many(sep.andR(p)))
            .map(a -> l -> l.add(a));
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, Optional<A>> optional(Parser<I, CTX, A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }

    static <I, CTX extends Parser.Context<I>, A, OPEN, CLOSE>
    Parser<I, CTX, A> between(
            Parser<I, CTX, OPEN> open,
            Parser<I, CTX, CLOSE> close,
            Parser<I, CTX, A> p) {
        return open.andR(p).andL(close);
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, A> choice(Parser<I, CTX, A>... ps) {
        return choice(IList.ofArray(ps));
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, A> choice(IList<Parser<I, CTX, A>> ps) {
        if (ps.tail().isEmpty()) {
            return ps.head();
        } else {
            return ps.head().or(choice(ps.tail()));
        }
    }
}

abstract class ParserImpl<I, CTX extends Parser.Context<I>, A> implements Parser<I, CTX, A> {

    private Boolean acceptsEmpty;

    private SymSet<I> firstSet;

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

    public SymSet<I> firstSet() {
        if (firstSet == null) {
            firstSet = Objects.requireNonNull(firstSetCalc());
        }
        return firstSet;
    }

    protected boolean acceptsEmptyCalc() {
        throw new IllegalStateException("acceptsEmptyCalc() not implemented");
    }

    protected SymSet<I> firstSetCalc() {
        throw new IllegalStateException("firstSetCalc() not implemented");
    }
}

abstract class Impl {
    static <I, CTX extends Parser.Context<I>, A>
    ParserImpl<I, CTX, IList<A>> many(Parser<I, CTX, A> p) {
        return new ParserImpl<I, CTX, IList<A>>() {
            @Override
            public boolean acceptsEmpty() {
                return true;
            }

            @Override
            protected SymSet<I> firstSetCalc() {
                return p.firstSet();
            }

            @Override
            public Result<I, IList<A>> parse(CTX ctx, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<I, A> r = p.parse(ctx, pos);
                    if (r.isSuccess()) {
                        final Result.Success<I, A> succ = (Result.Success<I, A>) r;
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
