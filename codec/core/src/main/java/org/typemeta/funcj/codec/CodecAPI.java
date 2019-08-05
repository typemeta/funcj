package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

public interface CodecAPI {
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
}
