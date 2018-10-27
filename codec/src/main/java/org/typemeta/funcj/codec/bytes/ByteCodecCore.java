package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.CodecCoreInternal;
import org.typemeta.funcj.codec.bytes.io.ByteIO;
import org.typemeta.funcj.codec.bytes.io.ByteIO.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via byte streams.
 */
public interface ByteCodecCore extends CodecCoreInternal<Input, Output> {
    default <T> void encode(
            Class<T> type,
            T value,
            OutputStream os) {
        encode(type, value, ByteIO.outputOf(os));
    }

    default <T> T decode(
            Class<T> type,
            InputStream is) {
        return decode(type, ByteIO.inputOf(is));
    }
}
