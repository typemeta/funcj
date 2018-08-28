package org.typemeta.funcj.codec;

import java.lang.reflect.*;
import java.util.Objects;

/**
 * A {@code FieldCodec} encapsulates encoding a field
 * to an encoded type {@code IN}, and vice-versa.
 * @param <IN, OUT>       the encoded type
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final boolean fieldVal = field.getBoolean(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final boolean fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setBoolean(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final boolean[] fieldVal = (boolean[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final boolean[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final byte fieldVal = field.getByte(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final byte fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setByte(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final byte[] fieldVal = (byte[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final byte[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final char fieldVal = field.getChar(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final char fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setChar(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final char[] fieldVal = (char[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final char[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final short fieldVal = field.getShort(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final short fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setShort(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final short[] fieldVal = (short[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final short[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final int fieldVal = field.getInt(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        public void decodeField(Object obj, IN in) throws Exception {
            final int fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setInt(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final int[] fieldVal = (int[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final int[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final long fieldVal = field.getLong(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final long fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setLong(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final long[] fieldVal = (long[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final long[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final float fieldVal = field.getFloat(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final float fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setFloat(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final float[] fieldVal = (float[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final float[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final double fieldVal = field.getDouble(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final double fieldVal = codec.decodePrim(in);
            setAccessible(true);
            field.setDouble(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final double[] fieldVal = (double[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final double[] fieldVal = codec.decode(in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final T fieldVal = (T) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final T fieldVal;
            fieldVal = codec.decode((Class<T>) field.getType(), in);
            setAccessible(true);
            field.set(obj, fieldVal);
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
        public IN encodeField(Object obj, IN in) throws Exception {
            setAccessible(true);
            final T[] fieldVal = (T[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, in);
        }

        @Override
        public void decodeField(Object obj, IN in) throws Exception {
            final T[] fieldVal;
            fieldVal = codec.decode((Class<T[]>) field.getType(), in);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    /**
     * Encode a field with an object into an encoded type {@code IN}.
     * @param obj       the object to which the field belongs
     * @param in       the encoded parent value
     * @return          the encoded value
     * @throws Exception if the operation fails
     */
    IN encodeField(Object obj, IN in) throws Exception;

    /**
     * One of the two {@code decode} methods must be implemented by sub-classes.
     * @param obj       object to which the field belongs
     * @param in       the encoded value
     * @throws Exception if the operation fails
     */
    void decodeField(Object obj, IN in) throws Exception;
}
