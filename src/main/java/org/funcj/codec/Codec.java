package org.funcj.codec;

import org.funcj.codec.utils.Utils;

/**
 * Interface for classes that encapsulates the logic for encoding a value of type <code>T</code>
 * into a value of type <code>E</code> and vice versa.
 * @param <T> type to be encoded/decoded
 * @param <E> encoded type
 */
public interface Codec<T, E> {

    /**
     * Codec for null values.
     * @param <E> encoded type
     */
    interface NullCodec<E> extends Codec<Object, E> {
        /**
         * Check whether and encoded value represents a null value.
         * @param enc encoded value
         * @return true if encoded value represents a null value
         */
        boolean isNull(E enc);
    }

    /**
     * Codec for {@link java.lang.Boolean} and <code>boolean</code> values.
     * @param <E> encoded type
     */
    abstract class BooleanCodec<E> implements Codec<Boolean, E> {

        @Override
        public E encode(Boolean val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Boolean decode(Class<Boolean> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Boolean decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(boolean val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(boolean val) {
            throw Utils.opnNotImplError();
        }

        public abstract boolean decodePrim(E enc);
    }

    /**
     * Codec for {@link java.lang.Byte} and <code>byte</code> values.
     * @param <E> encoded type
     */
    abstract class ByteCodec<E> implements Codec<Byte, E> {

        @Override
        public E encode(Byte val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Byte decode(Class<Byte> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Byte decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(byte val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(byte val) {
            throw Utils.opnNotImplError();
        }

        public abstract byte decodePrim(E enc);
    }

    /**
     * Codec for {@link java.lang.Character} and <code>char</code> values.
     * @param <E> encoded type
     */
    abstract class CharCodec<E> implements Codec<Character, E> {

        @Override
        public E encode(Character val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Character decode(Class<Character> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Character decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(char val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(char val) {
            throw Utils.opnNotImplError();
        }

        public abstract char decodePrim(E enc);
    }

    /**
     * Codec for {@link java.lang.Short} and <code>short</code> values.
     * @param <E> encoded type
     */
    abstract class ShortCodec<E> implements Codec<Short, E> {

        @Override
        public E encode(Short val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Short decode(Class<Short> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Short decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(short val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(short val) {
            throw Utils.opnNotImplError();
        }

        public abstract short decodePrim(E enc);
    }

    /**
     * Codec for {@link java.lang.Integer} and <code>int</code> values.
     * @param <E> encoded type
     */
    abstract class IntCodec<E> implements Codec<Integer, E> {

        @Override
        public E encode(Integer val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Integer decode(Class<Integer> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Integer decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(int val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(int val) {
            throw Utils.opnNotImplError();
        }

        public abstract int decodePrim(E enc);
    }

    /**
     * Codec for {@link java.lang.Long} and <code>long</code> values.
     * @param <E> encoded type
     */
    abstract class LongCodec<E> implements Codec<Long, E> {

        @Override
        public E encode(Long val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Long decode(Class<Long> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Long decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(long val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(long val) {
            throw Utils.opnNotImplError();
        }

        public abstract long decodePrim(E enc);
    }

    /**
     * Codec for {@link java.lang.Float} and <code>float</code> values.
     * @param <E> encoded type
     */
    abstract class FloatCodec<E> implements Codec<Float, E> {

        @Override
        public E encode(Float val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Float decode(Class<Float> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Float decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(float val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(float val) {
            throw Utils.opnNotImplError();
        }

        public abstract float decodePrim(E enc);
    }

    /**
     * Codec for {@link java.lang.Double} and <code>double</code> values.
     * @param <E> encoded type
     */
    abstract class DoubleCodec<E> implements Codec<Double, E> {

        @Override
        public E encode(Double val, E enc) {
            return encodePrim(val, enc) ;
        }

        @Override
        public Double decode(Class<Double> dynType, E enc) {
            return decodePrim(enc);
        }

        @Override
        public Double decode(E enc) {
            return decodePrim(enc);
        }

        public E encodePrim(double val, E enc) {
            return encodePrim(val);
        }

        public E encodePrim(double val) {
            throw Utils.opnNotImplError();
        }

        public abstract double decodePrim(E enc);
    }

    /**
     * Encode a value of type <code>T</code> into and encoded value of type <code>E</code>.
     * @param val unencoded value
     * @param enc encoded parent value
     * @return encoded value
     */
    E encode(T val, E enc) ;

    /**
     * Decode a value of type <code>E</code> back into a value of type <code>T</code>.
     * One of the two <code>decode</code> methods must be implemented by sub-classes.
     * @param dynType the dynamic type to decode into.
     * @param enc the encoded value
     * @return decoded value
     */
    default T decode(Class<T> dynType, E enc) {
        return decode(enc);
    }

    /**
     * Decode an encoded value of type <code>E</code> back into a value of type <code>T</code>.
     * One of the two <code>decode</code> methods must be implemented by sub-classes.
     * @param enc the encoded value
     * @return decoded value
     */
    default T decode(E enc) {
        throw Utils.opnNotImplError();
    }
}
