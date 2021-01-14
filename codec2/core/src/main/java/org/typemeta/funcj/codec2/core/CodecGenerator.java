package org.typemeta.funcj.codec2.core;

public interface CodecGenerator<IN, OUT> {
    <T> Codec<T, IN, OUT> generate(CodecCore<IN, OUT> core, Class<T> type);
}
