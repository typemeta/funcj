package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.bytes.ByteTypes.*;
import org.typemeta.funcj.codec.impl.CodecCoreImpl;

import java.io.*;

/**
 * Interface for classes which implement an encoding via byte streams.
 */
public class ByteCodecCore
        extends CodecCoreDelegate<InStream, OutStream, Config>
        implements CodecAPI.IO {

    public ByteCodecCore(ByteCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public ByteCodecCore(Config config) {
        this(new ByteCodecFormat(config));
    }

    public ByteCodecCore() {
        this(new ByteConfig());
    }

    /**
     * Encode the given value into byte data and write the results to the {@link OutputStream} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param os        the output stream to which the byte data is written
     * @param <T>       the static type of the value
     * @return          the output stream
     */
    public <T> OutputStream encode(Class<? super T> type, T value, OutputStream os) {
        encodeImpl(type, value, ByteTypes.outputOf(os));
        return os;
    }

    /**
     * Decode a value by reading byte data from the given {@link InputStream} object.
     * @param type      the static type of the value to be decoded.
     * @param is        the input stream from which byte data is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    @Override
    public <T> T decode(Class<? super T> type, InputStream is) {
        return decodeImpl(type, ByteTypes.inputOf(is));
    }
}
