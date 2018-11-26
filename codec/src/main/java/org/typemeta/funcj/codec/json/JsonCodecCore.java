package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.json.JsonCodec.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class JsonCodecCore
        extends CodecCoreDelegate<Input, Output, Config>
        implements CodecAPI {

    public JsonCodecCore(JsonCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public JsonCodecCore(Config config) {
        this(new JsonCodecFormat(config));
    }

    public JsonCodecCore() {
        this(new JsonConfigImpl());
    }

    @Override
    public <T> void encode(Class<? super T> type, T value, Writer writer) {
        encode(type, value, JsonCodec.outputOf(writer));
    }

    @Override
    public <T> T decode(Class<? super T> type, Reader reader) {
        return decode(type, JsonCodec.inputOf(reader));
    }
}
