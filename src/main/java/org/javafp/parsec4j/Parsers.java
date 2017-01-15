package org.javafp.parsec4j;

import org.javafp.data.*;
import org.javafp.data.Functions.*;

import java.util.Optional;
import java.util.function.Predicate;

public class Parsers {

    public static <I, A> Parser<I, A> fail() {
        return new Parser<I, A>() {
            @Override public Result<I, A> parse(Input<I> input, int pos) {
                return Result.failure(pos);
            }

            @Override public boolean accepts(Input<I> input, int pos) {
                return true;
            }
        };
    }

    public static <I, A> Parser<I, A> pure(A a) {
        return new Parser<I, A>() {
            @Override public Result<I, A> parse(Input<I> input, int pos) {
                return Result.success(a, pos);
            }

            @Override public boolean accepts(Input<I> input, int pos) {
                return true;
            }
        };
    }

    public static <I, A> Parser<I, A> pure(F0<A> fa) {
        return pure(fa.apply());
    }

    public static <I, A, B> Parser<I, B> ap(Parser<I, F<A, B>> pf, Parser<I, A> pa) {
        return new Parser<I, B>() {
            @Override public Result<I, B> parse(Input<I> input, int pos) {
                return pf.parse(input, pos)
                    .match(
                        succ -> pa.parse(input, succ.next).map(succ.value),
                        fail -> fail.cast()
                    );
            }

            @Override public boolean accepts(I token) {
                return pf.accepts(token);
            }
        };
    }

    public static <I, A, B> F<Parser<I, A>, Parser<I, B>> liftA(F<A, B> f) {
        return a -> a.map(f);
    }

    public static <I, A, B, C> F<Parser<I, A>, F<Parser<I, B>, Parser<I, C>>> liftA2(F<A, F<B, C>> f) {
        return a -> b -> ap(a.map(f), b);
    }

    public static <I> Parser<I, Unit> eof() {
        return new Parser<I, Unit>() {
            @Override public Result<I, Unit> parse(Input<I> input, int pos) {
                return input.isEof(pos) ?
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

    public static <I> Parser<I, I> satisfy(Predicate<I> pred) {
        return new Parser<I, I>() {
            @Override public Result<I, I> parse(Input<I> input, int pos) {
                if (!input.isEof(pos)) {
                    final I i = input.at(pos);
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

    public static <I> Parser<I, I> any() {
        return (input, pos) ->
            input.isEof(pos) ?
                Result.failure(pos) :
                Result.success(input.at(pos), pos+1);
    }

    public static <I, A> Parser<I, IList<A>> many(Parser<I, A> p) {
        return new Parser<I, IList<A>>() {
            @Override public Result<I, IList<A>> parse(Input<I> input, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<I, A> r = p.parse(input, pos);
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

    public static <I, A> Parser<I, IList.NonEmpty<A>> many1(Parser<I, A> p) {
        return new Parser<I, IList.NonEmpty<A>>() {
            @Override public Result<I, IList.NonEmpty<A>> parse(Input<I> input, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<I, A> r = p.parse(input, pos);
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

    public static <I, A> Parser<I, Optional<A>> optional(Parser<I, A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }
}
