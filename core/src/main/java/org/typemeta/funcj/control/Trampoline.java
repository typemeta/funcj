package org.typemeta.funcj.control;

import org.typemeta.funcj.functions.Functions.*;

/**
 * Trampoline monad.
 * <p>
 * The trampoline monad provides a mechanism for converting recursive computations,
 * that may blow the stack (i.e. cause an {@link StackOverflowError}),
 * into their iterative equivalent.
 * <p>
 * Based on the <a href="http://blog.higher-order.com/assets/trampolines.pdf">Stackless Scala With Free Monads</a>
 * paper by Runar Bjarnason.
 * @param <A>
 */
public interface Trampoline<A> {

    static <A> Done<A> done(A result) {
        return new Done<A>(result);
    }

    static <A> More<A> more(F0<Trampoline<A>> next) {
        return new More<A>(next);
    }

    static <A, B> FlatMap<A, B> flatMapOf(Trampoline<A> sub, F<A, Trampoline<B>> f) {
        return new FlatMap<A, B>(sub) {
            @Override
            Trampoline<B> k(A a) {
                return f.apply(a);
            }
        };
    }

    final class Done<A> implements Trampoline<A> {
        final A result;

        public Done(A result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "Done{" +
                    "result=" + result +
                    '}';
        }
    }

    final class More<A> implements Trampoline<A> {
        final F0<Trampoline<A>> next;

        public More(F0<Trampoline<A>> next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "More{" +
                    "next=" + next +
                    '}';
        }
    }

    abstract class FlatMap<A, B> implements Trampoline<B> {
        final Trampoline<A> sub;

        protected FlatMap(Trampoline<A> sub) {
            this.sub = sub;
        }

        abstract Trampoline<B> k(A a);

        @Override
        public <C> Trampoline<C> flatMap(F<B, Trampoline<C>> f) {
            return flatMapOf(sub, x -> flatMapOf(k(x), f));
        }

        @Override
        public String toString() {
            return "FlatMap{" +
                    "sub=" + sub +
                    '}';
        }
    }

    default <B> Trampoline<B> map(F<A, B> f) {
        return flatMap(x -> more(() -> done(f.apply(x))));
    }

    default <B> Trampoline<B> flatMap(F<A, Trampoline<B>> f) {
        return flatMapOf(this, f);
    }

    default Either<F0<Trampoline<A>>, A> resume() {
        Trampoline<A> next = this;
        while (true) {
            if (next instanceof Done) {
                final Done<A> done = (Done) next;
                return Either.right(done.result);
            } else if (next instanceof More) {
                final More<A> more = (More) next;
                return Either.left(more.next);
            } else {
                final FlatMap<Object, A> fm = (FlatMap) next;
                if (fm.sub instanceof Done) {
                    final Done<A> done = (Done) fm.sub;
                    next = fm.k(done.result);
                } else if (fm.sub instanceof More) {
                    final More<A> more = (More) fm.sub;
                    return Either.left(() -> more.next.apply().flatMap(fm::k));
                } else {
                    final FlatMap<Object, A> fm2 = (FlatMap) fm.sub;
                    next = fm2.sub.flatMap(x -> flatMapOf(fm2.k(x), fm::k));
                }
            }
        }
    }

    default A runT() {
        Trampoline<A> next = this;
        while (true) {
            final Either<F0<Trampoline<A>>, A> result = next.resume();
            if (result instanceof Either.Left) {
                final Either.Left<F0<Trampoline<A>>, A> left = (Either.Left<F0<Trampoline<A>>, A>)result;
                next = left.value.apply();
            } else {
                final Either.Right<F0<Trampoline<A>>, A> right = (Either.Right<F0<Trampoline<A>>, A>)result;
                return right.value;
            }
        }
    }
}
