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
public interface P<I, A> {

    abstract class Seq  <I, S, T> implements P<I, T> {
        private final P<I, S> first;

        protected Seq(P<I, S> first) {
            this.first = first;
        }

        public abstract Result<I, T> parse(Input<I> input);

        @Override public boolean accepts(Input<I> input) {
            return first.accepts(input);
        }

        @Override public boolean accepts(I token) {
            return first.accepts(token);
        }
    }

    Result<I, A> parse(Input<I> input);

    default boolean accepts(Input<I> input) {
        return !input.isEof() && accepts(input.head());
    }

    default boolean accepts(I token) {
        return true;
    }

    default Result<I, A> run(Input<I> input) {
        return this.andL(eof()).parse(input);
    }

    default <B> P<I, B> map(F<A, B> f) {
        return new Seq<I, A, B>(this) {
            @Override public Result<I, B> parse(Input<I> input) {
                return P.this.parse(input).map(f);
            }
        };
    }

    default P<I, A> or(P<I, A> rhs) {
        return new P<I, A>() {
            @Override public Result<I, A> parse(Input<I> input) {
                if (P.this.accepts(input)) {
                    final Result<I, A> r = P.this.parse(input);
                    if (r.isSuccess()) {
                        return r;
                    }
                }

                if (rhs.accepts(input)) {
                    return rhs.parse(input);
                } else {
                    return Result.failure(input);
                }
            }

            @Override public boolean accepts(Input<I> input) {
                return P.this.accepts(input) || rhs.accepts(input);
            }

            @Override public boolean accepts(I token) {
                return P.this.accepts(token) || rhs.accepts(token);
            }
        };
    }

    default <B> P<I, Tuple2<A, B>> product(P<I, B> pb) {
        return ap(this.map(curry(Tuple2::new)), pb);
    }

    default <B> P<I, A> andL(P<I, B> pb) {
        return this.and(pb).map(F2.first());
    }

    default <B> P<I, B> andR(P<I, B> pb) {
        return this.and(pb).map(F2.second());
    }

    default <B> ApplyBuilder._2<I, A, B> and(P<I, B> pb) {
        return new ApplyBuilder._2<I, A, B>(this, pb);
    }

    default <B> P<I, B> flatMap(F<A, P<I, B>> f) {
        return new Seq<I, A, B>(this) {
            @Override public Result<I, B> parse(Input<I> input) {
                return P.this.parse(input)
                    .match(
                        succ -> f.apply(succ.value).parse(succ.tail),
                        fail -> fail.cast()
                    );
            }
        };
    }

    default P<I, A> chainl1(P<I, Op2<A>> op) {
        final P<I, IList<Op<A>>> plf = many(op.and(this).map((f, y) -> (A x) -> f.apply(x, y)));
        return this.and(plf).map((a, lf) -> lf.foldl((acc, f) -> f.apply(acc), a));
    }
}
