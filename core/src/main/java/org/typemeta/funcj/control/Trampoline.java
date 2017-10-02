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
 * @param <T>           the value type yielded by the {@code Trampoline}
 */
public interface Trampoline<T> {

    /**
     * Construct a {@link Pure} value.
     * @param result        the value to be wrapped into the {@code Done} value
     * @param <A>           the value type
     * @return              the new {@code Done} value
     */
    static <A> Pure<A> done(A result) {
        return new Pure<A>(result);
    }

    /**
     * Construct a {@link Suspend} value.
     * @param next          a continuation that yields the next {@link Trampoline} in the computation.
     * @param <A>           the value type
     * @return              the new {@code More} value
     */
    static <A> Suspend<A> defer(F0<Trampoline<A>> next) {
        return new Suspend<A>(next);
    }

    /**
     * Construct a {@link FlatMapped} value.
     * @param sub           the {@link Trampoline} value to be bound into the {@code FlatMap}
     * @param f             the function that, when invoked, yields the next {@code Trampoline} value
     * @param <A>           the value type of the {@code Trampoline} value
     * @param <B>           the value type of the {@code Trampoline} value return by {@code f}
     * @return              the new {@code FlatMap} value
     */
    static <A, B> FlatMapped<A, B> flatMapOf(Trampoline<A> sub, F<A, Trampoline<B>> f) {
        return new FlatMapped<A, B>(sub) {
            @Override
            Trampoline<B> k(A a) {
                return f.apply(a);
            }
        };
    }

    /**
     * Represents the end of a computation, in the form of the resultant value.
     * @param <T>           the value type
     */
    final class Pure<T> implements Trampoline<T> {
        /**
         * The result of the computation.
         */
        final T result;

        public Pure(T result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "Done{result=" + result + '}';
        }
    }

    /**
     * Represents a continuation, indicating that further steps are required in the computation.
     * @param <T>           the value type
     */
    final class Suspend<T> implements Trampoline<T> {
        /**
         * The continuation, that yields the next {@code Trampoline} in the computation.
         */
        final F0<Trampoline<T>> next;

        public Suspend(F0<Trampoline<T>> next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "More{next=" + next + '}';
        }
    }

    /**
     * Represents a deferred flatMap operation, namely a value and a function to be applied to the value.
     * @param <S>           the flatMap value input type
     * @param <T>           the flatMap value output type
     */
    abstract class FlatMapped<S, T> implements Trampoline<T> {
        /**
         * The {@link Trampoline} value to which the function {@link FlatMapped#k(Object)} will get applied.
         */
        final Trampoline<S> sub;

        protected FlatMapped(Trampoline<S> sub) {
            this.sub = sub;
        }

        /**
         * The function to be applied to the flatMap value.
         * @param s             the argument to the flatMap operation
         * @return              the result of the flatMap operation
         */
        abstract Trampoline<T> k(S s);

        @Override
        public <U> Trampoline<U> flatMap(F<T, Trampoline<U>> f) {
            return flatMapOf(sub, x -> flatMapOf(k(x), f));
        }

        @Override
        public String toString() {
            return "FlatMap{sub=" + sub + '}';
        }
    }

    /**
     * Map a function over this value.
     * @param f             the function to be mapped
     * @param <U>           the return type of the function
     * @return              a {@code Trampoline} that wraps the result of applying the function
     */
    default <U> Trampoline<U> map(F<T, U> f) {
        return flatMap(x -> defer(() -> done(f.apply(x))));
    }

    /**
     * FlatMap a function over this value.
     * @param f             the function to be flatMapped
     * @param <U>           the value type of the {@code Trampoline} returned by the function
     * @return              a {@code Trampoline} representing the deferred result of a flatMap
     */
    default <U> Trampoline<U> flatMap(F<T, Trampoline<U>> f) {
        return flatMapOf(this, f);
    }

    /**
     * Resume the computation until it yields the next value.
     * @return              either an {@link Either.Left} of a further computation,
     *                      or an {@link Either.Right} of a final result.
     */
    default Either<F0<Trampoline<T>>, T> resume() {
        // The following loop is effectively an iterative rendition of a tail-recursive call.
        Trampoline<T> next = this;
        while (true) {
            if (next instanceof Trampoline.Pure) {
                final Pure<T> pure = (Pure) next;
                return Either.right(pure.result);
            } else if (next instanceof Trampoline.Suspend) {
                final Suspend<T> suspend = (Suspend) next;
                return Either.left(suspend.next);
            } else {
                final FlatMapped<Object, T> fm = (FlatMapped) next;
                if (fm.sub instanceof Trampoline.Pure) {
                    final Pure<T> pure = (Pure) fm.sub;
                    next = fm.k(pure.result);
                } else if (fm.sub instanceof Trampoline.Suspend) {
                    final Suspend<T> suspend = (Suspend) fm.sub;
                    return Either.left(() -> suspend.next.apply().flatMap(fm::k));
                } else {
                    final FlatMapped<Object, T> fm2 = (FlatMapped) fm.sub;
                    next = fm2.sub.flatMap(x -> flatMapOf(fm2.k(x), fm::k));
                }
            }
        }
    }

    /**
     * Run the computation until it yields a final result.
     * @return              the final result of the computation
     */
    default T runT() {
        // The following loop is effectively an iterative rendition of a tail-recursive call.
        Trampoline<T> next = this;
        while (true) {
            final Either<F0<Trampoline<T>>, T> result = next.resume();
            if (result instanceof Either.Left) {
                final Either.Left<F0<Trampoline<T>>, T> left = (Either.Left<F0<Trampoline<T>>, T>)result;
                next = left.value.apply();
            } else {
                final Either.Right<F0<Trampoline<T>>, T> right = (Either.Right<F0<Trampoline<T>>, T>)result;
                return right.value;
            }
        }
    }
}
