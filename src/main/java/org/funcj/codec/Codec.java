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

    interface IntegerArrayCodec<E> extends Codec<int[], E> {
        E encode(int[] vals, E out);
        int[] decode(E in);
    }

    abstract class DynamicCodec<T, E> implements Codec<T, E> {

        protected final Class<T> stcClass;
        protected final CodecCore<E> core;

        public DynamicCodec(Class<T> stcClass, CodecCore<E> core) {
            this.stcClass = stcClass;
            this.core = core;
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

    interface ObjectArrayCodec<T, E> extends Codec<T[], E> {
    }

    E encode(T val, E out);
    T decode(E in);
}
