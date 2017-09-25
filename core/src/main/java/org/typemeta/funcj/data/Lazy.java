package org.typemeta.funcj.data;

import org.typemeta.funcj.util.Functions;

import java.util.Objects;

/**
 * Wrapper for values which are computed lazily, i.e. on demand.
 * The result is computed once and cached.
 * @param <T> type of value
 */
public interface Lazy<T> extends Functions.F0<T> {
    /**
     * Construct a lazy value from a value supplier.
     * The lazy value is not thread-safe, meaning the supplier may be invoked
     * multiple times if the {@code Lazy} value is passed to multiple threads
     * before it is computed.
     * @param get supplier of the value
     * @param <T> value type
     * @return lazy value
     */
    static <T> Lazy<T> of(Functions.F0<T> get) {
        return new LazyImpl<T>(get);
    }

    /**
     * Construct a lazy value from a value supplier.
     * The lazy value is thread-safe, meaning the supplier will only be invoked
     * once, even if the {@code Lazy} value is passed to multiple threads.
     * @param get supplier of the value
     * @param <T> value type
     * @return lazy value
     */
    static <T> Lazy<T> ofTS(Functions.F0<T> get) {
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

    @Override
    public String toString() {
        return "Lazy<" + apply() + '>';
    }
}

class LazyThreadSafeImpl<T> implements Lazy<T> {

    private class UnEval implements Functions.F0<T> {
        Functions.F0<T> source;

        private UnEval(Functions.F0<T> source) {
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

    private Functions.F0<T> get;

    LazyThreadSafeImpl(Functions.F0<T> get) {
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

