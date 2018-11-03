package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

/**
 * Interface for classes which provide an encoding of values of any type,
 * into a specific target type.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public interface CodecCore<IN, OUT> {

    /**
     * Return the config object associated with this {@code CodecCore}.
     * @return          the config object associated with this {@code CodecCore}
     */
    CodecConfig config();

    /**
     * Register a {@code Codec} for a class.
     * @param clazz     the class to register codec against
     * @param codec     the codec
     * @param <T>       the codec value type
     */
    <T> void registerCodec(Class<? extends T> clazz, Codec<T, IN, OUT> codec);

    /**
     * Create a {@code ObjectCodecBuilder} for the specified class.
     * <p>
     * Create a {@code ObjectCodecBuilder}, essentially a fluent interface
     * for creating and registering a {@code Codec}.
     * @param clazz     the class to register codec against
     * @param <T>       the codec value type
     * @return          an {@code ObjectCodecBuilder}
     */
    <T> ObjectCodecBuilder<T, IN, OUT> registerCodec(Class<T> clazz);

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
     * Register a {@code TypeConstructor} for the specified class.
     * @param clazz     the class to register the {@code TypeConstructor} against
     * @param typeCtor  the {@code TypeConstructor}
     * @param <T>       the type constructed by the {@code TypeConstructor}
     */
    <T> void registerTypeConstructor(
            Class<? extends T> clazz,
            NoArgsCtor<T> typeCtor);

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param type      the class of the decoded value
     * @param val       the value to encode
     * @param out       the encoded parent (may be null for certain encodings)
     * @param <T>       the decoded value type
     * @return          the encoded value
     */
    <T> OUT encode(Class<? super T> type, T val, OUT out);

    /**
     * Decode a value of type {@code T} from encoded value of type {@code E}.
     * @param type      the type of the decoded value
     * @param in        the value to decode
     * @param <T>       the decoded value type
     * @return          the decoded value
     */
    <T> T decode(Class<? super T> type, IN in);
}
