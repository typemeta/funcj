package org.typemeta.funcj.codec2.core;

import org.typemeta.funcj.codec2.core.utils.CodecException;

import java.util.Objects;
import java.util.function.Supplier;

public class CodecRef<T, IN, OUT> implements Codec<T, IN, OUT> {

    private enum Uninitialised implements Codec {
        INSTANCE;

        static Codec of() {
            return INSTANCE;
        }

        @Override
        public Class<Object> type() {
            throw error();
        }

        @Override
        public Object encode(EncoderCore core, Context ctx, Object value, Object o) {
            return error();
        }

        @Override
        public Object encodeImpl(EncoderCore core, Context ctx, Object value, Object o) {
            return error();
        }

        @Override
        public Object decode(DecoderCore core, Context ctx, Object o) {
            return error();
        }

        @Override
        public Object decodeImpl(DecoderCore core, Context ctx, Object o) {
            return error();
        }

        private static RuntimeException error() {
            return new CodecException("Uninitialised lazy Codec reference");
        }
    }

    private volatile Codec<T, IN, OUT> impl;

    /**
     * Initialise this reference.
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

    public synchronized Codec<T, IN, OUT> setIfUninitialised(Supplier<Codec<T, IN, OUT>> implSupp) {
        if (this.impl == Uninitialised.INSTANCE) {
            this.impl = Objects.requireNonNull(implSupp.get());
        }
        return impl;
    }

    public Codec<T, IN, OUT> get() {
        return impl;
    }

    @Override
    public Class<T> type() {
        return null;
    }

    @Override
    public OUT encode(EncoderCore<OUT> core, Context ctx, T value, OUT out) {
        return impl.encode(core, ctx, value, out);
    }

    @Override
    public OUT encodeImpl(EncoderCore<OUT> core, Context ctx, T value, OUT out) {
        return impl.encodeImpl(core, ctx, value, out);
    }

    @Override
    public T decode(DecoderCore<IN> core, Context ctx, IN in) {
        return impl.decode(core, ctx, in);
    }

    @Override
    public T decodeImpl(DecoderCore<IN> core, Context ctx, IN in) {
        return impl.decodeImpl(core, ctx, in);
    }
}
