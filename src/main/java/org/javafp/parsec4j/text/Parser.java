package org.javafp.parsec4j.text;

import org.javafp.util.*;
import org.javafp.util.Functions.*;
import org.javafp.data.*;

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

    static Parser<Chr> satisfy(Predicate<Chr> pred) {
        return (input, pos) -> {
            if (!input.isEof(pos)) {
                final Chr c = Chr.valueOf(input.at(pos));
                if (pred.test(c)) {
                    return Result.success(c, pos+1);
                }
            }

            return Result.failure(pos);
        };
    }

    static Parser<Character> value(char chr) {
        return value(chr, chr);
    }

    static <A> Parser<A> value(char chr, A res) {
        return (input, pos) -> {
            if (!input.isEof(pos)) {
                final char c = input.at(pos);
                if (c == chr) {
                    return Result.success(res, pos+1);
                }
            }

            return Result.failure(pos);
        };
    }

    static Parser<Chr> any() {
        return (input, pos) ->
            input.isEof(pos) ?
                Result.failure(pos) :
                Result.success(Chr.valueOf(input.at(pos)), pos+1);
    }

    static <A> Parser<IList<A>> many(Parser<A> p) {
        return Impl.many(p).map(IList::reverse);
    }

    static <A> Parser<IList.NonEmpty<A>> many1(Parser<A> p) {
        return p.and(Impl.many(p))
            .map(a -> l -> l.add(a))
            .map(IList.NonEmpty::reverse);
    }

    static <A, SEP> Parser<IList<A>> sepBy(Parser<A> p, Parser<SEP> sep) {
        return sepBy1(p, sep).or(pure(IList.nil()));
    }

    static <A, SEP> Parser<IList<A>> sepBy1(Parser<A> p, Parser<SEP> sep) {
        return many(p.andL(sep))
            .or(p.map(IList::of));
    }

    static <A> Parser<Optional<A>> optional(Parser<A> p) {
        return p.map(Optional::of).or(pure(Optional.empty()));
    }

    static <A, OPEN, CLOSE> Parser<A> between(
            Parser<OPEN> open,
            Parser<CLOSE> close,
            Parser<A> p) {
        return open.andR(p).andL(close);
    }


    static <A> Parser<A> choice(Parser<A>... ps) {
        return choice(IList.ofArray(ps));
    }

    static <A> Parser<A> choice(IList<Parser<A>> ps) {
        if (ps.tail().isEmpty()) {
            return ps.head();
        } else {
            return ps.head().or(choice(ps.tail()));
        }
    }
}

abstract class Impl {
    static <A> Parser<IList<A>> many(Parser<A> p) {
        return (in, pos) -> {
            IList<A> acc = IList.of();
            while (true) {
                final Result<A> r = p.parse(in, pos);
                if (r.isSuccess()) {
                    final Result.Success<A> succ = (Result.Success<A>) r;
                    acc = acc.add(succ.value);
                    pos = succ.next;
                } else {
                    return Result.success(acc, pos);
                }
            }
        };
    }
}