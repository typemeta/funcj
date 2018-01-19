package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecCore;
import org.typemeta.funcj.json.model.JsValue;

/**
 * Interface for classes which implement an encoding into JSON,
 * via the {@link JsValue} representation for JSON values.
 */
@SuppressWarnings("unchecked")
public interface JsonCodecCore extends CodecCore<JsValue> {

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param val   the value to encode
     * @param <T>   the decoded value type
     * @return      the encoded value
     * @throws Exception if the operation fails
     */
    default <T> JsValue encode(T val) throws Exception {
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
    default <T> JsValue encode(Class<T> type, T val) throws Exception {
        return encode(type, val, null);
    }
}
