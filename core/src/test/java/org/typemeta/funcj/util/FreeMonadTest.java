package org.typemeta.funcj.util;

import static org.typemeta.funcj.functions.Functions.F;

public class FreeMonadTest {

    public static <T> F<T, T> id() {
        return F.id();
    }

    // ADT for Command.

    public interface Command<NEXT> {
        <T> T match (F<Get<NEXT>, T> get, F<Set<NEXT>, T> set, F<End<NEXT>, T> end);
    }

    public static class Get<NEXT> implements Command<NEXT> {
        public final String name;
        public final F<String, NEXT> f;

        public Get(String name, F<String, NEXT> f) {
            this.name = name;
            this.f = f;
        }

        @Override
        public <T> T match(F<Get<NEXT>, T> get, F<Set<NEXT>, T> set, F<End<NEXT>, T> end) {
            return get.apply(this);
        }
    }

    public static <NEXT> Command<NEXT> get(String name, F<String, NEXT> f) {
        return new Get<>(name, f);
    }

    public static class Set<NEXT> implements Command<NEXT> {
        public final String name;
        public final String value;
        public final NEXT next;

        public Set(String name, String value, NEXT next) {
            this.name = name;
            this.value = value;
            this.next = next;
        }

        @Override
        public <T> T match(
                F<Get<NEXT>, T> get,
                F<Set<NEXT>, T> set,
                F<End<NEXT>, T> end) {
            return set.apply(this);
        }
    }

    public static <NEXT> Command<NEXT> set(String name, String value, NEXT next) {
        return new Set<>(name, value, next);
    }

    public static class End<NEXT> implements Command<NEXT> {
        @Override
        public <T> T match(
                F<Get<NEXT>, T> get,
                F<Set<NEXT>, T> set,
                F<End<NEXT>, T> end) {
            return end.apply(this);
        }
    }

    public static <NEXT> Command<NEXT> end() {
        return new End<>();
    }

    public static <A, B> Command<B> fmap(F<A, B> f, Command<A> cmd) {
        return cmd.<Command<B>>match(
                get -> get(get.name, f.compose(get.f)),
                set -> set(set.name, set.value, f.apply(set.next)),
                end -> new End<>()
        );
    }

    // ADT for FreeMonad.
    public interface FreeMonad<A> {
        <T> T match(F<Free<A>, T> free, F<Retn<A>, T> retn);

        default <B> FreeMonad<B> bind(F<A, FreeMonad<B>> f) {
            return this.<FreeMonad<B>>match(
                    fr -> free(fmap(x -> x.bind(f), fr.cmd)),
                    retn -> f.apply(retn.a)
            );
        }

        default <B> FreeMonad<B> bind(FreeMonad<B> m) {
            return this.<FreeMonad<B>>match(
                    fr -> free(fmap(x -> m, fr.cmd)),
                    retn -> m
            );
        }
    }

    public static class Free<A> implements FreeMonad<A> {
        public final Command<FreeMonad<A>> cmd;

        public Free(Command<FreeMonad<A>> cmd) {
            this.cmd = cmd;
        }

        @Override
        public <T> T match(F<Free<A>, T> free, F<Retn<A>, T> retn) {
            return free.apply(this);
        }
    }

    public static <A> FreeMonad<A> free(Command<FreeMonad<A>> cmd) {
        return new Free<>(cmd);
    }

    public static class Retn<A> implements FreeMonad<A> {
        public final A a;

        public Retn(A a) {
            this.a = a;
        }

        @Override
        public <T> T match(F<Free<A>, T> free, F<Retn<A>, T> retn) {
            return retn.apply(this);
        }
    }

    public static <A> FreeMonad<A> retn(A a) {
        return new Retn<>(a);
    }

    public static <A> FreeMonad<A> liftFree(Command<A> cmd) {
        return free(fmap(x -> retn(x), cmd));
    }

    public static <A> FreeMonad<A> getF(String key) {
        return liftFree(get(key, x -> (A)x));
    }

    public static <A> FreeMonad<A> setF(String key, String value) {
        return liftFree(set(key, value, null));
    }

    public static <A> FreeMonad<A> endF() {
        return liftFree(end());
    }

    static final FreeMonad<Integer> test =
            free(get("foo", foo ->
                    free(set("bar", foo,
                             free(end())))));

    static final FreeMonad<String> test2() {
        return
                FreeMonadTest.<String>
                        getF("foo").bind(foo ->
                                                 setF("bar", foo).bind(
                                                         endF()));
    }

    private static <A> A error(String msg) {
        throw new RuntimeException(msg);
    }

    @FunctionalInterface
    interface IMap<K, V> {
        static <K, V> IMap<K, V> empty() {
            return getK -> {
                throw new RuntimeException("Not found");
            };
        }

        V get(K key);

        default IMap<K, V> put(K key, V value) {
            return getK -> {
                if (getK.equals(key)) {
                    return value;
                } else {
                    return get(getK);
                }
            };
        }
    }

    public static <A> IMap<String, String> run(FreeMonad<A> freeCmd, IMap<String, String> m) {
        return freeCmd.match(
                free -> free.cmd.match(
                        get -> run(get.f.apply(m.get(get.name)), m),
                        set -> run(set.next, m.put(set.name, set.value)),
                        end -> m
                ),
                retn -> error("Unreachable")
        );
    }

    public static void main(String[] args) {
        final IMap<String, String> m = IMap.<String, String>empty().put("foo", "xyz");
        final IMap<String, String> m2 = run(test2(), m);
        System.out.println("bar=" + m2.get("bar"));
    }
}

