package org.javafp.parsec4j.text;

import org.javafp.data.Functions.*;
import org.javafp.data.*;

import java.util.Optional;
import java.util.function.Predicate;

import static org.javafp.data.Functions.F2.curry;

/**
 * A parser is essentially a function from an input stream to a Result.
 * The Parser type along with the pure and map functions constitute an applicative functor.
 * @param <A> Parse result type
 */
@FunctionalInterface
public interface Parser<A> {

    Result<A> parse(Input input, int pos);

    default Result<A> run(Input input) {
        return this.andL(eof()).parse(input, 0);
    }

    default boolean accepts(Input input, int pos) {
        return !input.isEof(pos) && accepts(input.at(pos));
    }

    default boolean accepts(char token) {
        return true;
    }

    default <B> Parser<B> map(F<A, B> f) {
        return new Chain<A, B>(this) {
            @Override public Result<B> parse(Input input, int pos) {
                return Parser.this.parse(input, pos).map(f);
            }
        };
    }

    default Parser<A> or(Parser<A> rhs) {
        return new Parser<A>() {
            @Override public Result<A> parse(Input input, int pos) {
                if (Parser.this.accepts(input, pos)) {
                    final Result<A> r = Parser.this.parse(input, pos);
                    if (r.isSuccess()) {
                        return r;
                    }
                }

                if (rhs.accepts(input, pos)) {
                    return rhs.parse(input, pos);
                } else {
                    return Result.failure(pos);
                }
            }

            @Override public boolean accepts(Input input, int pos) {
                return Parser.this.accepts(input, pos) || rhs.accepts(input, pos);
            }

            @Override public boolean accepts(char token) {
                return Parser.this.accepts(token) || rhs.accepts(token);
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

    default <B> Parser<B> flatMap(F<A, Parser<B>> f) {
        return new Chain<A, B>(this) {
            @Override public Result<B> parse(Input input, int pos) {
                return Parser.this.parse(input, pos)
                    .match(
                        succ -> f.apply(succ.value).parse(input, pos+1),
                        fail -> fail.cast()
                    );
            }
        };
    }

    default Parser<A> chainl1(Parser<Op2<A>> op) {
        final Parser<IList<Op<A>>> plf =
            many(op.and(this).map((f, y) -> x -> f.apply(x, y)));
        return this.and(plf).map((a, lf) -> lf.foldl((acc, f) -> f.apply(acc), a));
    }

    static <A> Parser<A> fail() {
        return new Parser<A>() {
            @Override public Result<A> parse(Input input, int pos) {
                return Result.failure(pos);
            }

            @Override public boolean accepts(Input input, int pos) {
                return true;
            }
        };
    }

    static <A> Parser<A> pure(A a) {
        return new Parser<A>() {
            @Override public Result<A> parse(Input input, int pos) {
                return Result.success(a, pos);
            }

            @Override public boolean accepts(Input input, int pos) {
                return true;
            }
        };
    }

    static <A> Parser<A> pure(F0<A> fa) {
        return pure(fa.apply());
    }

    static <A, B> Parser<B> ap(Parser<F<A, B>> pf, Parser<A> pa) {
        return new Parser<B>() {
            @Override public Result<B> parse(Input input, int pos) {
                return pf.parse(input, pos)
                    .match(
                        succ -> pa.parse(input, succ.next).map(succ.value),
                        fail -> fail.cast()
                    );
            }

            @Override public boolean accepts(char token) {
                return pf.accepts(token);
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
        return new Parser<Unit>() {
            @Override public Result<Unit> parse(Input input, int pos) {
                return input.isEof(pos) ?
                    Result.success(Unit.UNIT, pos) :
                    Result.failure(pos);
            }

            @Override public boolean accepts(Input input, int pos) {
                return input.isEof(pos);
            }

            @Override public boolean accepts(char token) {
                return false;
            }
        };
    }

    static Parser<Character> satisfy(Predicate<Character> pred) {
        return new Parser<Character>() {
            @Override public Result<Character> parse(Input input, int pos) {
                if (!input.isEof(pos)) {
                    final Character i = input.at(pos);
                    if (pred.test(i)) {
                        return Result.success(i, pos+1);
                    }
                }

                return Result.failure(pos);
            }

            @Override public boolean accepts(char token) {
                return pred.test(token);
            }
        };
    }

    static Parser<Character> any() {
        return (input, pos) ->
            input.isEof(pos) ?
                Result.failure(pos) :
                Result.success(input.at(pos), pos+1);
    }

    static <A> Parser<IList<A>> many(Parser<A> p) {
        return new Parser<IList<A>>() {
            @Override public Result<IList<A>> parse(Input input, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<A> r = p.parse(input, pos);
                    if (r.isSuccess()) {
                        final Result.Success<A> succ = (Result.Success<A>) r;
                        acc = acc.add(succ.value);
                        pos = succ.next;
                    } else {
                        return Result.success(acc.reverse(), pos);
                    }
                }
            }

            @Override public boolean accepts(char token) {
                return p.accepts(token);
            }
        };
    }

    static <A> Parser<IList.NonEmpty<A>> many1(Parser<A> p) {
        return new Parser<IList.NonEmpty<A>>() {
            @Override public Result<IList.NonEmpty<A>> parse(Input input, int pos) {
                IList<A> acc = IList.of();
                while (true) {
                    final Result<A> r = p.parse(input, pos);
                    if (r.isSuccess()) {
                        final Result.Success<A> succ = (Result.Success<A>) r;
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

            @Override public boolean accepts(char token) {
                return p.accepts(token);
            }
        };
    }

    static <A> Parser<Optional<A>> optional(Parser<A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }

    abstract class Chain<S, T> implements Parser<T> {
        private final Parser<S> first;

        protected Chain(Parser<S> first) {
            this.first = first;
        }

        @Override public boolean accepts(Input input, int pos) {
            return first.accepts(input, pos);
        }

        @Override public boolean accepts(char token) {
            return first.accepts(token);
        }
    }
}
