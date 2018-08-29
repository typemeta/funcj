package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

/**
 * Interface for classes which providing an encoding into a specific target type {@code E}.
 * Unlike {@link Codec}, which can only encode a single type,
 * {@code CodecCore} implementations will handle any type.
 * @param <IN, OUT>       the encoded type
 */
public interface CodecCore<IN, OUT> {

    /**
     * Register a {@code Codec} for a class.
     * @param clazz     the class to register codec against
     * @param codec     the codec
     * @param <T>       the codec value type
     */
    <T> void registerCodec(Class<? extends T> clazz, Codec<T, IN, OUT> codec);

    /**
     * Register a {@code Codec} for a class.
     * @param name      name of class to register codec against
     * @param codec     the codec
     * @param <T>       the codec value type
     */
    <T> void registerCodec(String name, Codec<T, IN, OUT> codec);

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
     * Register a type proxy.
     * A type proxy maps a type to its proxy before selecting its {@code Codec}.
     * @param type      type to be mapped
     * @param proxyType proxy type
     */
    void registerTypeProxy(Class<?> type, Class<?> proxyType);

    /**
     * Register a type proxy.
     * A type proxy maps a type to its proxy before selecting its {@code Codec}.
     * @param typeName  name of type to be mapped
     * @param proxyType proxy type
     */
    void registerTypeProxy(String typeName, Class<?> proxyType);

    /**
     * Register a {@code TypeConstructor} for the specified class.
     * @param clazz     the class to register the {@code TypeConstructor} against
     * @param typeCtor  the {@code TypeConstructor}
     * @param <T>       the type constructed by the {@code TypeConstructor}
     */
    <T> void registerTypeConstructor(
            Class<? extends T> clazz,
            TypeConstructor<T> typeCtor);

    /**
     * Encode a non-null value of type {@code T} into encoded form {@code E}
     * @param val       the value to encode
     * @param enc       the encoded parent (may be null for certain encodings)
     * @param <T>       the decoded value type
     * @return          the encoded value
     * @throws Exception the exception
     */
    <T> IN encode(T val, IN enc) throws Exception;

    /**
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param type      the class of the decoded value
     * @param val       the value to encode
     * @param enc       the encoded parent (may be null for certain encodings)
     * @param <T>       the decoded value type
     * @return          the encoded value
     * @throws Exception the exception
     */
    <T> IN encode(Class<T> type, T val, IN enc) throws Exception;

    /**
     * Decode a value of type {@code T} from encoded value of type {@code E}.
     * @param type      the type of the decoded value
     * @param enc       the value to decode
     * @param <T>       the decoded value type
     * @return          the decoded value
     * @throws Exception the exception
     */
    <T> T decode(Class<T> type, IN enc) throws Exception;
}
