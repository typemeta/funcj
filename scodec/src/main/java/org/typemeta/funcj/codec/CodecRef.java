package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

import java.util.Objects;

/**
 * A reference to a {@link Codec}.
 * {@code CodecRef} implements the {@link Codec} interface.
 * Used when looking up a codec for a recursive type.
 * @param <T>       the raw type to be encoded/decoded
 * @param <IN, OUT>       the encoded type
 */
public class CodecRef<T, IN, OUT> implements Codec<T, IN, OUT> {

    private enum Uninitialised implements Codec<Object, Object, Object> {
        INSTANCE;

        @Override
        public Object encode(Object val, Object in) throws Exception {
            throw error();
        }

        @Override
        public Object decode(Class<Object> dynType, Object in) throws Exception {
            throw error();
        }

        @Override
        public Object decode(Object in) throws Exception {
            throw error();
        }

        private static RuntimeException error() {
            return new RuntimeException("Uninitialised lazy Codec reference");
        }

        @SuppressWarnings("unchecked")
        static <T, IN, OUT> Codec<T, IN, OUT> of() {
            return (Codec<T, IN, OUT>) INSTANCE;
        }
    }

    private Codec<T, IN, OUT> impl;

    CodecRef(Codec<T, IN, OUT> p) {
        this.impl = Objects.requireNonNull(impl);
    }

    CodecRef() {
        this.impl = Uninitialised.of();
    }

    /**
     * Initialise this reference
     * @param impl      the codec
     * @return          this codec
     */
    public Codec<T, IN, OUT> set(Codec<T, IN, OUT> impl) {
        if (this.impl != Uninitialised.INSTANCE) {
            throw new IllegalStateException("CodecRef is already initialised");
        } else {
            this.impl = Objects.requireNonNull(impl);
            return this;
        }
    }

    public synchronized Codec<T, IN, OUT> setIfUninitialised(Functions.F0<Codec<T, IN, OUT>> implSupp) {
        if (this.impl == Uninitialised.INSTANCE) {
            this.impl = Objects.requireNonNull(implSupp.apply());
        }
        return impl;
    }

    public Codec<T, IN, OUT> get() {
        return impl;
    }

    @Override
    public IN encode(T val, IN in) throws Exception {
        return impl.encode(val, in);
    }

    @Override
    public T decode(Class<T> dynType, IN in) throws Exception {
        return impl.decode(dynType, in);
    }

    @Override
    public T decode(IN in) throws Exception {
        return impl.decode(in);
    }
}
