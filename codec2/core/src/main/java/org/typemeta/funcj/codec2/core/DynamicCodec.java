package org.typemeta.funcj.codec2.core;

public interface DynamicCodec<IN, OUT> {
    <T> CodecFormat.EncodeResult<OUT> encodeDynamic(EncoderCore<OUT> core, Context ctx, T value, OUT out, Encoder<T, OUT> encoder);

    <T> T decodeDynamic(EncoderCore<OUT> core, Context ctx, IN in);
}
