package org.javafp.data;

import static org.javafp.util.Functions.*;

import java.util.Objects;

public interface Lazy<T> extends F0<T> {
    static <T> Lazy<T> of(F0<T> get) {
        return new LazyImpl<T>(get);
    }

    static <T> Lazy<T> ofTS(F0<T> get) {
        return new LazyThreadSafeImpl<T>(get);
    }

    static <A, R> F<Lazy<A>, Lazy<R>> lift(F<A, R> f) {
        return lt -> Lazy.of(() -> f.apply(lt.apply()));
    }

    static <A, B, R> F2<Lazy<A>, Lazy<B>, Lazy<R>> lift(F2<A, B, R> f) {
        return (la, lb) -> Lazy.of(() -> f.apply(la.apply(), lb.apply()));
    }

    static <A, B, C, R> F3<Lazy<A>, Lazy<B>, Lazy<C>, Lazy<R>> lift(F3<A, B, C, R> f) {
        return (la, lb, lc) -> Lazy.of(() -> f.apply(la.apply(), lb.apply(), lc.apply()));
    }
}

class LazyImpl<T> implements Lazy<T> {

    private class UnEval implements F0<T> {
        final F0<T> source;

        private UnEval(F0<T> source) {
            this.source = source;
        }

        @Override
        public T apply() {
            final T eval = source.apply();
            LazyImpl.this.get = () -> eval;
            return eval;
        }
    }

    private F0<T> get;

    LazyImpl(F0<T> get) {
        this.get = new UnEval(Objects.requireNonNull(get));
    }

    @Override
    public T apply() {
        return get.apply();
    }

    @Override
    public String toString() {
        return "Lazy<" + apply() + '>';
    }
}

class LazyThreadSafeImpl<T> implements Lazy<T> {

    private class UnEval implements F0<T> {
        F0<T> source;

        private UnEval(F0<T> source) {
            this.source = Objects.requireNonNull(source);
        }

        @Override
        public synchronized T apply() {
            if (source != null) {
                final T eval = source.apply();
                LazyThreadSafeImpl.this.get = () -> eval;
                source = null;
                return eval;
            } else {
                return LazyThreadSafeImpl.this.get.apply();
            }
        }
    }

    private F0<T> get;

    LazyThreadSafeImpl(F0<T> get) {
        this.get = new UnEval(Objects.requireNonNull(get));
    }

    @Override
    public T apply() {
        return get.apply();
    }

    @Override
    public String toString() {
        return "Lazy<" + apply() + '>';
    }
}

