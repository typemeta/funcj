package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.impl.CodecCoreImpl;
import org.typemeta.funcj.codec.json.JsonTypes.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class JsonCodecCore
        extends CodecCoreDelegate<InStream, OutStream, Config>
        implements CodecAPI.RW {

    public JsonCodecCore(JsonCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public JsonCodecCore(Config config) {
        this(new JsonCodecFormat(config));
    }

    public JsonCodecCore() {
        this(new JsonConfig());
    }

    @Override
    public <T> Writer encode(Class<? super T> type, T value, Writer writer) {
        encodeImpl(type, value, JsonTypes.outputOf(writer));
        return writer;
    }

    @Override
    public <T> T decode(Class<? super T> type, Reader reader) {
        return decodeImpl(type, JsonTypes.inputOf(reader));
    }
}
