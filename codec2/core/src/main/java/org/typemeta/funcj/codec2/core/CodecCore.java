package org.typemeta.funcj.codec2.core;

public interface CodecCore<IN, OUT> extends EncoderCore<OUT>, DecoderCore<IN> {
    CodecFormat<IN, OUT> format();

    CodecConfig config();

    <T> Codec<T, IN, OUT> getCodec(Class<T> type);
}
