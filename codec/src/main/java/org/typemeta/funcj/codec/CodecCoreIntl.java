package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.*;

/**
 * Internal interface for {@link CodecCore} implementations.
 * @param <E>       the encoded type
 */
public interface CodecCoreIntl<E> extends CodecCore<E> {

    String classToName(Class<?> clazz);

    <X> Class<X> remapType(Class<X> type);

    <T> Class<T> nameToClass(String name) throws CodecException;

    <T> TypeConstructor<T> getTypeConstructor(Class<T> clazz);

    <T> Codec<T, E> makeNullSafeCodec(Codec<T, E> codec);

    Codec.NullCodec<E> nullCodec();

    Codec.BooleanCodec<E> booleanCodec();

    Codec<boolean[], E> booleanArrayCodec();

    Codec.ByteCodec<E> byteCodec();

    Codec<byte[], E> byteArrayCodec();

    Codec.CharCodec<E> charCodec();

    Codec<char[], E> charArrayCodec();

    Codec.ShortCodec<E> shortCodec();

    Codec<short[], E> shortArrayCodec();

    Codec.IntCodec<E> intCodec();

    Codec<int[], E> intArrayCodec();

    Codec.LongCodec<E> longCodec();

    Codec<long[], E> longArrayCodec();

    Codec.FloatCodec<E> floatCodec();

    Codec<float[], E> floatArrayCodec();

    Codec.DoubleCodec<E> doubleCodec();

    Codec<double[], E> doubleArrayCodec();

    Codec<String, E> stringCodec();

    <EM extends Enum<EM>> Codec<EM, E> enumCodec(Class<? super EM> enumType);

    <K, V> Codec<Map<K, V>, E> mapCodec(Class<K> keyType, Class<V> valType);

    <V> Codec<Map<String, V>, E> mapCodec(Codec<V, E> valueCodec);

    <K, V> Codec<Map<K, V>, E> mapCodec(
            Codec<K, E> keyCodec,
            Codec<V, E> valueCodec);

    <T> Codec<Collection<T>, E> collCodec(
            Class<T> elemType,
            Codec<T, E> elemCodec);

    <T> Codec<T[], E> objectArrayCodec(
            Class<T> elemType,
            Codec<T, E> elemCodec);

    Codec<Object, E> dynamicCodec();

    <T> Codec<T, E> dynamicCodec(Class<T> stcType);

    <T> Codec<T, E> dynamicCodec(Codec<T, E> codec, Class<T> stcType);

    <T> Codec<T, E> getNullSafeCodec(Class<T> type);

    <T> Codec<T, E> getCodec(String name, Functions.F0<Codec<T, E>> codecSupp);

    <T> Codec<T, E> getNullUnsafeCodec(Class<T> type);

    <T> Codec<T, E> getNullUnsafeCodecImplDyn(Class<T> dynType);

    <T> Codec<T, E> getNullUnsafeCodecImplStc(Class<T> stcType);

    <T> Codec<T, E> getNullUnsafeCodecImpl(Class<T> type);

    <T> Codec<T, E> createObjectCodec(Class<T> type);

    <T> Codec<T, E> createObjectCodec(Map<String, FieldCodec<E>> fieldCodecs);

    <T> ObjectCodecBuilder<T, E> objectCodec(Class<T> clazz);

    <T> ObjectCodecBuilder<T, E> objectCodecDeferredRegister(Class<T> clazz);

    <T> Codec<T, E> createObjectCodec(
            Map<String, ObjectCodecBuilder.FieldCodec<T, E>> fieldCodecs,
            Functions.F<Object[], T> ctor);

    <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, E> createObjectCodec(ObjectMeta<T, E, RA> objMeta);

    String getFieldName(Field field, int depth, Set<String> existingNames);

    <T> FieldCodec<E> getFieldCodec(Field field);

    <T> Codec<T, E> dynamicCheck(Codec<T, E> codec, Class<T> stcType);

    abstract class ObjectMeta<T, E, RA extends CodecCoreIntl.ObjectMeta.ResultAccumlator<T>>
            implements Iterable<CodecCoreIntl.ObjectMeta.Field<T, E, RA>> {
        public interface ResultAccumlator<T> {
            T construct();
        }

        public interface Field<T, E, RA> {
            String name();
            E encodeField(T val, E enc) throws Exception;
            RA decodeField(RA acc, E enc) throws Exception;
        }

        public abstract RA startDecode(Class<T> type) throws CodecException;

        public Stream<BaseCodecCore.ObjectMeta.Field<T, E, RA>> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        public abstract int size();
    }
}
