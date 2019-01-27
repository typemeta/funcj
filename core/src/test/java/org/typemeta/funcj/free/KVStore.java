package org.typemeta.funcj.free;

import org.typemeta.funcj.control.*;
import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions.F;

import static org.typemeta.funcj.free.KVStore.DSL.Free.*;

/**
 * Free monad example.
 */
@SuppressWarnings("unchecked")
public class KVStore {
    interface DSL<A> {
        static <T, NEXT> Put<T, NEXT> put(String key, T value) {
            return new Put<T, NEXT>(key, value);
        }

        static <T> Get<T> get(String key) {
            return new Get<T>(key);
        }

        static <T> Delete<T> delete(String key) {
            return new Delete<T>(key);
        }

        class Put<T, NEXT> implements DSL<NEXT> {
            public final String key;
            public final T value;

            public Put(String key, T value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public String toString() {
                return "Put{key='" + key + '\'' + ", value=" + value + '}';
            }
        }

        class Get<T> implements DSL<Option<T>> {
            public final String key;

            public Get(String key) {
                this.key = key;
            }

            @Override
            public String toString() {
                return "Get{key='" + key + '\'' + '}';
            }
        }

        class Delete<T> implements DSL<Unit> {
            public final String key;

            public Delete(String key) {
                this.key = key;
            }

            @Override
            public String toString() {
                return "Delete{key='" + key + '\'' + '}';
            }
        }

        interface Free<A> {
            static <A> Pure<A> pure(A a) {
                return new Pure<A>(a);
            }

            static <A> Suspend<A> suspend(DSL<A> a) {
                return new Suspend<A>(a);
            }

            static <B, C> FlatMapped<B, C> flatMapped(Free<C> c, F<C, Free<B>> f) {
                return new FlatMapped<B, C>(c, f);
            }

            static <A> Free<A> liftF(DSL<A> value) {
                return suspend(value);
            }

            class Pure<A> implements Free<A> {
                public final A a;

                public Pure(A a) {
                    this.a = a;
                }
            }

            class Suspend<A> implements Free<A> {
                public final DSL<A> a;

                public Suspend(DSL<A> a) {
                    this.a = a;
                }
            }

            class FlatMapped<B, C> implements Free<B> {
                public final Free<C> c;
                public final F<C, Free<B>> g;

                public FlatMapped(Free<C> c, F<C, Free<B>> g) {
                    this.c = c;
                    this.g = g;
                }

                @Override
                public Free<B> step() {
                    if (c instanceof FlatMapped) {
                        final FlatMapped<C, Object> fm = (FlatMapped<C, Object>)c;
                        return fm.c.flatMap(cc -> fm.g.apply(cc).flatMap(g)).step();
                    } else if (c instanceof Pure) {
                        final Pure<C> pure = (Pure<C>)c;
                        return g.apply(pure.a).step();
                    } else {
                        return this;
                    }
                }
            }

            default <B> Free<B> flatMap(F<A, Free<B>> f) {
                return flatMapped(this, f);
            }

            default <B> Free<B> flatMap(Free<B> b) {
                return flatMapped(this, x -> b);
            }

            default Free<A> step() {
                return this;
            }

            interface NT<A, R> extends F<Free<A>, R> {
                default R apply(Free<A> fr) {
                    Free<A> step = fr.step();
                    if (step instanceof Pure) {
                        return transform((Pure<A>)step);
                    } else if (step instanceof Suspend) {
                        return transform((Suspend<A>)step);
                    } else {
                        return transform((FlatMapped<A, Object>)step);
                    }
                }

                R transform(Pure<A> pure);
                R transform(Suspend<A> suspend);
                <V> R transform(FlatMapped<A, V> flatMapped);
            }

