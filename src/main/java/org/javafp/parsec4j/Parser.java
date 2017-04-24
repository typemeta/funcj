package org.javafp.parsec4j;

import org.javafp.data.*;
import org.javafp.util.Functions.*;
import org.javafp.util.Unit;

import java.util.Optional;

import static org.javafp.parsec4j.Impl.FALSE;
import static org.javafp.parsec4j.Impl.TRUE;
import static org.javafp.parsec4j.Parser.pure;
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

    Lazy<Boolean> acceptsEmpty();

    Lazy<SymSet<I>> firstSet();

    Result<I, A> parse(CTX ctx, int pos, SymSet<I> follow);

    default Result<I, A> run(CTX ctx) {
        return this.andL(eof()).parse(ctx, 0, SymSet.empty());
    }

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> pure(A a) {
        return new ParserImpl<I, CTX, A>(TRUE, SymSet::empty) {
            @Override
            public Result<I, A> parse(CTX ctx, int pos, SymSet<I> follow) {
                return Result.success(a, pos);
            }
        };
    }

    default <B> Parser<I, CTX, B> map(F<A, B> f) {
        return new ParserImpl<I, CTX, B>(
            Parser.this.acceptsEmpty(),
            Parser.this.firstSet()
        ) {
            @Override
            public Result<I, B> parse(CTX ctx, int pos, SymSet<I> follow) {
                return Parser.this.parse(ctx, pos, follow).map(f);
            }
        };
    }

    default Parser<I, CTX, A> or(Parser<I, CTX, A> rhs) {
        return new ParserImpl<I, CTX, A>(
            Impl.or(Parser.this.acceptsEmpty(), rhs.acceptsEmpty()),
            Impl.union(Parser.this.firstSet(), rhs.firstSet())
        ) {
            @Override
            public Result<I, A> parse(CTX ctx, int pos, SymSet<I> follow) {
                final Input<I> in = ctx.input();
                if (ctx.isEof(pos)) {
                    if (Parser.this.acceptsEmpty().apply()) {
                        return Parser.this.parse(ctx, pos, follow);
                    } else if (rhs.acceptsEmpty().apply()) {
                        return rhs.parse(ctx, pos, follow);
                    } else {
                        return Result.failure(pos);
                    }
                } else {
                    final I i = in.at(pos);
                    if (Parser.this.firstSet().apply().matches(i)) {
                        return Parser.this.parse(ctx, pos, follow);
                    } else if (rhs.firstSet().apply().matches(i)) {
                        return rhs.parse(ctx, pos, follow);
                    } else if (follow.matches(i)) {
                        if (Parser.this.acceptsEmpty().apply()) {
                            return Parser.this.parse(ctx, pos, follow);
                        } else if (rhs.acceptsEmpty().apply()) {
                            return rhs.parse(ctx, pos, follow);
                        }
                    }
                    return Result.failure(pos);
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
            .map((a, lf) -> lf.foldLeft((acc, f) -> f.apply(acc), a));
    }

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> fail() {
        return new ParserImpl<I, CTX, A>(() -> true, SymSet::empty) {
            @Override
            public Result<I, A> parse(CTX ctx, int pos, SymSet<I> follow) {
                return Result.failure(pos);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A, B>
    Parser<I, CTX, B> ap(Parser<I, CTX, F<A, B>> pf, Parser<I, CTX, A> pa) {
        return new ParserImpl<I, CTX, B>(
            Impl.and(pf.acceptsEmpty(), pa.acceptsEmpty()),
            Impl.combine(pf.acceptsEmpty(), pf.firstSet(), pa.firstSet())
        ) {
            @Override
            public Result<I, B> parse(CTX ctx, int pos, SymSet<I> follow) {
                final SymSet<I> followF =
                    Impl.combine(
                        pa.acceptsEmpty().apply(),
                        pa.firstSet().apply(),
                        follow);

                final Result<I, F<A, B>> r = pf.parse(ctx, pos, followF);

                if (r.isSuccess()) {
                    final Result.Success<I, F<A, B>> succ = (Result.Success<I, F<A, B>>) r;
                    final Result<I, A> r2 = pa.parse(ctx, succ.next, follow);
                    return r2.map(succ.value);
                } else {
                    return ((Result.Failure<I, F<A, B>>) r).cast();
                }
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A, B>
    Parser<I, CTX, B> ap(F<A, B> f, Parser<I, CTX, A> pa) {
        return ap(pure(f), pa);
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
        return new ParserImpl<I, CTX, Unit>(TRUE, SymSet::empty) {
            @Override
            public Result<I, Unit> parse(CTX ctx, int pos, SymSet<I> follow) {
                return ctx.isEof(pos) ?
                    Result.success(Unit.UNIT, pos) :
                    Result.failure(pos);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>> Parser<I, CTX, I> satisfy(String name, Predicate<I> pred) {
        return new ParserImpl<I, CTX, I>(FALSE, () -> SymSet.pred(name, pred)) {
            @Override
            public Result<I, I> parse(CTX ctx, int pos, SymSet<I> follow) {
                if (!ctx.isEof(pos)) {
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
        return new ParserImpl<I, CTX, A>(FALSE, () -> SymSet.value(val)) {
            @Override
            public Result<I, A> parse(CTX ctx, int pos, SymSet<I> follow) {
                if (!ctx.isEof(pos)) {
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
        return new ParserImpl<I, CTX, I>(TRUE, SymSet::all) {
            @Override
            public Result<I, I> parse(CTX ctx, int pos, SymSet<I> follow) {
                return ctx.isEof(pos) ?
                    Result.failure(pos) :
                    Result.success(ctx.input().at(pos), pos+1);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, IList<A>> many(Parser<I, CTX, A> p) {
        return new ParserImpl<I, CTX, IList<A>>(TRUE, p.firstSet()) {
            @Override
            public Result<I, IList<A>> parse(CTX ctx, int pos, SymSet<I> follow) {
                IList<A> acc = IList.of();
                final SymSet<I> follow2 = follow.union(p.firstSet().apply());
                while (true) {
                    if (!ctx.isEof(pos)) {
                        final I i = ctx.input().at(pos);
                        if (firstSet().apply().matches(i)) {
                            final Result<I, A> r = p.parse(ctx, pos, follow2);
                            if (r.isSuccess()) {
                                final Result.Success<I, A> succ = (Result.Success<I, A>) r;
                                acc = acc.add(succ.value);
                                pos = succ.next;
                                continue;
                            }
                        }
                    }
                    return Result.success(acc.reverse(), pos);
                }
            }
        };
    }
//            p.and(
//                LazyParser.of(() -> many(p))
//            ).map(
//                a -> l -> (IList<A>)l.add(a)
//            ).or(pure(IList.of()));

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, IList.NonEmpty<A>> many1(Parser<I, CTX, A> p) {
        return p.and(many(p))
            .map(a -> l -> l.add(a));
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, Unit> skipMany(Parser<I, CTX, A> p) {
        return many(p).map(u -> Unit.UNIT);
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
        return ps.foldLeft1(Parser::or);
    }
}

abstract class ParserImpl<I, CTX extends Parser.Context<I>, A> implements Parser<I, CTX, A> {

    private final Lazy<Boolean> acceptsEmpty;

    private final Lazy<SymSet<I>> firstSet;

    ParserImpl(Lazy<Boolean> acceptsEmpty, Lazy<SymSet<I>> firstSet) {
        this.acceptsEmpty = acceptsEmpty;
        this.firstSet = firstSet;
    }
//
//    ParserImpl(boolean acceptsEmpty, F0<SymSet<I>> firstSet) {
//        this.acceptsEmpty = () -> acceptsEmpty;
//        this.firstSet = Lazy.of(firstSet);
//    }
//
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

abstract class Impl {

    static final Lazy<Boolean> TRUE = () -> true;
    static final Lazy<Boolean> FALSE = () -> false;

    static Lazy<Boolean> and(Lazy<Boolean> l, Lazy<Boolean> r) {
        return Lazy.of(() -> l.apply() && r.apply());
    }

    static Lazy<Boolean> or(Lazy<Boolean> l, Lazy<Boolean> r) {
        return Lazy.of(() -> l.apply() || r.apply());
    }

    static <I> Lazy<SymSet<I>> union(Lazy<SymSet<I>> l, Lazy<SymSet<I>> r) {
        return Lazy.of(() -> l.apply().union(r.apply()));
    }

    static <I> Lazy<SymSet<I>> combine(
            Lazy<Boolean> acceptsEmpty,
            Lazy<SymSet<I>> fs1,
            Lazy<SymSet<I>> fs2) {
        return Lazy.of(() -> (acceptsEmpty.apply() ? union(fs1, fs2) : fs1).apply());
    }

    static <I> SymSet<I> combine(
            boolean acceptsEmpty,
            SymSet<I> fs1,
            SymSet<I> fs2) {
        return acceptsEmpty ? fs1.union(fs2) : fs1;
    }
}
