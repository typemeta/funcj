package org.typemeta.funcj.codec2.json;

import org.typemeta.funcj.codec2.core.*;
import org.typemeta.funcj.codec2.json.JsonTypes.InStream;
import org.typemeta.funcj.codec2.json.JsonTypes.OutStream;

public class JsonCodecCore implements CodecCore<InStream, OutStream> {
    @Override
    public CodecFormat<InStream, OutStream> format() {
        return null;
    }

    @Override
    public <T> Encoder<T, OutStream> getEncoder(Class<T> type) {
        return null;
    }

    @Override
    public <T> Decoder<T, InStream> getDecoder(Class<T> type) {
        return null;
    }

    @Override
    public <T> Codec<T, InStream, OutStream> getCodec(Class<T> type) {
        return null;
    }
}
