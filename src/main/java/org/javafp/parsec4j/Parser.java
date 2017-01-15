package org.javafp.parsec4j;

import org.javafp.data.*;
import org.javafp.data.Functions.*;

import java.util.Optional;
import java.util.function.Predicate;

import static org.javafp.data.Functions.F2.curry;

/**
 * A parser is essentially a function from an input stream to a Result.
 * The Parser type along with the pure and map functions constitute an applicative functor.
 * @param <I> Input stream symbol type.
 * @param <A> Parse result type
 */
@FunctionalInterface
public interface Parser<I, CTX extends Parser.Context<I>, A> {

    interface Context<I> {
        Input<I> input();
    }

    class CtxImpl<I> implements Context<I> {
        private final Input<I> input;

        public CtxImpl(Input<I> input) {
            this.input = input;
        }

        @Override
        public Input<I> input() {
            return input;
        }
    }

    Result<I, A> parse(CTX ctx, int pos);

    default Result<I, A> run(CTX ctx) {
        return this.andL(eof()).parse(ctx, 0);
    }

    default boolean accepts(Input<I> input, int pos) {
        return !input.isEof(pos) && accepts(input.at(pos));
    }

    default boolean accepts(I token) {
        return true;
    }

    default <B> Parser<I, CTX, B> map(F<A, B> f) {
        return new Chain<I, CTX, A, B>(this) {
            @Override public Result<I, B> parse(CTX ctx, int pos) {
                return Parser.this.parse(ctx, pos).map(f);
            }
        };
    }

    default Parser<I, CTX, A> or(Parser<I, CTX, A> rhs) {
        return new Parser<I, CTX, A>() {
            @Override public Result<I, A> parse(CTX ctx, int pos) {
                if (Parser.this.accepts(ctx.input(), pos)) {
                    final Result<I, A> r = Parser.this.parse(ctx, pos);
                    if (r.isSuccess()) {
                        return r;
                    }
                }

                if (rhs.accepts(ctx.input(), pos)) {
                    return rhs.parse(ctx, pos);
                } else {
                    return Result.failure(pos);
                }
            }

            @Override public boolean accepts(Input<I> input, int pos) {
                return Parser.this.accepts(input, pos) || rhs.accepts(input, pos);
            }

            @Override public boolean accepts(I token) {
                return Parser.this.accepts(token) || rhs.accepts(token);
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

    default <B> Parser<I, CTX, B> flatMap(F<A, Parser<I, CTX, B>> f) {
        return new Chain<I, CTX, A, B>(this) {
            @Override public Result<I, B> parse(CTX ctx, int pos) {
                return Parser.this.parse(ctx, pos)
                    .match(
                        succ -> f.apply(succ.value).parse(ctx, pos+1),
                        fail -> fail.cast()
                    );
            }
        };
    }

    default Parser<I, CTX, A> chainl1(Parser<I, CTX, Op2<A>> op) {
        final Parser<I, CTX, IList<Op<A>>> plf =
            many(op.and(this).map((f, y) -> x -> f.apply(x, y)));
        return this.and(plf).map((a, lf) -> lf.foldl((acc, f) -> f.apply(acc), a));
    }

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> fail() {
        return new Parser<I, CTX, A>() {
            @Override public Result<I, A> parse(CTX ctx, int pos) {
                return Result.failure(pos);
            }

            @Override public boolean accepts(Input<I> input, int pos) {
                return true;
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A> Parser<I, CTX, A> pure(A a) {
        return new Parser<I, CTX, A>() {
            @Override public Result<I, A> parse(CTX ctx, int pos) {
                return Result.success(a, pos);
            }

            @Override public boolean accepts(Input<I> input, int pos) {
                return true;
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, A> pure(F0<A> fa) {
        return pure(fa.apply());
    }

    static <I, CTX extends Parser.Context<I>, A, B>
    Parser<I, CTX, B> ap(Parser<I, CTX, F<A, B>> pf, Parser<I, CTX, A> pa) {
        return new Parser<I, CTX, B>() {
            @Override public Result<I, B> parse(CTX ctx, int pos) {
                return pf.parse(ctx, pos)
                    .match(
                        succ -> pa.parse(ctx, succ.next).map(succ.value),
                        fail -> fail.cast()
                    );
            }

            @Override public boolean accepts(I token) {
                return pf.accepts(token);
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
        return new Parser<I, CTX, Unit>() {
            @Override public Result<I, Unit> parse(CTX ctx, int pos) {
                return ctx.input().isEof(pos) ?
                    Result.success(Unit.UNIT, pos) :
                    Result.failure(pos);
            }

            @Override public boolean accepts(Input<I> input, int pos) {
                return input.isEof(pos);
            }

            @Override public boolean accepts(I token) {
                return false;
            }
        };
    }

    static <I, CTX extends Parser.Context<I>> Parser<I, CTX, I> satisfy(Predicate<I> pred) {
        return new Parser<I, CTX, I>() {
            @Override public Result<I, I> parse(CTX ctx, int pos) {
                if (!ctx.input().isEof(pos)) {
                    final I i = ctx.input().at(pos);
                    if (pred.test(i)) {
                        return Result.success(i, pos+1);
                    }
                }

                return Result.failure(pos);
            }

            @Override public boolean accepts(I token) {
                return pred.test(token);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>> Parser<I, CTX, I> any() {
        return (ctx, pos) ->
            ctx.input().isEof(pos) ?
                Result.failure(pos) :
                Result.success(ctx.input().at(pos), pos+1);
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, IList<A>> many(Parser<I, CTX, A> p) {
        return new Parser<I, CTX, IList<A>>() {
            @Override public Result<I, IList<A>> parse(CTX ctx, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<I, A> r = p.parse(ctx, pos);
                    if (r.isSuccess()) {
                        final Result.Success<I, A> succ = (Result.Success<I, A>) r;
                        acc = acc.add(succ.value);
                        pos = succ.next;
                    } else {
                        return Result.success(acc.reverse(), pos);
                    }
                }
            }

            @Override public boolean accepts(I token) {
                return p.accepts(token);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, IList.NonEmpty<A>> many1(Parser<I, CTX, A> p) {
        return new Parser<I, CTX, IList.NonEmpty<A>>() {
            @Override public Result<I, IList.NonEmpty<A>> parse(CTX ctx, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<I, A> r = p.parse(ctx, pos);
                    if (r.isSuccess()) {
                        final Result.Success<I, A> succ = (Result.Success<I, A>) r;
                        acc = acc.add(succ.value);
                        pos = succ.next;
                    } else {
                        final int pos2 = pos;
                        return acc.match(
                            nel -> Result.success(nel.reverse(), pos2),
                            empty -> Result.failure(pos2)
                        );
                    }
                }
            }

            @Override public boolean accepts(I token) {
                return p.accepts(token);
            }
        };
    }

    static <I, CTX extends Parser.Context<I>, A>
    Parser<I, CTX, Optional<A>> optional(Parser<I, CTX, A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }

    abstract class Chain<I, CTX extends Context<I>, S, T> implements Parser<I, CTX, T> {
        private final Parser<I, CTX, S> first;

        protected Chain(Parser<I, CTX, S> first) {
            this.first = first;
        }

        @Override public boolean accepts(Input<I> input, int pos) {
            return first.accepts(input, pos);
        }

        @Override public boolean accepts(I token) {
            return first.accepts(token);
        }
    }
}
