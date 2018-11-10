package org.typemeta.funcj.codec;


/**
 * Interface for classes that encapsulates the logic for encoding a value of type {@code T}
 * into an encoded value and vice versa.
 * @param <T>       the raw type to be encoded/decoded
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public interface Codec<T, IN, OUT, CFG extends CodecConfig> {

    /**
     * A sub-interface for codecs for classes which are final.
     * @param <T>       the raw type to be encoded/decoded
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     * @param <CFG>     the config type
     */
    interface FinalCodec<T, IN, OUT, CFG extends CodecConfig> extends Codec<T, IN, OUT, CFG> {

        @Override
        default OUT encodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, T value, OUT out) {
            if (core.format().encodeNull(value, out)) {
                return out;
            } else {
                return encode(core, value, out);
            }
        }

        @Override
        default T decodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            if (core.format().decodeNull(in)) {
                return null;
            } else {
                return decode(core, in);
            }
        }
    }

    /**
     * Codec for {@link java.lang.Boolean} and {@code boolean} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface BooleanCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Boolean, IN, OUT, CFG> {

        @Override
        default Class<Boolean> type() {
            return Boolean.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Boolean value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Boolean decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(boolean val, OUT out);

        boolean decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Byte} and {@code byte} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface ByteCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Byte, IN, OUT, CFG> {

        @Override
        default Class<Byte> type() {
            return Byte.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Byte value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Byte decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(byte val, OUT out);

        byte decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Character} and {@code char} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface CharCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Character, IN, OUT, CFG> {

        @Override
        default Class<Character> type() {
            return Character.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Character value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Character decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(char val, OUT out);

        char decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Short} and {@code short} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface ShortCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Short, IN, OUT, CFG> {

        @Override
        default Class<Short> type() {
            return Short.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Short value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Short decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(short val, OUT out);

        short decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Integer} and {@code int} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface IntCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Integer, IN, OUT, CFG> {

        @Override
        default Class<Integer> type() {
            return Integer.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Integer value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Integer decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(int val, OUT out);

        int decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Long} and {@code long} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface LongCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Long, IN, OUT, CFG> {

        @Override
        default Class<Long> type() {
            return Long.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Long value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Long decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(long val, OUT out);

        long decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Float} and {@code float} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface FloatCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Float, IN, OUT, CFG> {

        @Override
        default Class<Float> type() {
            return Float.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Float value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Float decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(float val, OUT out);

        float decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Double} and {@code double} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface DoubleCodec<IN, OUT, CFG extends CodecConfig> extends FinalCodec<Double, IN, OUT, CFG> {

        @Override
        default Class<Double> type() {
            return Double.class;
        }

        @Override
        default OUT encode(CodecCoreEx<IN, OUT, CFG> core, Double value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Double decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(double value, OUT out);

        double decodePrim(IN in);
    }

    Class<T> type();

    /**
     * Encode a value of type {@code T} into an encoded value of type {@code OUT}.
     *
     * @param core      the codec core
     * @param value     the unencoded value
     * @param out       the encoded output stream
     * @return          the encoded output stream
     */
    OUT encode(CodecCoreEx<IN, OUT, CFG> core, T value, OUT out);

    /**
     * Decode an encoded value of type {@code IN} back into a value of type {@code T}.
     *
     * @param core      the codec core
     * @param in        the encoded input stream
     * @return          the decoded value
     */
    T decode(CodecCoreEx<IN, OUT, CFG> core, IN in);

    /**
     * Encode a value of type {@code T} into an encoded value of type {@code OUT},
     * where the value maybe either null or of a different (sub-) type.
     * @param core      the codec core
     * @param value     the unencoded value
     * @param out       the encoded output stream
     * @return          the encoded output stream
     */
    default OUT encodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, T value, OUT out) {
        if (core.format().encodeNull(value, out)) {
            return out;
        } else {
            if (!core.encodeDynamicType(this, value, out)) {
                return encode(core, value, out);
            } else {
                return out;
            }
        }
    }

    /**
     * Decode an encoded value of type {@code IN} back into a value of type {@code T},
     * where the unencoded value maybe either null or of a different (sub-) type.
     * @param core      the codec core
     * @param in        the encoded input stream
     * @return          the decoded value
     */
    default T decodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, IN in) {
        if (core.format().decodeNull(in)) {
            return null;
        } else {
            final T value = core.decodeDynamicType(in);
            if (value != null) {
                return value;
            } else {
                return decode(core, in);
            }
        }
    }
}
