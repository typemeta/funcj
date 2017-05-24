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
        protected final CodecCore<E> core;
        protected final Class<T> stcClass;

        protected CodecBase(CodecCore<E> core, Class<T> stcClass) {
            this.core = core;
            this.stcClass = stcClass;
        }
    }

    abstract class DynamicCodec<T, E> extends CodecBase<T, E> {

        public DynamicCodec(CodecCore<E> core, Class<T> stcClass) {
            super(core, stcClass);
        }

        @Override
        public E encode(T val, E out) {
            if (val == null) {
                return core.nullCodec().encode(val, out);
            } else {
                final Codec<Object, E> codec = core.getCodec(stcClass, (Class)val.getClass());
                return codec.encode(val, out);
            }
        }

        @Override
        public T decode(E in) {
            final Class<? extends T> dynClass = getType(in);
            return core.getCodec(stcClass, dynClass).decode(in);
        }

        protected abstract Class<? extends T> getType(E in);
    }

    E encode(T val, E out);
    T decode(E in);
}
