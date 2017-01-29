package org.javafp.parsec4j.text;

import org.javafp.util.Functions.*;
import org.javafp.data.*;
import org.javafp.util.Unit;

import java.util.Optional;
import java.util.function.Predicate;

import static org.javafp.util.Functions.F2.curry;

/**
 * A parser is essentially a function from an input stream to a Result.
 * @param <A> Parse result type
 */
@FunctionalInterface
public interface Parser<A> {

    Result<A> parse(Input input, int pos);

    default Result<A> run(Input input) {
        return this.andL(eof()).parse(input, 0);
    }

    default <B> Parser<B> map(F<A, B> f) {
        return (input, pos) -> Parser.this.parse(input, pos).map(f);
    }

    default <B> Parser<B> flatMap(F<A, Parser<B>> f) {
        return (input, pos) -> Parser.this.parse(input, pos)
            .match(
                succ -> f.apply(succ.value).parse(input, pos+1),
                fail -> fail.cast()
            );
    }

    default Parser<A> or(Parser<A> rhs) {
        return (input, pos) -> {
            final Result<A> r = Parser.this.parse(input, pos);
            if (r.isSuccess()) {
                return r;
            } else {
                return rhs.parse(input, pos);
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
            many(op.and(this).map((f, y) -> x -> f.apply(x, y)));
        return this.and(plf)
            .map((a, lf) -> lf.foldl((acc, f) -> f.apply(acc), a));
    }

    static <A> Parser<A> fail() {
        return (input, pos) -> Result.failure(pos);
    }

    static <A> Parser<A> pure(A a) {
        return (input, pos) -> Result.success(a, pos);
    }

    static <A> Parser<A> pure(F0<A> fa) {
        return pure(fa.apply());
    }

    static <A, B> Parser<B> ap(Parser<F<A, B>> pf, Parser<A> pa) {
        return (input, pos) -> pf.parse(input, pos)
            .match(
                succ -> pa.parse(input, succ.next).map(succ.value),
                fail -> fail.cast()
            );
    }

    static <A, B> Parser<B> ap(F<A, B> f, Parser<A> pa) {
        return (input, pos) -> pa.parse(input, pos).map(f);
    }

    static <A, B> F<Parser<A>, Parser<B>> liftA(F<A, B> f) {
        return a -> a.map(f);
    }

    static <A, B, C> F<Parser<A>, F<Parser<B>, Parser<C>>> liftA2(F<A, F<B, C>> f) {
        return a -> b -> ap(a.map(f), b);
    }

    static Parser<Unit> eof() {
        return (input, pos) -> input.isEof(pos) ?
            Result.success(Unit.UNIT, pos) :
            Result.failure(pos);
    }

    static Parser<Character> satisfy(Predicate<Character> pred) {
        return (input, pos) -> {
            if (!input.isEof(pos)) {
                final Character i = input.at(pos);
                if (pred.test(i)) {
                    return Result.success(i, pos+1);
                }
            }

            return Result.failure(pos);
        };
    }

    static Parser<Character> any() {
        return (input, pos) ->
            input.isEof(pos) ?
                Result.failure(pos) :
                Result.success(input.at(pos), pos+1);
    }

    static <A> Parser<IList<A>> many(Parser<A> p) {
        return (input, pos) -> {
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
        };
    }

    static <A> Parser<IList.NonEmpty<A>> many1(Parser<A> p) {
        return (input, pos) -> {
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
        };
    }

    static <A> Parser<Optional<A>> optional(Parser<A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }
}
