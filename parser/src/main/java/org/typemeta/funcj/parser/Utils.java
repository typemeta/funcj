package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.tuples.Tuple2;

abstract class Utils {

    static final Lazy<Boolean> LTRUE = () -> true;
    static final Lazy<Boolean> LFALSE = () -> false;

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

    static <I, A> Result<I, A> failure(Parser<I, ?> parser, Input<I> in) {
        return Result.failure(in, parser.firstSet().apply());
    }

    static <I, A> Result<I, A> failure(String msg, Input<I> in) {
        return Result.failureMessage(in, msg);
    }

    static <I, A> Result<I, A> failureEof(Parser<I, ?> parser, Input<I> in) {
        return Result.failureEof(in, parser.firstSet().apply());
    }

    static <A> A reduce(A a, IList<Tuple2<Functions.Op2<A>, A>> lopA) {
        return lopA.match(
                nel -> nel.head()._1.apply(a, reduce(nel.head()._2, nel.tail())),
                nil -> a
        );
    }
}