            default <T> Id<A> foldMapId(F<DSL<T>, Id<T>> f) {
                return Id.tailRecM(this, new NT<A, Id<Either<Free<A>, A>>>() {
                    @Override
                    public Id<Either<Free<A>, A>> transform(Pure<A> pure) {
                        return Id.pure(Either.right(pure.a));
                    }

                    @Override
                    public Id<Either<Free<A>, A>> transform(Suspend<A> suspend) {
                        final F<DSL<A>, Id<A>> fA = ((F<DSL<A>, Id<A>>)(F)f);
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

            default <T, U> State<IMapS<U>, A> foldMapMS(F<DSL<T>, State<IMapS<U>, A>> f) {
                return State.tailRecM(this, new NT<A, State<IMapS<U>, Either<Free<A>, A>>>() {
                    @Override
                    public State<IMapS<U>, Either<Free<A>, A>> transform(Pure<A> pure) {
                        return State.pure(Either.right(pure.a));
                    }

                    @Override
                    public State<IMapS<U>, Either<Free<A>, A>> transform(Suspend<A> suspend) {
                        final F<DSL<A>, State<IMapS<U>, A>> fA = ((F<DSL<A>, State<IMapS<U>, A>>)(F)f);
                        return fA.apply(suspend.a).map(Either::right);
                    }

                    @Override
                    public <V> State<IMapS<U>, Either<Free<A>, A>> transform(FlatMapped<A, V> flatMapped) {
                        final F<DSL<T>, State<IMapS<U>, V>> fV = (F<DSL<T>, State<IMapS<U>, V>>)(F)f;
                        return flatMapped.c.foldMapMS(fV)
                                .map(flatMapped.g)
                                .map(Either::left);
                    }
                });
            }
        }
    }

    static <T> DSL.Free<Unit> put(String key, T value) {
        return liftF(DSL.put(key, value));
    }

    static <T> DSL.Free<Option<T>> get(String key) {
        return liftF(DSL.get(key));
    }

    static <T> DSL.Free<Unit> delete(String key) {
        return liftF(DSL.delete(key));
    }

    static <T> DSL.Free<Unit> update(String key, F<T, T> f) {
        return KVStore.<T>get(key)
                .flatMap(vo ->
                        vo.map(v ->
                                put(key, f.apply(v))
                        ).getOrElse(DSL.Free.pure(Unit.UNIT)));
    }

    static final DSL.Free<Option<String>> program =
            put("red", "abc").flatMap(
                    KVStore.<String>update("red", x -> x + "xyz").flatMap(
                            put("green", "def").flatMap(
                                    KVStore.<String>get("red").flatMap(n ->
                                            delete("green").flatMap(pure(n))
                                    )
                            )
                    )
            );

    static final DSL.Free<Option<Integer>> programInt =
            put("red", 3).flatMap(
                    KVStore.<Integer>update("red", x -> x + 7).flatMap(
                            put("green", 11).flatMap(
                                    KVStore.<Integer>get("red").flatMap(n ->
                                            delete("green").flatMap(pure(n))
                                    )
                            )
                    )
            );

    static <T> F<DSL<T>, Id<T>> impureInterp() {
        final IMapSS[] m = {IMapSS.empty()};

        return kvsT -> {
            if (kvsT instanceof DSL.Get) {
                final DSL.Get<T> get = (DSL.Get<T>)kvsT;
                System.out.println(get);
                return (Id<T>)Id.pure(m[0].get(get.key));
            } else if (kvsT instanceof DSL.Put) {
                final DSL.Put put = (DSL.Put)kvsT;
                System.out.println(put);
                m[0] = m[0].put(put.key, put.value.toString());
                return (Id<T>)Id.pure(Unit.UNIT);
            } else {
                final DSL.Delete del = (DSL.Delete)kvsT;
                System.out.println(del);
                m[0] = m[0].del(del.key);
                return (Id<T>)Id.pure(Unit.UNIT);
            }
        };
    }

    static <T, Object> F<DSL<T>, State<IMapS<Object>, T>> pureInterp() {
        return kvsT -> {
            if (kvsT instanceof DSL.Get) {
                final DSL.Get<T> get = (DSL.Get<T>)kvsT;
                System.out.println(get);
                return (State<IMapS<Object>, T>)State.<IMapS<Object>, Option<T>>inspect(m -> m.get(get.key).map(o -> (T)o));
            } else if (kvsT instanceof DSL.Put) {
                final DSL.Put put = (DSL.Put)kvsT;
                System.out.println(put);
                return (State<IMapS<Object>, T>)State.<IMapS<Object>>modify(m -> m.put(put.key, (Object)put.value));
            } else {
                final DSL.Delete del = (DSL.Delete)kvsT;
                System.out.println(del);
                return (State<IMapS<Object>, T>)State.<IMapS<Object>>modify(m -> m.del(del.key));
            }
        };
    }

    public static void main(String[] args) {
        {
            final Option<String> oi = program.foldMapId(impureInterp()).value;
            System.out.println("\nResult=" + oi + "\n");
        }

        {
            final Option<Integer> oi = programInt.foldMapMS(pureInterp()).eval(IMapS.empty());
            System.out.println("\nResult=" + oi + "\n");
        }
    }
}

@FunctionalInterface
interface IMapSS {
    static IMapSS empty() {
        return k -> {
            throw new RuntimeException(k + " not found");
        };
    }

    Option<String> get(String key);

    default IMapSS put(String key, String value) {
        return k -> k.equals(key) ? Option.some(value) : get(k);
    }


    default IMapSS del(String key) {
        return k -> k.equals(key) ? Option.none() : get(k);
    }
}

@FunctionalInterface
interface IMapS<V> {
    static <V> IMapS<V> empty() {
        return k -> {
            throw new RuntimeException(k + " not found");
        };
    }

    Option<V> get(String key);

    default IMapS<V> put(String key, V value) {
        return k -> k.equals(key) ? Option.some(value) : get(k);
    }

    default IMapS<V> del(String key) {
        return k -> k.equals(key) ? Option.none() : get(k);
    }
}
