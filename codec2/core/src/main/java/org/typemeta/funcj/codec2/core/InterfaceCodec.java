package org.typemeta.funcj.codec2.core;

import org.typemeta.funcj.codec2.core.utils.CodecException;

public class InterfaceCodec<T, IN, OUT> implements NonFinalCodec<T, IN, OUT> {

    protected final Class<T> clazz;

    protected InterfaceCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<T> type() {
        return clazz;
    }

    @Override
    public OUT encodeImpl(EncoderCore<OUT> core, Context ctx, T value, OUT out) {
        throw new CodecException("Internal error - can't encode an interface (" + clazz.getName() + ")");
    }

    @Override
    public T decodeImpl(DecoderCore<IN> core, Context ctx, IN in) {
        throw new CodecException("Internal error - can't decode an interface (" + clazz.getName() + ")");
    }
}
