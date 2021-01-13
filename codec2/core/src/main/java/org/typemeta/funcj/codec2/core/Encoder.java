package org.typemeta.funcj.codec2.core;

public interface Encoder<T, OUT> {
    Class<T> type();

    OUT encode(EncoderCore<OUT> core, Context ctx, T value, OUT out);

    OUT encodeImpl(EncoderCore<OUT> core, Context ctx, T value, OUT out);
}
