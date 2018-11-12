package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

import java.util.*;

/**
 * Implementations of this interface represent encodings
 * into specific formats, e.g. JSON.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <CFG>     the config type
 */
public interface CodecFormat<IN, OUT, CFG extends CodecConfig> {

    CFG config();

    <T> boolean encodeNull(T val, OUT out);

    boolean decodeNull(IN in);

    <T> boolean encodeDynamicType(
            CodecCoreEx<IN, OUT, CFG> core,
            Codec<T, IN, OUT, CFG> codec,
            T val,
            OUT out,
            Functions.F<Class<T>, Codec<T, IN, OUT, CFG>> getDynCodec);

    default <T> T decodeDynamicType(CodecCoreEx<IN, OUT, CFG> core, IN in) {
        return decodeDynamicType(in, name -> core.getCodec(this.config().<T>nameToClass(name)).decode(core, in));
    }

    <T> T decodeDynamicType(IN in, Functions.F<String, T> decoder);

    Codec.BooleanCodec<IN, OUT, CFG> booleanCodec();

    Codec<boolean[], IN, OUT, CFG> booleanArrayCodec();

    Codec.ByteCodec<IN, OUT, CFG> byteCodec();

    Codec<byte[], IN, OUT, CFG> byteArrayCodec();

    Codec.CharCodec<IN, OUT, CFG> charCodec();

    Codec<char[], IN, OUT, CFG> charArrayCodec();

    Codec.ShortCodec<IN, OUT, CFG> shortCodec();

    Codec<short[], IN, OUT, CFG> shortArrayCodec();

    Codec.IntCodec<IN, OUT, CFG> intCodec();

    Codec<int[], IN, OUT, CFG> intArrayCodec();

    Codec.LongCodec<IN, OUT, CFG> longCodec();

    Codec<long[], IN, OUT, CFG> longArrayCodec();

    Codec.FloatCodec<IN, OUT, CFG> floatCodec();

    Codec<float[], IN, OUT, CFG> floatArrayCodec();

    Codec.DoubleCodec<IN, OUT, CFG> doubleCodec();

    Codec<double[], IN, OUT, CFG> doubleArrayCodec();

    Codec<String, IN, OUT, CFG> stringCodec();

    <EM extends Enum<EM>> Codec<EM, IN, OUT, CFG> enumCodec(Class<EM> enumType);

    <V> Codec<Map<String, V>, IN, OUT, CFG> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, IN, OUT, CFG> valueCodec);

    <K, V> Codec<Map<K, V>, IN, OUT, CFG> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, IN, OUT, CFG> keyCodec,
            Codec<V, IN, OUT, CFG> valueCodec);

    <T> Codec<Collection<T>, IN, OUT, CFG> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, IN, OUT, CFG> elemCodec);

    <T> Codec<T[], IN, OUT, CFG> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, IN, OUT, CFG> elemCodec);

    <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, IN, OUT, CFG> createObjectCodec(
            Class<T> clazz,
            ObjectMeta<T, IN, OUT, RA> objMeta);

}
