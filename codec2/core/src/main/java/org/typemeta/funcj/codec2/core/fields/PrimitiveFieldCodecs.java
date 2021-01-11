package org.typemeta.funcj.codec2.core.fields;

import org.typemeta.funcj.codec2.core.Codec;
import org.typemeta.funcj.codec2.core.Context;
import org.typemeta.funcj.codec2.core.DecoderCore;
import org.typemeta.funcj.codec2.core.EncoderCore;
import org.typemeta.funcj.codec2.core.PrimitiveCodecs.BooleanCodec;
import org.typemeta.funcj.codec2.core.PrimitiveCodecs.IntegerCodec;
import org.typemeta.funcj.codec2.core.utils.CodecException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

public abstract class PrimitiveFieldCodecs {
    private PrimitiveFieldCodecs() {}

    abstract static class AbstractImpl<T, IN, OUT> implements FieldCodec<T, IN, OUT> {

        protected final Field field;

        protected final boolean isAccessible;

        protected AbstractImpl(Field field) {
            this.field = field;
            this.isAccessible =
                    Modifier.isFinal(field.getModifiers()) ||
                            !Modifier.isPublic(field.getModifiers());
        }

        protected void setAccessible(boolean flag) {
            if (isAccessible) {
                field.setAccessible(flag);
            }
        }
    }

    public static class BooleanFieldCodec<T, IN, OUT> extends AbstractImpl<T, IN, OUT> {

        protected final BooleanCodec<IN, OUT> codec;

        protected BooleanFieldCodec(Field field, BooleanCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(EncoderCore<OUT> core, Context ctx, T source, OUT out) {
            setAccessible(true);
            final boolean fieldVal = CodecException.wrap(() -> field.getBoolean(source));
            setAccessible(false);
            return codec.encodeBool(core, ctx, fieldVal, out);
        }

        @Override
        public void decodeField(DecoderCore<IN> core, Context ctx, T target, IN in) {
            final boolean fieldVal = codec.decodeBool(core, ctx, in);
            setAccessible(true);
            CodecException.wrap(() -> field.setBoolean(target, fieldVal));
            setAccessible(false);
        }
    }
//
//    public static class BooleanArrayFieldCodec<T, IN, OUT> extends AbstractImpl<T, IN, OUT> {
//
//        protected final Codec<boolean[], IN, OUT> codec;
//
//        protected BooleanArrayFieldCodec(Field field, Codec<boolean[], IN, OUT> codec) {
//            super(field);
//            this.codec = Objects.requireNonNull(codec);
//        }
//
//        @Override
//        public OUT encodeField(EncoderCore<OUT> core, Context ctx, T source, OUT out) {
//            setAccessible(true);
//            final boolean[] fieldVal = CodecException.wrap(() -> (boolean[])field.get(source));
//            setAccessible(false);
//            return codec.encode(core, ctx, fieldVal, out);
//        }
//
//        @Override
//        public void decodeField(DecoderCore<IN> core, Context ctx, T target, IN in) {
//            final boolean[] fieldVal = codec.decode(core, ctx, in);
//            setAccessible(true);
//            CodecException.wrap(() -> field.set(target, fieldVal));
//            setAccessible(false);
//        }
//    }
//
    public static class IntegerFieldCodec<T, IN, OUT> extends AbstractImpl<T, IN, OUT> {

        protected final IntegerCodec<IN, OUT> codec;

        protected IntegerFieldCodec(Field field, IntegerCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(EncoderCore<OUT> core, Context ctx, T source, OUT out) {
            setAccessible(true);
            final int fieldVal = CodecException.wrap(() -> field.getInt(source));
            setAccessible(false);
            return codec.encodeInt(core, ctx, fieldVal, out);
        }

        @Override
        public void decodeField(DecoderCore<IN> core, Context ctx,T target, IN in) {
            final int fieldVal = codec.decodeInt(core, ctx, in);
            setAccessible(true);
            CodecException.wrap(() -> field.setInt(target, fieldVal));
            setAccessible(false);
        }
    }

    public static class ObjectFieldCodec<T, FT, IN, OUT> extends AbstractImpl<T, IN, OUT> {

        protected final Codec<FT, IN, OUT> codec;

        protected ObjectFieldCodec(Field field, Codec<FT, IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(EncoderCore<OUT> core, Context ctx, T source, OUT out) {
            setAccessible(true);
            final FT fieldVal = CodecException.wrap(() -> (FT)field.get(source));
            setAccessible(false);
            return codec.encode(core, ctx, fieldVal, out);
        }

        @Override
        public void decodeField(DecoderCore<IN> core, Context ctx, T target, IN in) {
            final FT fieldVal = codec.decode(core, ctx, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(target, fieldVal));
            setAccessible(false);
        }
    }
}
