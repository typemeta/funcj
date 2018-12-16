package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.json.JsonTypes.*;
import org.typemeta.funcj.codec.stream.StreamCodecCoreDelegate;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class JsonCodecCore
        extends StreamCodecCoreDelegate<InStream, OutStream, Config>
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
        encode(type, value, JsonTypes.outputOf(writer));
    }

    @Override
    public <T> T decode(Class<? super T> type, Reader reader) {
        return decode(type, JsonTypes.inputOf(reader));
    }
}
