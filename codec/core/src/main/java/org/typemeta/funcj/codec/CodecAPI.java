package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.functions.Functions;

import java.io.*;

/**
 * Simple common API for encoding/decoding values via Java streams.
 * Implementation classes may extend this API by adding further encoding/decoding methods
 * that are encoding-specific.
 * @param <INS>         the input stream type, typical either {@link Reader} or {@link InputStream}
 * @param <OUTS>        the output stream type, typical either {@link Writer} or {@link OutputStream}
 */
public interface CodecAPI<INS, OUTS> {

    /**
     * Return the config object associated with this {@code CodecCore}.
     * @return          the config object associated with this {@code CodecCore}
     */
    CodecConfig config();

    /**
     * Create and register a {@link Codecs.StringProxyCodec} for a class.
     * @param clazz     the class to register codec against
     * @param encode    a function to encode a value of type {@code T} as a {@link String}
     * @param decode    a function to decode a {@code String} back into a value of type {@code T}
     * @param <T>       the codec value type
     */
    <T> void registerStringProxyCodec(
            Class<T> clazz,
            Functions.F<T, String> encode,
            Functions.F<String, T> decode);

    /**
     * Register a {@link NoArgsTypeCtor} for the specified class.
     * @param clazz     the class to register the {@code NoArgsTypeCtor} against
     * @param typeCtor  the {@code NoArgsTypeCtor}
     * @param <T>       the type constructed by the {@code NoArgsTypeCtor}
     */
    <T> void registerNoArgsCtor(
            Class<? extends T> clazz,
            NoArgsTypeCtor<T> typeCtor);

    /**
     * Register a {@link ArgArrayTypeCtor} for the specified class.
     * @param clazz     the class to register the {@code ArgArrayTypeCtor} against
     * @param typeCtor  the {@code ArgArrayTypeCtor}
     * @param <T>       the type constructed by the {@code ArgArrayTypeCtor}
     */
    <T> void registerArgArrayCtor(
            Class<? extends T> clazz,
            ArgArrayTypeCtor<T> typeCtor);

    /**
     * Register a {@link ArgMapTypeCtor} for the specified class.
     * @param clazz     the class to register the {@code ArgMapTypeCtor} against
     * @param typeCtor  the {@code ArgMapTypeCtor}
     * @param <T>       the type constructed by the {@code ArgMapTypeCtor}
     */
    <T> void registerArgMapTypeCtor(
            Class<? extends T> clazz,
            ArgMapTypeCtor<T> typeCtor);

    /**
     * Encode a value of type {@code T} into an {@link OUTS}.
     * @param clazz     the class of the decoded value
     * @param value     the value to encode
     * @param wtr       the writer
     * @param <T>       the decoded value type
     * @return          the writer
     */
    <T> OUTS encode(Class<? super T> clazz, T value, OUTS wtr);

    /**
     * Decode a value of type {@code T} from an {@link INS}.
     * @param clazz     the type of the decoded value
     * @param rdr       the reader
     * @param <T>       the decoded value type
     * @return          the decoded value
     */
    <T> T decode(Class<? super T> clazz, INS rdr);

    /**
     * Encode the given value into JSON and write the results to the {@link OUTS} object.
     * @param value     the value to be encoded
     * @param writer    the output stream to which the JSON is written
     * @return          the writer
     */
    default OUTS encode(Object value, OUTS writer) {
        return encode(Object.class, value, writer);
    }

    /**
     * Decode a value of type {@code T} from an {@link INS}.
     * @param rdr       the reader
     * @return          the decoded value
     */
    default Object decode(INS rdr) {
        return decode(Object.class, rdr);
    }

    /**
     * Specialisation of {@code CodecAPI} for {@link InputStream} and {@link OutputStream}
     */
    interface IO extends CodecAPI<InputStream, OutputStream> {
    }

    /**
     * Specialisation of {@code CodecAPI} for {@link Reader} and {@link Writer}
     */
    interface RW extends CodecAPI<Reader, Writer> {

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
