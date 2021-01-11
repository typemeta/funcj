package org.typemeta.funcj.codec2.core;

public interface NullCodec<IN, OUT> {

    <T> CodecFormat.EncodeResult<OUT> encode(EncoderCore<OUT> core, Context ctx, T val, OUT out);

    boolean decode(DecoderCore<IN> core, Context ctx, IN in);
}
