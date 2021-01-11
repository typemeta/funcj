package org.typemeta.funcj.codec2.core;

public interface Encoder<T, OUT> {
    Class<T> type();

    default OUT encode(EncoderCore<OUT> core, Context ctx, T value, OUT out) {
        final CodecFormat.EncodeResult<OUT> encodeResult = core.format().nullCodec().encode(core, ctx, value, out);
        if (encodeResult.encoded()) {
            return encodeResult.out();
        } else {
            final Class<? extends T> dynamicType = (Class)value.getClass();
            if (type() == dynamicType) {
                return encodeImpl(core, ctx, value, out);
            } else {
                final Encoder<? extends T, OUT> encoder = core.getEncoder(dynamicType);
                return core.format().encodeDynamic(value, out, encoder);
            }
        }
    }

    OUT encodeImpl(EncoderCore<OUT> core, Context ctx, T value, OUT out);
}
