package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.CodecCoreIntl;

import java.io.*;

/**
 * Interface for classes which implement an encoding into JSON.
 */
public interface ByteCodecCore extends CodecCoreIntl<ByteIO.Input, ByteIO.Output> {
    default <T> void encode(
            Class<T> type,
            T value,
            OutputStream os) {
        encode(type, value, new DataOutputStream(os));
    }

    default <T> T decode(
            Class<T> type,
            InputStream is) {
        return decode(type, new DataInputStream(is));
    }
}
