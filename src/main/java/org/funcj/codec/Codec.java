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

    interface BooleanArrayCodec<E> {
        E encode(boolean[] vals, E out);
        boolean[] decode(E in);
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

    abstract class ObjectArrayCodec<T, E> implements Codec<T[], E> {
        private final Codec<T, E> elemCodec;

        protected ObjectArrayCodec(Codec<T, E> elemCodec) {
            this.elemCodec = elemCodec;
        }
    }

    E encode(T val, E out);
    T decode(E in);
}
