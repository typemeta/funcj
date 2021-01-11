package org.typemeta.funcj.codec2.core;

public interface EncoderCore<OUT> {
    CodecFormat<?, OUT> format();

    CodecConfig config();

    <T> Encoder<T, OUT> getEncoder(Class<T> type);
}
