package org.typemeta.funcj.codec2.core;

public interface Codec<T, IN, OUT> extends Encoder<T, OUT>, Decoder<T, IN> {
    @Override
    Class<T> type();

    @Override
    OUT encode(EncoderCore<OUT> core, Context ctx, T value, OUT out);

    @Override
    OUT encodeImpl(EncoderCore<OUT> core, Context ctx, T value, OUT out);

    @Override
    T decode(DecoderCore<IN> core, Context ctx, IN in);

    @Override
    T decodeImpl(DecoderCore<IN> core, Context ctx, IN in);
}
