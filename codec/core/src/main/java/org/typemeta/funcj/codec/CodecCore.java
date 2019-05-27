package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.bytes.ArgMapTypeCtor;
import org.typemeta.funcj.functions.Functions;

import java.util.*;

/**
 * Interface for classes which provide an encoding of values of any type,
 * into a specific target type.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <CFG>     the config type
 */
public interface CodecCore<IN, OUT, CFG extends CodecConfig> {

    CodecCoreEx<IN, OUT, CFG> getCodecCoreEx();

    /**
     * Return the config object associated with this {@code CodecCore}.
     * @return          the config object associated with this {@code CodecCore}
     */
    CFG config();

    /**
     * Register a {@code Codec} for a class.
     * @param clazz     the class to register codec against
     * @param codec     the codec
     * @param <T>       the codec value type
     */
    <T> void registerCodec(Class<? extends T> clazz, Codec<T, IN, OUT, CFG> codec);

    /**
     * Create a {@code ObjectCodecBuilderWithArgArray} for the specified class.
     * <p>
     * Create a {@code ObjectCodecBuilderWithArgArray}, essentially a fluent interface
     * for creating and registering a {@code Codec}.
     * @param clazz     the class to register codec against
     * @param <T>       the codec value type
     * @return          an {@code ObjectCodecBuilder}
     */
    <T> ObjectCodecBuilderWithArgArray<T, IN, OUT, CFG> registerCodecWithArgArray(Class<T> clazz);

    /**
     * Create a {@code ObjectCodecBuilderWithArgMap} for the specified class.
     * <p>
     * Create a {@code ObjectCodecBuilderWithArgMap}, essentially a fluent interface
     * for creating and registering a {@code Codec}.
     * @param clazz     the class to register codec against
     * @param <T>       the codec value type
     * @return          an {@code ObjectCodecBuilder}
     */
    <T> ObjectCodecBuilderWithArgMap<T, IN, OUT, CFG> registerCodecWithArgMap(Class<T> clazz);

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
     * Encode a value of type {@code T} into encoded form {@code E}.
     * @param clazz     the class of the decoded value
     * @param value     the value to encode
     * @param out       the encoded parent (may be null for certain encodings)
     * @param <T>       the decoded value type
     * @return          the encoded value
     */
    <T> OUT encode(Class<? super T> clazz, T value, OUT out);

    /**
     * Decode a value of type {@code T} from encoded value of type {@code E}.
     * @param clazz     the type of the decoded value
     * @param in        the input to decode
     * @param <T>       the decoded value type
     * @return          the decoded value
     */
    <T> T decode(Class<? super T> clazz, IN in);

    /**
     * Lookup a {@code Codec} for a name, and, if one doesn't exist,
     * then create a new one.
     * @param clazz     the class
     * @param <T>       the raw type to be encoded/decoded
     * @return          the {@code Codec} for the specified name
     */
    <T> Codec<T, IN, OUT, CFG> getCodec(Class<T> clazz);


    <T> Codec<Collection<T>, IN, OUT, CFG> getCollCodec(
            Class<Collection<T>> collType,
            Class<T> elemType);

    <K, V> Codec<Map<K, V>, IN, OUT, CFG> getMapCodec(
            Class<Map<K, V>> mapType,
            Class<K> keyType,
            Class<V> valType);
}
