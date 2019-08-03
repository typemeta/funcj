package org.typemeta.funcj.codec.avro;

import org.typemeta.funcj.codec.CodecAPI;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class AvroCodecCore
        extends CodecCoreDelegate<WithSchema, Object, Config>
        implements CodecAPI<Object, Object> {

    public AvroCodecCore(AvroCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public AvroCodecCore(Config config) {
        this(new AvroCodecFormat(config));
    }

    public AvroCodecCore() {
        this(new AvroConfig());
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
