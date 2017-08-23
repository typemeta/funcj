package org.funcj.codec.json;

import org.funcj.codec.*;
import org.funcj.json.JSValue;

/**
 * Interface for classes which implement an encoding into JSON,
 * via the {@link JSValue} representation for JSON values.
 */
public interface JsonCodecCore extends CodecCore<JSValue> {

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param val the value to encode
     * @param <T> the decoded value type
     * @return the encoded value
     */
    default <T> JSValue encode(T val) {
        return encode((Class<T>)val.getClass(), val);
    }

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param type the class of the decoded value
     * @param val the value to encode
     * @param <T> the decoded value type
     * @return the encoded value
     */
    default <T> JSValue encode(Class<T> type, T val) {
        return encode(type, val, null);
    }
}
