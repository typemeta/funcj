package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.OperationNotImplementedException;

/**
 * Interface for classes that encapsulates the logic for encoding a value of type {@code T}
 * into an encoded value and vice versa.
 * @param <T>       the raw type to be encoded/decoded
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public interface Codec<T, IN, OUT> {

    /**
     * Codec for null values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    interface NullCodec<IN, OUT> extends Codec<Object, IN, OUT> {
        /**
         * Check whether an encoded value represents a null value.
         * @param in        encoded value
         * @return          true if encoded value represents a null value
         */
        boolean isNull(IN in);
    }

    /**
     * Codec for {@link java.lang.Boolean} and {@code boolean} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class BooleanCodec<IN, OUT> implements Codec<Boolean, IN, OUT> {

        @Override
        public OUT encode(Boolean val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Boolean decode(Class<Boolean> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Boolean decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(boolean val, OUT out);

        public abstract boolean decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Byte} and {@code byte} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class ByteCodec<IN, OUT> implements Codec<Byte, IN, OUT> {

        @Override
        public OUT encode(Byte val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Byte decode(Class<Byte> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Byte decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(byte val, OUT out);

        public abstract byte decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Character} and {@code char} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class CharCodec<IN, OUT> implements Codec<Character, IN, OUT> {

        @Override
        public OUT encode(Character val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Character decode(Class<Character> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Character decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(char val, OUT out);

        public abstract char decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Short} and {@code short} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class ShortCodec<IN, OUT> implements Codec<Short, IN, OUT> {

        @Override
        public OUT encode(Short val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Short decode(Class<Short> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Short decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(short val, OUT out);

        public abstract short decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Integer} and {@code int} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class IntCodec<IN, OUT> implements Codec<Integer, IN, OUT> {

        @Override
        public OUT encode(Integer val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Integer decode(Class<Integer> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Integer decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(int val, OUT out);

        public abstract int decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Long} and {@code long} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class LongCodec<IN, OUT> implements Codec<Long, IN, OUT> {

        @Override
        public OUT encode(Long val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Long decode(Class<Long> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Long decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(long val, OUT out);

        public abstract long decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Float} and {@code float} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class FloatCodec<IN, OUT> implements Codec<Float, IN, OUT> {

        @Override
        public OUT encode(Float val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Float decode(Class<Float> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Float decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(float val, OUT out);

        public abstract float decodePrim(IN in);
    }

    /**
     * Codec for {@link java.lang.Double} and {@code double} values.
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    abstract class DoubleCodec<IN, OUT> implements Codec<Double, IN, OUT> {

        @Override
        public OUT encode(Double val, OUT out) {
            return encodePrim(val, out);
        }

        @Override
        public Double decode(Class<Double> dynType, IN in) {
            return decodePrim(in);
        }

        @Override
        public Double decode(IN in) {
            return decodePrim(in);
        }

        public abstract OUT encodePrim(double val, OUT out);

        public abstract double decodePrim(IN in);
    }

    /**
     * Encode a value of type {@code T} into an encoded value of type {@code OUT}.
     * @param val       the unencoded value
     * @param out       the encoded parent value
     * @return          the encoded value
     */
    OUT encode(T val, OUT out);

    /**
     * Decode a value of type {@code IN} back into a value of type {@code T}.
     * One of the two {@code decode} methods must be implemented by sub-classes.
     * @param dynType   the dynamic type to decode into.
     * @param in        the encoded value
     * @return          the decoded value
     */
    default T decode(Class<T> dynType, IN in) {
        return decode(in);
    }

    /**
     * Decode an encoded value of type {@code IN} back into a value of type {@code T}.
     * One of the two {@code decode} methods must be implemented by sub-classes.
     * @param in        the encoded value
     * @return          the decoded value
     */
    default T decode(IN in) {
        throw new OperationNotImplementedException();
    }
}
