package org.typemeta.funcj.codec;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * A {@code FieldCodec} encapsulates encoding a field
 * to an encoded type {@code OUT}, and vice-versa from encoded type {@code IN}.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <CFG>     the config type
 */
@SuppressWarnings("unchecked")
public interface FieldCodec<IN, OUT, CFG extends CodecConfig> {

    abstract class Impl<IN, OUT, CFG extends CodecConfig> implements FieldCodec<IN, OUT, CFG> {

        protected final Field field;

        protected final boolean isAccessible;

        protected Impl(Field field) {
            this.field = field;
            this.isAccessible = Modifier.isFinal(field.getModifiers()) ||
                    !Modifier.isPublic(field.getModifiers());
        }

        protected void setAccessible(boolean flag) {
            if (isAccessible) {
                field.setAccessible(flag);
            }
        }
    }

    class BooleanFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.BooleanCodec<IN, OUT, CFG> codec;

        BooleanFieldCodec(Field field, Codec.BooleanCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final boolean fieldVal = CodecException.wrap(() -> field.getBoolean(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final boolean fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setBoolean(obj, fieldVal));
            setAccessible(false);
        }
    }

    class BooleanArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<boolean[], IN, OUT, CFG> codec;

        BooleanArrayFieldCodec(Field field, Codec<boolean[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final boolean[] fieldVal = CodecException.wrap(() -> (boolean[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final boolean[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ByteFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.ByteCodec<IN, OUT, CFG> codec;

        ByteFieldCodec(Field field, Codec.ByteCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final byte fieldVal = CodecException.wrap(() -> field.getByte(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final byte fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setByte(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ByteArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<byte[], IN, OUT, CFG> codec;

        ByteArrayFieldCodec(Field field, Codec<byte[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final byte[] fieldVal = CodecException.wrap(() -> (byte[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final byte[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class CharFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.CharCodec<IN, OUT, CFG> codec;

        CharFieldCodec(Field field, Codec.CharCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final char fieldVal = CodecException.wrap(() -> field.getChar(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final char fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setChar(obj, fieldVal));
            setAccessible(false);
        }
    }

    class CharArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<char[], IN, OUT, CFG> codec;

        CharArrayFieldCodec(Field field, Codec<char[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final char[] fieldVal = CodecException.wrap(() -> (char[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final char[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ShortFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.ShortCodec<IN, OUT, CFG> codec;

        ShortFieldCodec(Field field, Codec.ShortCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final short fieldVal = CodecException.wrap(() -> field.getShort(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final short fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setShort(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ShortArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<short[], IN, OUT, CFG> codec;

        ShortArrayFieldCodec(Field field, Codec<short[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final short[] fieldVal = CodecException.wrap(() -> (short[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final short[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class IntegerFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.IntCodec<IN, OUT, CFG> codec;

        IntegerFieldCodec(Field field, Codec.IntCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final int fieldVal = CodecException.wrap(() -> field.getInt(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final int fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setInt(obj, fieldVal));
            setAccessible(false);
        }
    }

    class IntegerArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<int[], IN, OUT, CFG> codec;

        IntegerArrayFieldCodec(Field field, Codec<int[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final int[] fieldVal = CodecException.wrap(() -> (int[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final int[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class LongFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.LongCodec<IN, OUT, CFG> codec;

        LongFieldCodec(Field field, Codec.LongCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final long fieldVal = CodecException.wrap(() -> field.getLong(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final long fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setLong(obj, fieldVal));
            setAccessible(false);
        }
    }

    class LongArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<long[], IN, OUT, CFG> codec;

        LongArrayFieldCodec(Field field, Codec<long[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final long[] fieldVal = CodecException.wrap(() -> (long[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final long[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class FloatFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.FloatCodec<IN, OUT, CFG> codec;

        FloatFieldCodec(Field field, Codec.FloatCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final float fieldVal = CodecException.wrap(() -> field.getFloat(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final float fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setFloat(obj, fieldVal));
            setAccessible(false);
        }
    }

    class FloatArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<float[], IN, OUT, CFG> codec;

        FloatArrayFieldCodec(Field field, Codec<float[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final float[] fieldVal = CodecException.wrap(() -> (float[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final float[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class DoubleFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec.DoubleCodec<IN, OUT, CFG> codec;

        DoubleFieldCodec(Field field, Codec.DoubleCodec<IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final double fieldVal = CodecException.wrap(() -> field.getDouble(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final double fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setDouble(obj, fieldVal));
            setAccessible(false);
        }
    }

    class DoubleArrayFieldCodec<IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<double[], IN, OUT, CFG> codec;

        DoubleArrayFieldCodec(Field field, Codec<double[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final double[] fieldVal = CodecException.wrap(() -> (double[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final double[] fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ObjectFieldCodec<T, IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<T, IN, OUT, CFG> codec;

        protected ObjectFieldCodec(Field field, Codec<T, IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final T fieldVal = CodecException.wrap(() -> (T) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final T fieldVal;
            fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ObjectArrayFieldCodec<T, IN, OUT, CFG extends CodecConfig> extends Impl<IN, OUT, CFG> {

        protected final Codec<T[], IN, OUT, CFG> codec;

        protected ObjectArrayFieldCodec(Field field, Codec<T[], IN, OUT, CFG> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out) {
            setAccessible(true);
            final T[] fieldVal;
            fieldVal = CodecException.wrap(() -> (T[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(core, fieldVal, out);
        }

        @Override
        public void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in) {
            final T[] fieldVal;
            fieldVal = codec.decodeWithCheck(core, in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }


    /**
     * Encode a field within an object into an encoded type {@code OUT}.
     * @param obj       the object to which the field belongs
     * @param out       the encoded parent value
     * @return          the encoded value
     */
    OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, OUT out);

    /**
     * Decode a field with an encoded type {@code IN} back into a value of type {@code T}.
     * @param obj       object to which the field belongs
     * @param in        the encoded value
     */
    void decodeField(CodecCoreEx<IN, OUT, CFG> core, Object obj, IN in);
}
