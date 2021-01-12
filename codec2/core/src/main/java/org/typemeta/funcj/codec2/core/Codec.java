package org.typemeta.funcj.codec2.core;

import org.typemeta.funcj.codec2.core.CodecFormat.EncodeResult;

public interface Codec<T, IN, OUT> extends Encoder<T, OUT>, Decoder<T, IN> {
    @Override
    Class<T> type();

    @Override
    default OUT encode(EncoderCore<OUT> core, Context ctx, T value, OUT out) {
        final EncodeResult<OUT> nullEncRes = core.format().nullCodec().encode(core, ctx, value, out);
        if (nullEncRes.encoded()) {
            return nullEncRes.out();
        } else {
            final EncodeResult<OUT> dynEncRes = core.format().dynamicCodec().encodeDynamic(core, ctx, value, out, this);
            if (dynEncRes.encoded()) {
                return dynEncRes.out();
            } else {
                return encodeImpl(core, ctx, value, out);
            }
        }
    }

    @Override
    OUT encodeImpl(EncoderCore<OUT> core, Context ctx, T value, OUT out);

    @Override
    default T decode(DecoderCore<IN> core, Context ctx, IN in) {
        if (core.format().nullCodec().decode(core, ctx, in)) {
            return null;
        } else {
            final T dynValue = core.format().dynamicCodec().decodeDynamic(core, ctx, in);
            if (dynValue != null) {
                return dynValue ;
            } else {
                return decodeImpl(core, ctx, in);
            }
        }
    }

    @Override
    T decodeImpl(DecoderCore<IN> core, Context ctx, IN in);
}
