package org.typemeta.funcj.codec2.core;

public interface FinalCodec<T, IN, OUT> extends Codec<T, IN, OUT> {
    @Override
    default OUT encode(EncoderCore<OUT> core, Context ctx, T value, OUT out) {
        final CodecFormat.EncodeResult<OUT> encodeResult = core.format().encodeNull(core, ctx, value, out);
        if (encodeResult.encoded()) {
            return encodeResult.out();
        } else {
            return encodeImpl(core, ctx, value, out);
        }
    }

    @Override
    default T decode(DecoderCore<IN> core, Context ctx, IN in) {
        if (core.format().decodeNull(core, ctx, in)) {
            return null;
        } else {
            return decodeImpl(core, ctx, in);
        }
    }
}
