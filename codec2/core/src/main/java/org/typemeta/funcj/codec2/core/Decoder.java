package org.typemeta.funcj.codec2.core;

public interface Decoder<T, IN> {
    Class<T> type();

    default T decode(DecoderCore<IN> core, Context ctx, IN in) {
        if (core.format().nullCodec().decode(core, ctx, in)) {
            return null;
        } else {
            return decodeImpl(core, ctx, in);
        }
    }

    T decodeImpl(DecoderCore<IN> core, Context ctx, IN in);
}
