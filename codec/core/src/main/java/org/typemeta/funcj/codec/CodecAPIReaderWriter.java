package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.CodecException;

import java.io.*;

public interface CodecAPIReaderWriter extends CodecAPI<Reader, Writer> {

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
        try(Writer wtr = new BufferedWriter(new OutputStreamWriter(os))) {
            encode(clazz, value, wtr);
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
        try(Reader rdr = new BufferedReader(new InputStreamReader(is))){
            return decode(clazz, rdr);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
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
