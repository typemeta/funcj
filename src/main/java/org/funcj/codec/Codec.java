package org.funcj.codec;

import java.util.Map;

public interface Codec<T, E> {

    interface NullCodec<E> extends Codec<Object, E> {
        boolean isNull(E in);
    }

    abstract class BooleanCodec<E> implements Codec<Boolean, E> {

        @Override
        public E encode(Boolean val, E out) {
            return encodePrim(val, out) ;
        }

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

        public Integer decode(E in) {
            return decodePrim(in);
        }

        abstract E encodePrim(int val, E out);

        abstract int decodePrim(E in);
    }

    abstract class CodecBase<T, E> implements Codec<T, E> {
        protected final Class<T> stcClass;

        protected CodecBase(Class<T> stcClass) {
            this.stcClass = stcClass;
        }
    }

    E encode(T val, E out);
    T decode(E in);
}
