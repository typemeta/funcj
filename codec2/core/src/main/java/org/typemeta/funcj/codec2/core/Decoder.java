package org.typemeta.funcj.codec2.core;

public interface Decoder<T, IN> {
    Class<T> type();

    T decode(DecoderCore<IN> core, Context ctx, IN in);

    T decodeImpl(DecoderCore<IN> core, Context ctx, IN in);
}
