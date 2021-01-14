package org.typemeta.funcj.codec2.core;

public interface NonFinalCodec<T, IN, OUT> extends Codec<T, IN, OUT> {
    @Override
    default OUT encode(EncoderCore<OUT> core, Context ctx, T value, OUT out) {
        final CodecFormat.EncodeResult<OUT> nullResult = core.format().encodeNull(core, ctx, value, out);
        if (nullResult.encoded()) {
            return nullResult.out();
        } else {
            final CodecFormat.EncodeResult<OUT> dynResult = core.format().encodeDynamic(core, ctx, value, out, this);
            if (dynResult.encoded()) {
                return dynResult.out();
            } else {
                return encodeImpl(core, ctx, value, out);
            }
        }
    }

    @Override
    default T decode(DecoderCore<IN> core, Context ctx, IN in) {
        if (core.format().decodeNull(core, ctx, in)) {
            return null;
        } else {
            final T value = core.format().decodeDynamic(core, ctx, in);
            if (value != null) {
                return value;
            } else {
                return decodeImpl(core, ctx, in);
            }
        }
    }
}
