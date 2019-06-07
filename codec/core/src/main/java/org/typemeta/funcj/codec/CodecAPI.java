package org.typemeta.funcj.codec;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Simple common API for encoding/decoding values via Java streams.
 * Implementation classes may extend this API by adding further encoding/decoding methods
 * that are encoding-specific.
 * @param <IN>          the input stream type, typical either {@link Reader} or {@link InputStream}
 * @param <OUT>         the output stream type, typical either {@link Writer} or {@link OutputStream}
 */
public interface CodecAPI<IN, OUT> {

    /**
     * Encode a value of type {@code T} into an {@link OUT}.
     * @param clazz     the class of the decoded value
     * @param value     the value to encode
     * @param wtr       the writer
     * @param <T>       the decoded value type
     * @return          the writer
     */
    <T> OUT encode(Class<? super T> clazz, T value, OUT wtr);

    /**
     * Decode a value of type {@code T} from an {@link IN}.
     * @param clazz     the type of the decoded value
     * @param rdr       the reader
     * @param <T>       the decoded value type
     * @return          the decoded value
     */
    <T> T decode(Class<? super T> clazz, IN rdr);

    /**
     * Encode the given value into JSON and write the results to the {@link OUT} object.
     * @param value     the value to be encoded
     * @param writer    the output stream to which the JSON is written
     * @return          the writer
     */
    default OUT encode(Object value, OUT writer) {
        return encode(Object.class, value, writer);
    }

    /**
     * Decode a value of type {@code T} from an {@link IN}.
     * @param rdr       the reader
     * @return          the decoded value
     */
    default Object decode(IN rdr) {
        return decode(Object.class, rdr);
    }
}
