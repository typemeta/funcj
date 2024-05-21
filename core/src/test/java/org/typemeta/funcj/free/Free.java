package org.typemeta.funcj.free;

import org.typemeta.funcj.control.*;
import org.typemeta.funcj.functions.Functions;

import static org.typemeta.funcj.free.Free.*;

public interface Free<A> {
    static <A> Pure<A> pure(A a) {
        return new Pure<A>(a);
    }

    static <A> Suspend<A> suspend(KVStore.DSL<A> a) {
        return new Suspend<A>(a);
    }

    static <B, C> FlatMapped<B, C> flatMapped(Free<C> c, Functions.F<C, Free<B>> f) {
        return new FlatMapped<B, C>(c, f);
    }

    static <A> Free<A> liftF(KVStore.DSL<A> value) {
        return suspend(value);
    }

    class Pure<A> implements Free<A> {
        public final A a;

        public Pure(A a) {
            this.a = a;
        }
    }

    class Suspend<A> implements Free<A> {
        public final KVStore.DSL<A> a;

        public Suspend(KVStore.DSL<A> a) {
            this.a = a;
        }
    }

    class FlatMapped<B, C> implements Free<B> {
        public final Free<C> c;
        public final Functions.F<C, Free<B>> g;

        public FlatMapped(Free<C> c, Functions.F<C, Free<B>> g) {
            this.c = c;
            this.g = g;
        }

        @Override
        public Free<B> step() {
            if (c instanceof FlatMapped) {
                final FlatMapped<C, Object> fm = (FlatMapped<C, Object>) c;
                return fm.c.flatMap(cc -> fm.g.apply(cc).flatMap(g)).step();
            } else if (c instanceof Pure) {
                final Pure<C> pure = (Pure<C>) c;
                return g.apply(pure.a).step();
            } else {
                return this;
            }
        }
    }

    default <B> Free<B> flatMap(Functions.F<A, Free<B>> f) {
        return flatMapped(this, f);
    }

    default <B> Free<B> flatMap(Free<B> b) {
        return flatMapped(this, x -> b);
    }

    default Free<A> step() {
        return this;
    }

    interface NT<A, R> extends Functions.F<Free<A>, R> {
        default R apply(Free<A> fr) {
            Free<A> step = fr.step();
            if (step instanceof Pure) {
                return transform((Pure<A>) step);
            } else if (step instanceof Suspend) {
                return transform((Suspend<A>) step);
            } else {
                return transform((FlatMapped<A, Object>) step);
            }
        }

        R transform(Pure<A> pure);

        R transform(Suspend<A> suspend);

        <V> R transform(FlatMapped<A, V> flatMapped);
    }

    default <T> Id<A> foldMapId(Functions.F<KVStore.DSL<T>, Id<T>> f) {
        return Id.tailRecM(this, new NT<A, Id<Either<Free<A>, A>>>() {
            @Override
            public Id<Either<Free<A>, A>> transform(Pure<A> pure) {
                return Id.pure(Either.right(pure.a));
            }

            @Override
            public Id<Either<Free<A>, A>> transform(Suspend<A> suspend) {
                final Functions.F<KVStore.DSL<A>, Id<A>> fA = ((Functions.F<KVStore.DSL<A>, Id<A>>) (Functions.F) f);
                return fA.apply(suspend.a).map(Either::right);
            }

            @Override
            public <V> Id<Either<Free<A>, A>> transform(FlatMapped<A, V> flatMapped) {
                return flatMapped.c.foldMapId(f)
                        .map(flatMapped.g)
                        .map(Either::left);
            }
        });
    }

    default <T, U> State<IMapS<U>, A> foldMapMS(Functions.F<KVStore.DSL<T>, State<IMapS<U>, A>> f) {
        return State.tailRecM(this, new NT<A, State<IMapS<U>, Either<Free<A>, A>>>() {
            @Override
            public State<IMapS<U>, Either<Free<A>, A>> transform(Pure<A> pure) {
                return State.pure(Either.right(pure.a));
            }

            @Override
            public State<IMapS<U>, Either<Free<A>, A>> transform(Suspend<A> suspend) {
                final Functions.F<KVStore.DSL<A>, State<IMapS<U>, A>> fA = ((Functions.F<KVStore.DSL<A>, State<IMapS<U>, A>>) (Functions.F) f);
                return fA.apply(suspend.a).map(Either::right);
            }

            @Override
            public <V> State<IMapS<U>, Either<Free<A>, A>> transform(FlatMapped<A, V> flatMapped) {
                final Functions.F<KVStore.DSL<T>, State<IMapS<U>, V>> fV = (Functions.F<KVStore.DSL<T>, State<IMapS<U>, V>>) (Functions.F) f;
                return flatMapped.c.foldMapMS(fV)
                        .map(flatMapped.g)
                        .map(Either::left);
            }
        });
    }
}
