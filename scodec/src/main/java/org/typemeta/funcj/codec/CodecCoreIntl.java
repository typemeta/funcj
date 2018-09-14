package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Internal interface for {@link CodecCore} implementations.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public interface CodecCoreIntl<IN, OUT> extends CodecCore<IN, OUT> {

    String classToName(Class<?> clazz);

    <X> Class<X> remapType(Class<X> type);

    <T> Class<T> nameToClass(String name);

    <T> TypeConstructor<T> getTypeConstructor(Class<T> clazz);

    <T> Codec<T, IN, OUT> makeNullSafeCodec(Codec<T, IN, OUT> codec);

    Codec.NullCodec<IN, OUT> nullCodec();

    Codec.BooleanCodec<IN, OUT> booleanCodec();

    Codec<boolean[], IN, OUT> booleanArrayCodec();

    Codec.ByteCodec<IN, OUT> byteCodec();

    Codec<byte[], IN, OUT> byteArrayCodec();

    Codec.CharCodec<IN, OUT> charCodec();

    Codec<char[], IN, OUT> charArrayCodec();

    Codec.ShortCodec<IN, OUT> shortCodec();

    Codec<short[], IN, OUT> shortArrayCodec();

    Codec.IntCodec<IN, OUT> intCodec();

    Codec<int[], IN, OUT> intArrayCodec();

    Codec.LongCodec<IN, OUT> longCodec();

    Codec<long[], IN, OUT> longArrayCodec();

    Codec.FloatCodec<IN, OUT> floatCodec();

    Codec<float[], IN, OUT> floatArrayCodec();

    Codec.DoubleCodec<IN, OUT> doubleCodec();

    Codec<double[], IN, OUT> doubleArrayCodec();

    Codec<String, IN, OUT> stringCodec();

    <EM extends Enum<EM>> Codec<EM, IN, OUT> enumCodec(Class<? super EM> enumType);

    <K, V> Codec<Map<K, V>, IN, OUT> mapCodec(Class<K> keyType, Class<V> valType);

    <V> Codec<Map<String, V>, IN, OUT> mapCodec(Codec<V, IN, OUT> valueCodec);

    <K, V> Codec<Map<K, V>, IN, OUT> mapCodec(
            Codec<K, IN, OUT> keyCodec,
            Codec<V, IN, OUT> valueCodec);

    <T> Codec<Collection<T>, IN, OUT> collCodec(
            Class<T> elemType,
            Codec<T, IN, OUT> elemCodec);

    <T> Codec<T[], IN, OUT> objectArrayCodec(
            Class<T> elemType,
            Codec<T, IN, OUT> elemCodec);

    Codec<Object, IN, OUT> dynamicCodec();

    <T> Codec<T, IN, OUT> dynamicCodec(Class<T> stcType);

    <T> Codec<T, IN, OUT> dynamicCodec(Codec<T, IN, OUT> codec, Class<T> stcType);

    <T> Codec<T, IN, OUT> getNullSafeCodecDyn(Class<T> type);

    <T> Codec<T, IN, OUT> getCodec(String name, Functions.F0<Codec<T, IN, OUT>> codecSupp);

    <T> Codec<T, IN, OUT> getNullUnsafeCodecDyn(Class<T> type);

    <T> Codec<T, IN, OUT> createNullUnsafeCodecDyn(Class<T> dynType);

    <T> Codec<T, IN, OUT> createNullUnsafeCodecStc(Class<T> stcType);

    <T> Codec<T, IN, OUT> createNullUnsafeCodec(Class<T> type);

    <T> Codec<T, IN, OUT> createObjectCodec(Class<T> type);

    <T> Codec<T, IN, OUT> createObjectCodec(Map<String, FieldCodec<IN, OUT>> fieldCodecs);

    <T> ObjectCodecBuilder<T, IN, OUT> objectCodec(Class<T> clazz);

    <T> ObjectCodecBuilder<T, IN, OUT> objectCodecDeferredRegister(Class<T> clazz);

    <T> Codec<T, IN, OUT> createObjectCodec(
            Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT>> fieldCodecs,
            Functions.F<Object[], T> ctor);

    <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, IN, OUT> createObjectCodec(ObjectMeta<T, IN, OUT, RA> objMeta);

    String getFieldName(Field field, int depth, Set<String> existingNames);

    <T> FieldCodec<IN, OUT> createFieldCodec(Field field);

    <T> Codec<T, IN, OUT> dynamicCheck(Codec<T, IN, OUT> codec, Class<T> stcType);

    abstract class ObjectMeta<T, IN, OUT, RA extends CodecCoreIntl.ObjectMeta.ResultAccumlator<T>>
            implements Iterable<CodecCoreIntl.ObjectMeta.Field<T, IN, OUT, RA>> {
        public interface ResultAccumlator<T> {
            T construct();
        }

        public interface Field<T, IN, OUT, RA> {
            String name();
            OUT encodeField(T val, OUT out);
            RA decodeField(RA acc, IN in);
        }

        public abstract RA startDecode(Class<T> type);

        public Stream<BaseCodecCore.ObjectMeta.Field<T, IN, OUT, RA>> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        public abstract int size();
    }
}
