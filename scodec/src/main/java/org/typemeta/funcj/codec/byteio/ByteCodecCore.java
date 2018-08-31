package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.CodecCoreIntl;

/**
 * Interface for classes which implement an encoding into a byte stream.
 */
@SuppressWarnings("unchecked")
public interface ByteCodecCore extends CodecCoreIntl<ByteIO.Input, ByteIO.Output> {

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param val   the value to encode
     * @param <T>   the decoded value type
     * @return      the encoded value
     * @throws Exception if the operation fails
     */
    default <T> ByteIO.Output encode(T val) throws Exception {
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
    default <T> ByteIO.Output encode(Class<T> type, T val) throws Exception {
        return encode(type, val, null);
    }
}
