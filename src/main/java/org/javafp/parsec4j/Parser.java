package org.javafp.parsec4j;

import org.javafp.data.*;
import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Parsers.*;
import static org.javafp.data.Functions.F2.curry;

/**
 * A parser is essentially a function from an input stream to a Result.
 * The Parser type along with the pure and map functions constitute an applicative functor.
 * @param <I> Input stream symbol type.
 * @param <A> Parse result type
 */
@FunctionalInterface
public interface Parser<I, A> {

    abstract class Chain<I, S, T> implements Parser<I, T> {
        private final Parser<I, S> first;

        protected Chain(Parser<I, S> first) {
            this.first = first;
        }

        @Override public boolean accepts(Input<I> input, int pos) {
            return first.accepts(input, pos);
        }

        @Override public boolean accepts(I token) {
            return first.accepts(token);
        }
    }

    Result<I, A> parse(Input<I> input, int pos);

    default boolean accepts(Input<I> input, int pos) {
        return !input.isEof(pos) && accepts(input.at(pos));
    }

    default boolean accepts(I token) {
        return true;
    }

    default Result<I, A> run(Input<I> input) {
        return this.andL(eof()).parse(input, 0);
    }

    default <B> Parser<I, B> map(F<A, B> f) {
        return new Chain<I, A, B>(this) {
            @Override public Result<I, B> parse(Input<I> input, int pos) {
                return Parser.this.parse(input, pos).map(f);
            }
        };
    }

    default Parser<I, A> or(Parser<I, A> rhs) {
        return new Parser<I, A>() {
            @Override public Result<I, A> parse(Input<I> input, int pos) {
                if (Parser.this.accepts(input, pos)) {
                    final Result<I, A> r = Parser.this.parse(input, pos);
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

            @Override public boolean accepts(Input<I> input, int pos) {
                return Parser.this.accepts(input, pos) || rhs.accepts(input, pos);
            }

            @Override public boolean accepts(I token) {
                return Parser.this.accepts(token) || rhs.accepts(token);
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

    default <B> Parser<I, B> flatMap(F<A, Parser<I, B>> f) {
        return new Chain<I, A, B>(this) {
            @Override public Result<I, B> parse(Input<I> input, int pos) {
                return Parser.this.parse(input, pos)
                    .match(
                        succ -> f.apply(succ.value).parse(input, pos+1),
                        fail -> fail.cast()
                    );
            }
        };
    }

    default Parser<I, A> chainl1(Parser<I, Op2<A>> op) {
        final Parser<I, IList<Op<A>>> plf = many(op.and(this).map((f, y) -> (A x) -> f.apply(x, y)));
        return this.and(plf).map((a, lf) -> lf.foldl((acc, f) -> f.apply(acc), a));
    }
}
