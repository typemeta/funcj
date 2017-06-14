package org.funcj.codec;

public class Codecs {
    public static abstract class CodecBase<T, E> implements Codec<T, E> {

        protected final CodecCore<E> core;

        protected CodecBase(CodecCore<E> core) {
            this.core = core;
        }
    }

    public static class ClassCodec<T, E> extends CodecBase<Class<T>, E> {

        protected ClassCodec(CodecCore<E> core) {
            super(core);
        }

        @Override
        public E encode(Class<T> val, E enc) {
            return core.stringCodec().encode(core.classToName(val), enc);
        }

        @Override
        public Class<T> decode(E enc) {
            return core.nameToClass(core.stringCodec().decode(enc));
        }
    }
}
