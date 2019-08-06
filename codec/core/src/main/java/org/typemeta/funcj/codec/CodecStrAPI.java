package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.CodecException;

import java.io.*;

/**
 * Simple common API for encoding/decoding values via Java streams.
 * Implementation classes may extend this API by adding further encoding/decoding methods
 * that are encoding-specific.
 * @param <IS>          the input stream type, typical either {@link Reader} or {@link InputStream}
 * @param <OS>          the output stream type, typical either {@link Writer} or {@link OutputStream}
 */
public interface CodecStrAPI<IS, OS> extends CodecAPI {

    /**
     * Encode a value of type {@code T} into an {@link OS}.
     * @param clazz     the class of the decoded value
     * @param value     the value to encode
     * @param os        the output stream
     * @param <T>       the decoded value type
     * @return          the writer
     */
    <T> OS encode(Class<? super T> clazz, T value, OS os);

    /**
     * Decode a value of type {@code T} from an {@link IS}.
     * @param clazz     the type of the decoded value
     * @param is        the input stream
     * @param <T>       the decoded value type
     * @return          the decoded value
     */
    <T> T decode(Class<? super T> clazz, IS is);

    /**
     * Encode the given value into JSON and write the results to the {@link OS} object.
     * @param value     the value to be encoded
     * @param os        the output stream to which the JSON is written
     * @return          the writer
     */
    default OS encode(Object value, OS os) {
        return encode(Object.class, value, os);
    }

    /**
     * Decode a value of type {@code T} from an {@link IS}.
     * @param is        the input stream
     * @return          the decoded value
     */
    default Object decode(IS is) {
        return decode(Object.class, is);
    }

    /**
     * Specialisation of {@code CodecAPI} for {@link InputStream} and {@link OutputStream}
     */
    interface IO extends CodecStrAPI<InputStream, OutputStream> {
    }

    /**
     * Specialisation of {@code CodecAPI} for {@link Reader} and {@link Writer}
     */
    interface RW extends CodecStrAPI<Reader, Writer> {

        /**
         * Encode a value of type {@code T} into an {@link OutputStream}.
         * The {@code clazz} parameter indicates the expected type when the data is decoded.
         * If the type of the {@code value} differs then dynamic type information is added.
         * type
         * @param clazz     the class of the decoded value
         * @param value     the value to encode
         * @param os        the output stream
         * @param <T>       the decoded value type
         * @return          the output stream
         */
        default <T> OutputStream encode(Class<? super T> clazz, T value, OutputStream os) {
            try {
                final Writer wtr = new BufferedWriter(new OutputStreamWriter(os));
                encode(clazz, value, wtr);
                wtr.flush();
                return os;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
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
            final Reader rdr = new BufferedReader(new InputStreamReader(is));
            return decode(clazz, rdr);
        }

        /**
         * Encode a value of type {@code T} into an {@link OutputStream}.
         * @param value     the value to encode
         * @param os        the output stream
         * @return          the output stream
         */
        default OutputStream encode(Object value, OutputStream os) {
            return encode(Object.class, value, os);
        }

        /**
         * Decode a value of type {@code T} from an {@link InputStream}.
         * @param is        the input stream
         * @return          the decoded value
         */
        default Object decode(InputStream is) {
            return decode(Object.class, is);
        }
    }
}
