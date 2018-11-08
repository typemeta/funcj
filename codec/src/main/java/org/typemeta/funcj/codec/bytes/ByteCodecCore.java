package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.CodecCoreInternal;
import org.typemeta.funcj.codec.bytes.io.ByteIO;
import org.typemeta.funcj.codec.bytes.io.ByteIO.Input;
import org.typemeta.funcj.codec.bytes.io.ByteIO.Output;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for classes which implement an encoding via byte streams.
 */
public interface ByteCodecCore extends CodecCoreInternal<Input, Output> {
    /**
     * Encode the supplied value into byte data and write the results to the {@link OutputStream} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param os        the output stream to which the byte data is written
     * @param <T>       the static type of the value
     */
    default <T> void encode(Class<? super T> type, T value, OutputStream os) {
        encode(type, value, ByteIO.outputOf(os));
    }
    /**
     * Encode the supplied value into byte data and write the results to the {@link OutputStream} object.
     * @param value     the value to be encoded
     * @param os        the output stream to which the byte data is written
     * @param <T>       the static type of the value
     */
    default <T> void encode(T value, OutputStream os) {
        encode(Object.class, value, os);
    }

    /**
     * Decode a value by reading byte data from the supplied {@link InputStream} object.
     * @param type      the static type of the value to be decoded.
     * @param is        the input stream from which byte data is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    default <T> T decode(Class<T> type, InputStream is) {
        return decode(type, ByteIO.inputOf(is));
    }

    /**
     * Decode a value by reading byte data from the supplied {@link InputStream} object.
     * @param is        the input stream from which byte data is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    default <T> T decode(InputStream is) {
        return decode(Object.class, ByteIO.inputOf(is));
    }
}
