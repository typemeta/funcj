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
public interface Parser<I, A> {

    Lazy<Boolean> acceptsEmpty();

    Lazy<SymSet<I>> firstSet();

    Result<I, A> parse(Input<I> in, SymSet<I> follow);

    default Result<I, A> run(Input<I> in) {
        return this.andL(eof()).parse(in, SymSet.empty());
    }

    static <I, A> Parser<I, A> pure(A a) {
        return new ParserImpl<I, A>(TRUE, SymSet::empty) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                return Result.success(a, in);
            }
        };
    }

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

    default Parser<I, A> or(Parser<I, A> rhs) {
        return new ParserImpl<I, A>(
            Impl.or(Parser.this.acceptsEmpty(), rhs.acceptsEmpty()),
            Impl.union(Parser.this.firstSet(), rhs.firstSet())
        ) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                if (in.isEof()) {
                    if (Parser.this.acceptsEmpty().apply()) {
                        return Parser.this.parse(in, follow);
                    } else if (rhs.acceptsEmpty().apply()) {
                        return rhs.parse(in, follow);
                    } else {
                        return Result.failure(in);
                    }
                } else {
                    final I i = in.get();
                    if (Parser.this.firstSet().apply().matches(i)) {
                        return Parser.this.parse(in, follow);
                    } else if (rhs.firstSet().apply().matches(i)) {
                        return rhs.parse(in, follow);
                    } else if (follow.matches(i)) {
                        if (Parser.this.acceptsEmpty().apply()) {
                            return Parser.this.parse(in, follow);
                        } else if (rhs.acceptsEmpty().apply()) {
                            return rhs.parse(in, follow);
                        }
                    }
                    return Result.failure(in);
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
                return Result.failure(in);
            }
        };
    }

    static <I, A, B>
    Parser<I, B> ap(Parser<I, F<A, B>> pf, Parser<I, A> pa) {
        return new ParserImpl<I, B>(
            Impl.and(pf.acceptsEmpty(), pa.acceptsEmpty()),
            Impl.combine(pf.acceptsEmpty(), pf.firstSet(), pa.firstSet())
        ) {
            @Override
            public Result<I, B> parse(Input<I> in, SymSet<I> follow) {
                final SymSet<I> followF =
                    Impl.combine(
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

    static <I, A, B>
    Parser<I, B> ap(F<A, B> f, Parser<I, A> pa) {
        return ap(pure(f), pa);
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
        return new ParserImpl<I, Unit>(TRUE, SymSet::empty) {
            @Override
            public Result<I, Unit> parse(Input<I> in, SymSet<I> follow) {
                return in.isEof() ?
                    Result.success(Unit.UNIT, in) :
                    Result.failure(in);
            }
        };
    }

    static <I> Parser<I, I> satisfy(String name, Predicate<I> pred) {
        return new ParserImpl<I, I>(FALSE, () -> SymSet.pred(name, pred)) {
            @Override
            public Result<I, I> parse(Input<I> in, SymSet<I> follow) {
                final I i = in.get();
                return Result.success(i, in.next());
            }
        };
    }

    static <I> Parser<I, I> value(I val) {
        return value(val, val);
    }

    static <I, A> Parser<I, A> value(I val, A res) {
        return new ParserImpl<I, A>(FALSE, () -> SymSet.value(val)) {
            @Override
            public Result<I, A> parse(Input<I> in, SymSet<I> follow) {
                return Result.success(res, in.next());

            }
        };
    }

    static <I> Parser<I, I> any() {
        return new ParserImpl<I, I>(TRUE, SymSet::all) {
            @Override
            public Result<I, I> parse(Input<I> in, SymSet<I> follow) {
                return in.isEof() ?
                    Result.failure(in) :
                    Result.success(in.get(), in.next());
            }
        };
    }

    static <I, A>
    Parser<I, IList<A>> many(Parser<I, A> p) {
        return new ParserImpl<I, IList<A>>(TRUE, p.firstSet()) {
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
