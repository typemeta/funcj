package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.OperationNotImplementedException;

/**
 * Interface for classes that encapsulates the logic for encoding a value of type {@code T}
 * into a value of type {@code IN} and vice versa.
 * @param <T>       the raw type to be encoded/decoded
 * @param <IN, OUT>       the encoded type
 */
public interface Codec<T, IN, OUT> {

    /**
     * Codec for null values.
     * @param <IN, OUT>       the encoded type
     */
    interface NullCodec<IN, OUT> extends Codec<Object, IN, OUT> {
        /**
         * Check whether an encoded value represents a null value.
         * @param in       encoded value
         * @return          true if encoded value represents a null value
         * @throws Exception if the operation fails
         */
        boolean isNull(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Boolean} and {@code boolean} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class BooleanCodec<IN, OUT> implements Codec<Boolean, IN, OUT> {

        @Override
        public IN encode(Boolean val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Boolean decode(Class<Boolean> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Boolean decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(boolean val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(boolean val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract boolean decodePrim(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Byte} and {@code byte} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class ByteCodec<IN, OUT> implements Codec<Byte, IN, OUT> {

        @Override
        public IN encode(Byte val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Byte decode(Class<Byte> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Byte decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(byte val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(byte val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract byte decodePrim(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Character} and {@code char} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class CharCodec<IN, OUT> implements Codec<Character, IN, OUT> {

        @Override
        public IN encode(Character val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Character decode(Class<Character> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Character decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(char val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(char val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract char decodePrim(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Short} and {@code short} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class ShortCodec<IN, OUT> implements Codec<Short, IN, OUT> {

        @Override
        public IN encode(Short val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Short decode(Class<Short> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Short decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(short val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(short val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract short decodePrim(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Integer} and {@code int} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class IntCodec<IN, OUT> implements Codec<Integer, IN, OUT> {

        @Override
        public IN encode(Integer val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Integer decode(Class<Integer> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Integer decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(int val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(int val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract int decodePrim(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Long} and {@code long} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class LongCodec<IN, OUT> implements Codec<Long, IN, OUT> {

        @Override
        public IN encode(Long val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Long decode(Class<Long> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Long decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(long val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(long val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract long decodePrim(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Float} and {@code float} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class FloatCodec<IN, OUT> implements Codec<Float, IN, OUT> {

        @Override
        public IN encode(Float val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Float decode(Class<Float> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Float decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(float val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(float val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract float decodePrim(IN in) throws Exception;
    }

    /**
     * Codec for {@link java.lang.Double} and {@code double} values.
     * @param <IN, OUT>       the encoded type
     */
    abstract class DoubleCodec<IN, OUT> implements Codec<Double, IN, OUT> {

        @Override
        public IN encode(Double val, IN in) throws Exception {
            return encodePrim(val, in) ;
        }

        @Override
        public Double decode(Class<Double> dynType, IN in) throws Exception {
            return decodePrim(in);
        }

        @Override
        public Double decode(IN in) throws Exception {
            return decodePrim(in);
        }

        public IN encodePrim(double val, IN in) throws Exception {
            return encodePrim(val);
        }

        public IN encodePrim(double val) throws Exception {
            throw new OperationNotImplementedException();
        }

        public abstract double decodePrim(IN in) throws Exception;
    }

    /**
     * Encode a value of type {@code T} into nd encoded value of type {@code IN}.
     * @param val       the unencoded value
     * @param in       the encoded parent value
     * @return          the encoded value
     * @throws Exception  if the operation fails
     */
    IN encode(T val, IN in) throws Exception;

    /**
     * Decode a value of type {@code IN} back into a value of type {@code T}.
     * One of the two {@code decode} methods must be implemented by sub-classes.
     * @param dynType   the dynamic type to decode into.
     * @param in       the encoded value
     * @return          the decoded value
     * @throws Exception  if the operation fails
     */
    default T decode(Class<T> dynType, IN in) throws Exception {
        return decode(in);
    }

    /**
     * Decode an encoded value of type {@code IN} back into a value of type {@code T}.
     * One of the two {@code decode} methods must be implemented by sub-classes.
     * @param in       the encoded value
     * @return          the decoded value
     * @throws Exception  if the operation fails
     */
    default T decode(IN in) throws Exception {
        throw new OperationNotImplementedException();
    }
}
