package org.funcj.data;

import java.util.Objects;

import static org.funcj.util.Functions.F0;

/**
 * Interface for values which are acquired lazily.
 * @param <T> type of value
 */
public interface Lazy<T> extends F0<T> {
    /**
     * Construct a lazy value from value supplier.
     * The lazy value is not thead-safe, meaning the supplier may be invoked
     * multiple times if the {@code Lazy} value is passed to multiple threads.
     * @param get supplier of the value
     * @param <T> value type
     * @return lazy value
     */
    static <T> Lazy<T> of(F0<T> get) {
        return new LazyImpl<T>(get);
    }

    /**
     * Construct a lazy value from value supplier.
     * The lazy value is thead-safe, meaning the supplier will only be invoked
     * once, even if the {@code Lazy} value is passed to multiple threads.
     * @param get supplier of the value
     * @param <T> value type
     * @return lazy value
     */
    static <T> Lazy<T> ofTS(F0<T> get) {
        return new LazyThreadSafeImpl<T>(get);
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

