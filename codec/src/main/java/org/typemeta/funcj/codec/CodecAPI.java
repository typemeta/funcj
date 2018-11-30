package org.typemeta.funcj.codec;

import java.io.*;

/**
 * Simple common API for encoding/decoding values via Java streams.
 * Implementation classes may extend this API by adding further encoding/decoding methods
 * that are encoding-specific.
 */
public interface CodecAPI {

    /**
     * Encode a value of type {@code T} into an {@link Writer}.
     * @param clazz     the class of the decoded value
     * @param value     the value to encode
     * @param wtr       the writer
     * @param <T>       the decoded value type
     */
    <T> void encode(Class<? super T> clazz, T value, Writer wtr);

    /**
     * Decode a value of type {@code T} from an {@link Reader}.
     * @param clazz     the type of the decoded value
     * @param rdr       the reader
     * @param <T>       the decoded value type
     * @return          the decoded value
     */
    <T> T decode(Class<? super T> clazz, Reader rdr);

    /**
     * Encode the given value into JSON and write the results to the {@link Writer} object.
     * @param value     the value to be encoded
     * @param writer    the output stream to which the JSON is written
     */
    default void encode(Object value, Writer writer) {
        encode(Object.class, value, writer);
    }

    /**
     * Decode a value of type {@code T} from an {@link Reader}.
     * @param rdr       the reader
     * @return          the decoded value
     */
    default Object decode(Reader rdr) {
        return decode(Object.class, rdr);
    }

    /**
     * Encode a value of type {@code T} into an {@link OutputStream}.
     * The {@code clazz} parameter indicates the expected type when the data is decoded.
     * If the type of the {@code value} differs then dynamic type information is added.
     * type
     * @param clazz     the class of the decoded value
     * @param value     the value to encode
     * @param os        the output stream
     * @param <T>       the decoded value type
     */
    default <T> void encode(Class<? super T> clazz, T value, OutputStream os) {
        encode(clazz, value, new OutputStreamWriter(os));
    }

    /**
     * Decode a value of type {@code T} from an {@link InputStream}.
     * The {@code clazz} parameter indicates the expected type when the data is decoded.
     * @param clazz     the type of the decoded value
     * @param is        the input stream
     * @param <T>       the decoded value type
     * @return          the decoded value
     */
    default <T> T decode(Class<? super T> clazz, InputStream is) {
        return decode(clazz, new InputStreamReader(is));
    }

    /**
     * Encode a value of type {@code T} into an {@link OutputStream}.
     * @param value     the value to encode
     * @param os        the output stream
     */
    default void encode(Object value, OutputStream os) {
        encode(Object.class, value, new OutputStreamWriter(os));
    }

    /**
     * Decode a value of type {@code T} from an {@link InputStream}.
     * @param is        the input stream
     * @return          the decoded value
     */
    default Object decode(InputStream is) {
        return decode(Object.class, new InputStreamReader(is));
    }
}
