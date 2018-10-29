package org.typemeta.funcj.codec;


/**
 * Interface for classes that encapsulates the logic for encoding a value of type {@code T}
 * into an encoded value and vice versa.
 * @param <T>       the raw type to be encoded/decoded
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public interface Codec<T, IN, OUT> {

    abstract class Base<T, IN, OUT> implements Codec<T, IN, OUT> {
        protected final CodecCoreInternal<IN, OUT> core;

        protected Base(CodecCoreInternal<IN, OUT> core) {
            this.core = core;
        }

        @Override
        public CodecCoreInternal<IN, OUT> core() {
            return core;
        }
    }

    /**
     * A sub-interface for codecs for classes which are final.
     * @param <T>       the raw type to be encoded/decoded
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface FinalCodec<T, IN, OUT> extends Codec<T, IN, OUT> {

        @Override
        default OUT encodeWithCheck(T value, OUT out) {
            if (core().encodeNull(value, out)) {
                return out;
            } else {
                return encode(value, out);
            }
        }

        @Override
        default T decodeWithCheck(IN in) {
            if (core().decodeNull(in)) {
                return null;
            } else {
                return decode(in);
            }
        }
    }

    /**
     * Codec for {@link java.lang.Boolean} and {@code boolean} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface BooleanCodec<IN, OUT> extends FinalCodec<Boolean, IN, OUT> {

        @Override
        default Class<Boolean> type() {
            return Boolean.class;
        }

        @Override
        default OUT encode(Boolean value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Boolean decode(IN in) {
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
    interface ByteCodec<IN, OUT> extends FinalCodec<Byte, IN, OUT> {

        @Override
        default Class<Byte> type() {
            return Byte.class;
        }

        @Override
        default OUT encode(Byte value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Byte decode(IN in) {
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
    interface CharCodec<IN, OUT> extends FinalCodec<Character, IN, OUT> {

        @Override
        default Class<Character> type() {
            return Character.class;
        }

        @Override
        default OUT encode(Character value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Character decode(IN in) {
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
    interface ShortCodec<IN, OUT> extends FinalCodec<Short, IN, OUT> {

        @Override
        default Class<Short> type() {
            return Short.class;
        }

        @Override
        default OUT encode(Short value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Short decode(IN in) {
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
    interface IntCodec<IN, OUT> extends FinalCodec<Integer, IN, OUT> {

        @Override
        default Class<Integer> type() {
            return Integer.class;
        }

        @Override
        default OUT encode(Integer value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Integer decode(IN in) {
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
    interface LongCodec<IN, OUT> extends FinalCodec<Long, IN, OUT> {

        @Override
        default Class<Long> type() {
            return Long.class;
        }

        @Override
        default OUT encode(Long value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Long decode(IN in) {
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
    interface FloatCodec<IN, OUT> extends FinalCodec<Float, IN, OUT> {

        @Override
        default Class<Float> type() {
            return Float.class;
        }

        @Override
        default OUT encode(Float value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Float decode(IN in) {
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
    interface DoubleCodec<IN, OUT> extends FinalCodec<Double, IN, OUT> {

        @Override
        default Class<Double> type() {
            return Double.class;
        }

        @Override
        default OUT encode(Double value, OUT out) {
            return encodePrim(value, out);
        }

        @Override
        default Double decode(IN in) {
            return decodePrim(in);
        }

        OUT encodePrim(double val, OUT out);

        double decodePrim(IN in);
    }

    CodecCoreInternal<IN, OUT> core();

    Class<T> type();

    /**
     * Encode a value of type {@code T} into an encoded value of type {@code OUT}.
     * @param value     the unencoded value
     * @param out       the encoded output stream
     * @return          the encoded output stream
     */
    OUT encode(T value, OUT out);

    /**
     * Decode an encoded value of type {@code IN} back into a value of type {@code T}.
     * @param in        the encoded input stream
     * @return          the decoded value
     */
    T decode(IN in);

    /**
     * Encode a value of type {@code T} into an encoded value of type {@code OUT},
     * where the value maybe either null or of a different (sub-) type.
     * @param value     the unencoded value
     * @param out       the encoded output stream
     * @return          the encoded output stream
     */
    default OUT encodeWithCheck(T value, OUT out) {
        if (core().encodeNull(value, out)) {
            return out;
        } else {
            if (!core().encodeDynamicType(this, value, out)) {
                return encode(value, out);
            } else {
                return out;
            }
        }
    }

    /**
     * Decode an encoded value of type {@code IN} back into a value of type {@code T},
     * where the unencoded value maybe either null or of a different (sub-) type..
     * @param in        the encoded input stream
     * @return          the decoded value
     */
    default T decodeWithCheck(IN in) {
        if (core().decodeNull(in)) {
            return null;
        } else {
            final T value = core().decodeDynamicType(in);
            if (value != null) {
                return value;
            } else {
                return decode(in);
            }
        }
    }
}
