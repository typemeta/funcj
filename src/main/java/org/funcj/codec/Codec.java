package org.funcj.codec;

public interface Codec<T, E> {

    interface NullCodec<E> extends Codec<Object, E> {
        boolean isNull(E in);
    }

    abstract class BooleanCodec<E> implements Codec<Boolean, E> {

        @Override
        public E encode(Boolean val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Boolean decode(Class<Boolean> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Boolean decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(boolean val, E out);

        public abstract boolean decodePrim(E in);
    }


    abstract class ByteCodec<E> implements Codec<Byte, E> {

        @Override
        public E encode(Byte val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Byte decode(Class<Byte> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Byte decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(byte val, E out);

        public abstract byte decodePrim(E in);
    }

    abstract class CharCodec<E> implements Codec<Character, E> {

        @Override
        public E encode(Character val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Character decode(Class<Character> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Character decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(char val, E out);

        public abstract char decodePrim(E in);
    }

    abstract class ShortCodec<E> implements Codec<Short, E> {

        @Override
        public E encode(Short val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Short decode(Class<Short> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Short decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(short val, E out);

        public abstract short decodePrim(E in);
    }

    abstract class IntCodec<E> implements Codec<Integer, E> {

        @Override
        public E encode(Integer val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Integer decode(Class<Integer> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Integer decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(int val, E out);

        public abstract int decodePrim(E in);
    }

    abstract class LongCodec<E> implements Codec<Long, E> {

        @Override
        public E encode(Long val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Long decode(Class<Long> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Long decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(long val, E out);

        public abstract long decodePrim(E in);
    }

    abstract class FloatCodec<E> implements Codec<Float, E> {

        @Override
        public E encode(Float val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Float decode(Class<Float> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Float decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(float val, E out);

        public abstract float decodePrim(E in);
    }

    abstract class DoubleCodec<E> implements Codec<Double, E> {

        @Override
        public E encode(Double val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Double decode(Class<Double> dynType, E in) {
            return decodePrim(in);
        }

        @Override
        public Double decode(E in) {
            return decodePrim(in);
        }

        public abstract E encodePrim(double val, E out);

        public abstract double decodePrim(E in);
    }

    E encode(T val, E out);

    default T decode(Class<T> dynType, E in) {
        return decode(in);
    }

    default T decode(E in) {
        throw new CodecException("Operation not implemented");
    }

}
