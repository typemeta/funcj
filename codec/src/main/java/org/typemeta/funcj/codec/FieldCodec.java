package org.typemeta.funcj.codec;

import java.lang.reflect.*;
import java.util.Objects;

/**
 * A {@code FieldCodec} encapsulates encoding a field
 * to an encoded type {@code OUT}, and vice-versa from encoded type {@code IN}.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
@SuppressWarnings("unchecked")
public interface FieldCodec<IN, OUT> {

    abstract class Impl<IN, OUT> implements FieldCodec<IN, OUT> {

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

    class BooleanFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.BooleanCodec<IN, OUT> codec;

        BooleanFieldCodec(Field field, Codec.BooleanCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final boolean fieldVal = CodecException.wrap(() -> field.getBoolean(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final boolean fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setBoolean(obj, fieldVal));
            setAccessible(false);
        }
    }

    class BooleanArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<boolean[], IN, OUT> codec;

        BooleanArrayFieldCodec(Field field, Codec<boolean[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final boolean[] fieldVal = CodecException.wrap(() -> (boolean[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final boolean[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ByteFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.ByteCodec<IN, OUT> codec;

        ByteFieldCodec(Field field, Codec.ByteCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final byte fieldVal = CodecException.wrap(() -> field.getByte(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final byte fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setByte(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ByteArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<byte[], IN, OUT> codec;

        ByteArrayFieldCodec(Field field, Codec<byte[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final byte[] fieldVal = CodecException.wrap(() -> (byte[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final byte[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class CharFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.CharCodec<IN, OUT> codec;

        CharFieldCodec(Field field, Codec.CharCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final char fieldVal = CodecException.wrap(() -> field.getChar(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final char fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setChar(obj, fieldVal));
            setAccessible(false);
        }
    }

    class CharArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<char[], IN, OUT> codec;

        CharArrayFieldCodec(Field field, Codec<char[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final char[] fieldVal = CodecException.wrap(() -> (char[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final char[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ShortFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.ShortCodec<IN, OUT> codec;

        ShortFieldCodec(Field field, Codec.ShortCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final short fieldVal = CodecException.wrap(() -> field.getShort(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final short fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setShort(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ShortArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<short[], IN, OUT> codec;

        ShortArrayFieldCodec(Field field, Codec<short[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final short[] fieldVal = CodecException.wrap(() -> (short[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final short[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class IntegerFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.IntCodec<IN, OUT> codec;

        IntegerFieldCodec(Field field, Codec.IntCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final int fieldVal = CodecException.wrap(() -> field.getInt(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        public void decodeField(Object obj, IN in) {
            final int fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setInt(obj, fieldVal));
            setAccessible(false);
        }
    }

    class IntegerArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<int[], IN, OUT> codec;

        IntegerArrayFieldCodec(Field field, Codec<int[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final int[] fieldVal = CodecException.wrap(() -> (int[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final int[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class LongFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.LongCodec<IN, OUT> codec;

        LongFieldCodec(Field field, Codec.LongCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final long fieldVal = CodecException.wrap(() -> field.getLong(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final long fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setLong(obj, fieldVal));
            setAccessible(false);
        }
    }

    class LongArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<long[], IN, OUT> codec;

        LongArrayFieldCodec(Field field, Codec<long[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final long[] fieldVal = CodecException.wrap(() -> (long[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final long[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class FloatFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.FloatCodec<IN, OUT> codec;

        FloatFieldCodec(Field field, Codec.FloatCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final float fieldVal = CodecException.wrap(() -> field.getFloat(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final float fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setFloat(obj, fieldVal));
            setAccessible(false);
        }
    }

    class FloatArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<float[], IN, OUT> codec;

        FloatArrayFieldCodec(Field field, Codec<float[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final float[] fieldVal = CodecException.wrap(() -> (float[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final float[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class DoubleFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec.DoubleCodec<IN, OUT> codec;

        DoubleFieldCodec(Field field, Codec.DoubleCodec<IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final double fieldVal = CodecException.wrap(() -> field.getDouble(obj));
            setAccessible(false);
            return codec.encodePrim(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final double fieldVal = codec.decodePrim(in);
            setAccessible(true);
            CodecException.wrap(() -> field.setDouble(obj, fieldVal));
            setAccessible(false);
        }
    }

    class DoubleArrayFieldCodec<IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<double[], IN, OUT> codec;

        DoubleArrayFieldCodec(Field field, Codec<double[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final double[] fieldVal = CodecException.wrap(() -> (double[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final double[] fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ObjectFieldCodec<T, IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<T, IN, OUT> codec;

        protected ObjectFieldCodec(Field field, Codec<T, IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final T fieldVal = CodecException.wrap(() -> (T) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final T fieldVal;
            fieldVal = codec.decodeWithCheck(in);
            setAccessible(true);
            CodecException.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    class ObjectArrayFieldCodec<T, IN, OUT> extends Impl<IN, OUT> {

        protected final Codec<T[], IN, OUT> codec;

        protected ObjectArrayFieldCodec(Field field, Codec<T[], IN, OUT> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public OUT encodeField(Object obj, OUT out) {
            setAccessible(true);
            final T[] fieldVal;
            fieldVal = CodecException.wrap(() -> (T[]) field.get(obj));
            setAccessible(false);
            return codec.encodeWithCheck(fieldVal, out);
        }

        @Override
        public void decodeField(Object obj, IN in) {
            final T[] fieldVal;
            fieldVal = codec.decodeWithCheck(in);
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
    OUT encodeField(Object obj, OUT out);

    /**
     * Decode a field with an encoded type {@code IN} back into a value of type {@code T}.
     * @param obj       object to which the field belongs
     * @param in        the encoded value
     */
    void decodeField(Object obj, IN in);
}
