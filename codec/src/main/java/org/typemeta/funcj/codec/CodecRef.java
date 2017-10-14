package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

import java.util.Objects;

public class CodecRef<T, E> implements Codec<T, E> {

    private enum Uninitialised implements Codec<Object, Object> {
        INSTANCE;

        @Override
        public Object encode(Object val, Object enc) {
            throw error();
        }

        @Override
        public Object decode(Class<Object> dynType, Object enc) {
            throw error();
        }

        @Override
        public Object decode(Object enc) {
            throw error();
        }

        private static RuntimeException error() {
            return new RuntimeException("Uninitialised lazy Codec reference");
        }

        @SuppressWarnings("unchecked")
        static <T, E> Codec<T, E> of() {
            return (Codec<T, E>) INSTANCE;
        }
    }

    private Codec<T, E> impl;

    CodecRef(Codec<T, E> p) {
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
    public Codec<T, E> set(Codec<T, E> impl) {
        if (this.impl != Uninitialised.INSTANCE) {
            throw new IllegalStateException("CodecRef is already initialised");
        } else {
            this.impl = Objects.requireNonNull(impl);
            return this;
        }
    }

    public synchronized Codec<T, E> setIfUninitialised(Functions.F0<Codec<T, E>> implSupp) {
        if (this.impl == Uninitialised.INSTANCE) {
            this.impl = Objects.requireNonNull(implSupp.apply());
        }
        return impl;
    }

    public Codec<T, E> get() {
        return impl;
    }

    @Override
    public E encode(T val, E enc) {
        return impl.encode(val, enc);
    }

    @Override
    public T decode(Class<T> dynType, E enc) {
        return impl.decode(dynType, enc);
    }

    @Override
    public T decode(E enc) {
        return impl.decode(enc);
    }
}
