package org.javafp.data;

import java.util.Objects;

public interface Lazy<T> extends Functions.F0<T> {
    static <T> Lazy<T> ofUnsafe(Functions.F0<T> get) {
        return new LazyImpl<T>(get);
    }

    static <T> Lazy<T> of(Functions.F0<T> get) {
        return new LazyThreadSafeImpl<T>(get);
    }
}

class LazyImpl<T> implements Lazy<T> {

    private class UnEval implements Functions.F0<T> {
        final Functions.F0<T> source;

        private UnEval(Functions.F0<T> source) {
            this.source = source;
        }

        @Override
        public T apply() {
            final T eval = source.apply();
            LazyImpl.this.get = () -> eval;
            return eval;
        }
    }

    private Functions.F0<T> get;

    LazyImpl(Functions.F0<T> get) {
        this.get = new UnEval(Objects.requireNonNull(get));
    }

    @Override
    public T apply() {
        return get.apply();
    }
}

class LazyThreadSafeImpl<T> implements Lazy<T> {

    private class UnEval implements Functions.F0<T> {
        transient Functions.F0<T> source;

        private UnEval(Functions.F0<T> source) {
            this.source = source;
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

    private Functions.F0<T> get;

    LazyThreadSafeImpl(Functions.F0<T> get) {
        this.get = new UnEval(Objects.requireNonNull(get));
    }

    @Override
    public T apply() {
        return get.apply();
    }
}

