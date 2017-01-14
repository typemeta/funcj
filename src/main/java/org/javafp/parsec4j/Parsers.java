package org.javafp.parsec4j;

import org.javafp.data.*;
import org.javafp.data.Functions.*;

import java.util.Optional;
import java.util.function.Predicate;

public class Parsers {

    public static <I, A> P<I, A> fail() {
        return new P<I, A>() {
            @Override public Result<I, A> parse(Input<I> input) {
                return Result.failure(input);
            }

            @Override public boolean accepts(Input<I> input) {
                return true;
            }
        };
    }

    public static <I, A> P<I, A> pure(A a) {
        return new P<I, A>() {
            @Override public Result<I, A> parse(Input<I> input) {
                return Result.success(a, input);
            }

            @Override public boolean accepts(Input<I> input) {
                return true;
            }
        };
    }

    public static <I, A> P<I, A> pure(F0<A> fa) {
        return pure(fa.apply());
    }

    public static <I, A, B> P<I, B> ap(P<I, F<A, B>> pf, P<I, A> pa) {
        return new P<I, B>() {
            @Override public Result<I, B> parse(Input<I> input) {
                return pf.parse(input)
                    .match(
                        succ -> pa.parse(succ.tail).map(succ.value),
                        fail -> fail.cast()
                    );
            }

            @Override public boolean accepts(I token) {
                return pf.accepts(token);
            }
        };
    }

    public static <I, A, B> F<P<I, A>, P<I, B>> liftA(F<A, B> f) {
        return a -> a.map(f);
    }

    public static <I, A, B, C> F<P<I, A>, F<P<I, B>, P<I, C>>> liftA2(F<A, F<B, C>> f) {
        return a -> b -> ap(a.map(f), b);
    }

    public static <I> P<I, Unit> eof() {
        return new P<I, Unit>() {
            @Override public Result<I, Unit> parse(Input<I> input) {
                return input.isEof() ?
                    Result.success(Unit.UNIT, input) :
                    Result.failure(input);
            }

            @Override public boolean accepts(Input<I> input) {
                return input.isEof();
            }

            @Override public boolean accepts(I token) {
                return false;
            }
        };
    }

    public static <I> P<I, I> satisfy(Predicate<I> pred) {
        return new P<I, I>() {
            @Override public Result<I, I> parse(Input<I> input) {
                if (!input.isEof()) {
                    final I i = input.head();
                    if (pred.test(i)) {
                        return Result.success(i, input.tail());
                    }
                }

                return Result.failure(input);
            }

            @Override public boolean accepts(I token) {
                return pred.test(token);
            }
        };
    }

    public static <I> P<I, I> any() {
        return input ->
            input.isEof() ?
                Result.failure(input) :
                Result.success(input.head(), input.tail());
    }

    public static <I, A> P<I, IList<A>> many(P<I, A> p) {
        return new P<I, IList<A>>() {
            @Override public Result<I, IList<A>> parse(Input<I> input) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<I, A> r = p.parse(input);
                    if (r.isSuccess()) {
                        final Result.Success<I, A> succ = (Result.Success<I, A>) r;
                        acc = acc.add(succ.value);
                        input = succ.tail;
                    } else {
                        return Result.success(acc.reverse(), input);
                    }
                }
            }

            @Override public boolean accepts(I token) {
                return p.accepts(token);
            }
        };
    }

    public static <I, A> P<I, IList.NonEmpty<A>> many1(P<I, A> p) {
        return new P<I, IList.NonEmpty<A>>() {
            @Override public Result<I, IList.NonEmpty<A>> parse(Input<I> input) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<I, A> r = p.parse(input);
                    if (r.isSuccess()) {
                        final Result.Success<I, A> succ = (Result.Success<I, A>) r;
                        acc = acc.add(succ.value);
                        input = succ.tail;
                    } else {
                        final Input<I> input2 = input;
                        return acc.match(
                            nel -> Result.success(nel.reverse(), input2),
                            empty -> Result.failure(input2)
                        );
                    }
                }
            }

            @Override public boolean accepts(I token) {
                return p.accepts(token);
            }
        };
    }

    public static <I, A> P<I, Optional<A>> optional(P<I, A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }
}
