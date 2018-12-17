package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.data.Unit;

import java.io.*;

/**
 * Interface for classes which implement an encoding via byte streams.
 */
public class AvroCodecCore
        extends CodecCoreDelegate<Unit, Schema, Config>
        implements CodecAPI {

    public AvroCodecCore(AvroCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public AvroCodecCore(Config config) {
        this(new AvroCodecFormat(config));
    }

    public AvroCodecCore() {
        this(new AvroConfigImpl());
    }

    @Override
    public <T> void encode(Class<? super T> clazz, T value, Writer wtr) {
        throw new CodecException("Not supported");
    }

    @Override
    public <T> T decode(Class<? super T> clazz, Reader rdr) {
        throw new CodecException("Not supported");
    }

    /**
     * Encode the given value into byte data and write the results to the {@link OutputStream} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param os        the output stream to which the byte data is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(Class<? super T> type, T value, OutputStream os) {
        try (final OutStream out = AvroTypes.outputOf(os)) {
            encode(type, value, out);
        }
    }
}
