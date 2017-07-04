package org.funcj.codec;

import org.funcj.util.Functions;
import org.funcj.util.Functions.F;

import java.time.*;

public class Codecs {
    public static <E> void registerAll(CodecCore<E> core) {
        core.registerCodec(Class.class, new ClassCodec<E>(core));
        core.registerCodec(LocalDate.class, new LocalDateCodec<E>(core));
        core.registerCodec(OffsetDateTime.class, new OffsetDateTimeCodec<E>(core));
    }

    public static abstract class CodecBase<T, E> implements Codec<T, E> {

        protected final CodecCore<E> core;

        protected CodecBase(CodecCore<E> core) {
            this.core = core;
        }
    }

    public static class ClassCodec<E> extends CodecBase<Class, E> {

        protected ClassCodec(CodecCore<E> core) {
            super(core);
        }

        @Override
        public E encode(Class val, E enc) {
            return core.stringCodec().encode(core.classToName(val), enc);
        }

        @Override
        public Class decode(E enc) {
            return core.nameToClass(core.stringCodec().decode(enc));
        }
    }

    public static class StringProxyCodec<T, E> extends CodecBase<T, E> {

        protected final F<String, T> parser;

        public StringProxyCodec(CodecCore<E> core, F<String, T> parser) {
            super(core);
            this.parser = parser;
        }

        @Override
        public E encode(T val, E enc) {
            return core.stringCodec().encode(val.toString(), enc);
        }

        @Override
        public T decode(E enc) {
            return parser.apply(core.stringCodec().decode(enc));
        }
    }

    public static class LocalDateCodec<E> extends StringProxyCodec<LocalDate, E> {
        public LocalDateCodec(CodecCore<E> core) {
            super(core, LocalDate::parse);
        }
    }

    public static class OffsetDateTimeCodec<E> extends StringProxyCodec<OffsetDateTime, E> {
        public OffsetDateTimeCodec(CodecCore<E> core) {
            super(core, OffsetDateTime::parse);
        }
    }
}
