package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.CodecCoreIntl;
import org.typemeta.funcj.codec.byteio.ByteIO.Input;
import org.typemeta.funcj.codec.byteio.ByteIO.Output;

/**
 * Interface for classes which implement an encoding into a byte stream.
 */
@SuppressWarnings("unchecked")
public interface ByteCodecCore extends CodecCoreIntl<Input, Output> {

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param val   the value to encode
     * @param <T>   the decoded value type
     * @return      the encoded value
     * @throws Exception if the operation fails
     */
    default <T> Output encode(T val) {
        return encode((Class<T>)val.getClass(), val);
    }

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param type  the class of the decoded value
     * @param val   the value to encode
     * @param <T>   the decoded value type
     * @return      the encoded value
     * @throws Exception if the operation fails
     */
    default <T> Output encode(Class<T> type, T val) {
        return encode(type, val, null);
    }
}
