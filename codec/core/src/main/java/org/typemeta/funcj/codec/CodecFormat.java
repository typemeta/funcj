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
public interface CodecFormat<
        IN extends CodecFormat.Input<IN>,
        OUT extends CodecFormat.Output<OUT>,
        CFG extends CodecConfig
        > {

    /**
     * An abstraction for codec input, typically a stream of tokens.
     * @param <IN>      the input type
     */
    interface Input<IN extends Input<IN>> extends AutoCloseable {

        @Override
        default void close() {
        }

        boolean readBoolean();
        char readChar();
        byte readByte();
        short readShort();
        int readInt();
        long readLong();
        float readFloat();
        double readDouble();
        String readString();
    }

    /**
     * An abstraction for codec output, typically a stream of tokens.
     * @param <OUT>     the output type
     */
    interface Output<OUT extends Output<OUT>> extends AutoCloseable {

        @Override
        default void close() {
        }

        OUT writeBoolean(boolean value);
        OUT writeChar(char value);
        OUT writeByte(byte value);
        OUT writeShort(short value);
        OUT writeInt(int value);
        OUT writeLong(long value);
        OUT writeFloat(float value);
        OUT writeDouble(double value);
        OUT writeString(String value);
    }

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
        return decodeDynamicType(
                in,
                name -> core.getCodec(this.config().<T>nameToClass(name)).decode(core, in)
        );
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

    default <EM extends Enum<EM>> Codec<EM, IN, OUT, CFG> enumCodec(Class<EM> enumType) {
        return new Codec<EM, IN, OUT, CFG>() {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public OUT encode(CodecCoreEx<IN, OUT, CFG> core, EM value, OUT out) {
                return out.writeString(value.name());
            }

            @Override
            public EM decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
                return EM.valueOf(type(), in.readString());
            }
        };
    }

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
