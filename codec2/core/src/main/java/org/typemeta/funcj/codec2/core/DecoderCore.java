package org.typemeta.funcj.codec2.core;

public interface DecoderCore<IN> {
    CodecFormat<IN, ?> format();

    CodecConfig config();

    <T> Decoder<T, IN> getDecoder(Class<T> type);
}
