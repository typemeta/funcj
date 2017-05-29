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
        public Boolean decode(E in) {
            return decodePrim(in);
        }

        abstract E encodePrim(boolean val, E out);

        abstract boolean decodePrim(E in);
    }

    interface BooleanArrayCodec<E> extends Codec<boolean[], E> {
        E encode(boolean[] vals, E out);
        boolean[] decode(E in);
    }

    abstract class IntegerCodec<E> implements Codec<Integer, E> {

        @Override
        public E encode(Integer val, E out) {
            return encodePrim(val, out) ;
        }

        @Override
        public Integer decode(E in) {
            return decodePrim(in);
        }

        abstract E encodePrim(int val, E out);

        abstract int decodePrim(E in);
    }

    interface IntegerArrayCodec<E> extends Codec<int[], E> {
        E encode(int[] vals, E out);
        int[] decode(E in);
    }

    E encode(T val, E out);

    default T decode(Class<T> dynType, E in) {
        return decode(in);
    }

    default T decode(E in) {
        throw new IllegalStateException();
    }
}
