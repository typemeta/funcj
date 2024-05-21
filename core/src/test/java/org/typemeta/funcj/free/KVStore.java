package org.typemeta.funcj.free;

import org.typemeta.funcj.control.*;
import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions.F;

import static org.typemeta.funcj.free.Free.*;

/**
 * Free monad example.
 */
@SuppressWarnings("unchecked")
public class KVStore {
    public interface DSL<A> {
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

    }

    protected static <T> Free<Unit> put(String key, T value) {
        return liftF(DSL.put(key, value));
    }

    protected static <T> Free<Option<T>> get(String key) {
        return liftF(DSL.get(key));
    }

    protected static <T> Free<Unit> delete(String key) {
        return liftF(DSL.delete(key));
    }

    protected static <T> Free<Unit> update(String key, F<T, T> f) {
        return KVStore.<T>get(key)
                .flatMap(vo ->
                        vo.map(v ->
                                put(key, f.apply(v))
                        ).orElseGet(Free.pure(Unit.UNIT)));
    }

    protected static final Free<Option<String>> program =
            put("red", "abc").flatMap(
                    KVStore.<String>update("red", x -> x + "xyz").flatMap(
                            put("green", "def").flatMap(
                                    KVStore.<String>get("red").flatMap(n ->
                                            delete("green").flatMap(pure(n))
                                    )
                            )
                    )
            );

    protected static final Free<Option<Integer>> programInt =
            put("red", 3).flatMap(
                    KVStore.<Integer>update("red", x -> x + 7).flatMap(
                            put("green", 11).flatMap(
                                    KVStore.<Integer>get("red").flatMap(n ->
                                            delete("green").flatMap(pure(n))
                                    )
                            )
                    )
            );

    protected static <T> F<DSL<T>, Id<T>> impureInterp() {
        final IMapSS[] m = {IMapSS.empty()};

        return kvsT -> {
            if (kvsT instanceof DSL.Get) {
                final DSL.Get<T> get = (DSL.Get<T>)kvsT;
                System.out.println(get);
                return (Id<T>)Id.pure(m[0].get(get.key));
            } else if (kvsT instanceof DSL.Put) {
                final DSL.Put<T, ?> put = (DSL.Put<T, ?>)kvsT;
                System.out.println(put);
                m[0] = m[0].put(put.key, put.value.toString());
                return (Id<T>)Id.pure(Unit.UNIT);
            } else {
                final DSL.Delete<T> del = (DSL.Delete<T>)kvsT;
                System.out.println(del);
                m[0] = m[0].del(del.key);
                return (Id<T>)Id.pure(Unit.UNIT);
            }
        };
    }

    protected static <T, Object> F<DSL<T>, State<IMapS<Object>, T>> pureInterp() {
        return kvsT -> {
            if (kvsT instanceof DSL.Get) {
                final DSL.Get<T> get = (DSL.Get<T>)kvsT;
                System.out.println(get);
                return (State<IMapS<Object>, T>)State.<IMapS<Object>, Option<T>>inspect(m -> m.get(get.key).map(o -> (T)o));
            } else if (kvsT instanceof DSL.Put) {
                final DSL.Put<T, ?> put = (DSL.Put<T, ?>)kvsT;
                System.out.println(put);
                return (State<IMapS<Object>, T>)State.<IMapS<Object>>modify(m -> m.put(put.key, (Object)put.value));
            } else {
                final DSL.Delete<T> del = (DSL.Delete<T>)kvsT;
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
