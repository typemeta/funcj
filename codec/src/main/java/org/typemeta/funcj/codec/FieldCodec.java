package org.typemeta.funcj.codec;

import java.lang.reflect.*;
import java.util.Objects;

/**
 * A {@code FieldCodec} encapsulates encoding a field
 * to an encoded type {@code E}, and vice-versa.
 * @param <E>       the encoded type
 */
@SuppressWarnings("unchecked")
public interface FieldCodec<E> {

    abstract class Impl<E> implements FieldCodec<E> {

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

    class BooleanFieldCodec<E> extends Impl<E> {

        protected final Codec.BooleanCodec<E> codec;

        BooleanFieldCodec(Field field, Codec.BooleanCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final boolean fieldVal = field.getBoolean(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final boolean fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setBoolean(obj, fieldVal);
            setAccessible(false);
        }
    }

    class BooleanArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<boolean[], E> codec;

        BooleanArrayFieldCodec(Field field, Codec<boolean[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final boolean[] fieldVal = (boolean[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final boolean[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class ByteFieldCodec<E> extends Impl<E> {

        protected final Codec.ByteCodec<E> codec;

        ByteFieldCodec(Field field, Codec.ByteCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final byte fieldVal = field.getByte(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final byte fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setByte(obj, fieldVal);
            setAccessible(false);
        }
    }

    class ByteArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<byte[], E> codec;

        ByteArrayFieldCodec(Field field, Codec<byte[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final byte[] fieldVal = (byte[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final byte[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class CharFieldCodec<E> extends Impl<E> {

        protected final Codec.CharCodec<E> codec;

        CharFieldCodec(Field field, Codec.CharCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final char fieldVal = field.getChar(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final char fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setChar(obj, fieldVal);
            setAccessible(false);
        }
    }

    class CharArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<char[], E> codec;

        CharArrayFieldCodec(Field field, Codec<char[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final char[] fieldVal = (char[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final char[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class ShortFieldCodec<E> extends Impl<E> {

        protected final Codec.ShortCodec<E> codec;

        ShortFieldCodec(Field field, Codec.ShortCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final short fieldVal = field.getShort(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final short fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setShort(obj, fieldVal);
            setAccessible(false);
        }
    }

    class ShortArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<short[], E> codec;

        ShortArrayFieldCodec(Field field, Codec<short[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final short[] fieldVal = (short[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final short[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class IntegerFieldCodec<E> extends Impl<E> {

        protected final Codec.IntCodec<E> codec;

        IntegerFieldCodec(Field field, Codec.IntCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final int fieldVal = field.getInt(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        public void decodeField(Object obj, E enc) throws Exception {
            final int fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setInt(obj, fieldVal);
            setAccessible(false);
        }
    }

    class IntegerArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<int[], E> codec;

        IntegerArrayFieldCodec(Field field, Codec<int[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final int[] fieldVal = (int[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final int[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class LongFieldCodec<E> extends Impl<E> {

        protected final Codec.LongCodec<E> codec;

        LongFieldCodec(Field field, Codec.LongCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final long fieldVal = field.getLong(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final long fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setLong(obj, fieldVal);
            setAccessible(false);
        }
    }

    class LongArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<long[], E> codec;

        LongArrayFieldCodec(Field field, Codec<long[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final long[] fieldVal = (long[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final long[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class FloatFieldCodec<E> extends Impl<E> {

        protected final Codec.FloatCodec<E> codec;

        FloatFieldCodec(Field field, Codec.FloatCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final float fieldVal = field.getFloat(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final float fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setFloat(obj, fieldVal);
            setAccessible(false);
        }
    }

    class FloatArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<float[], E> codec;

        FloatArrayFieldCodec(Field field, Codec<float[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final float[] fieldVal = (float[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final float[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class DoubleFieldCodec<E> extends Impl<E> {

        protected final Codec.DoubleCodec<E> codec;

        DoubleFieldCodec(Field field, Codec.DoubleCodec<E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final double fieldVal = field.getDouble(obj);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final double fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            field.setDouble(obj, fieldVal);
            setAccessible(false);
        }
    }

    class DoubleArrayFieldCodec<E> extends Impl<E> {

        protected final Codec<double[], E> codec;

        DoubleArrayFieldCodec(Field field, Codec<double[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final double[] fieldVal = (double[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final double[] fieldVal = codec.decode(enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class ObjectFieldCodec<T, E> extends Impl<E> {

        protected final Codec<T, E> codec;

        protected ObjectFieldCodec(Field field, Codec<T, E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final T fieldVal = (T) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final T fieldVal;
            fieldVal = codec.decode((Class<T>) field.getType(), enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    class ObjectArrayFieldCodec<T, E> extends Impl<E> {

        protected final Codec<T[], E> codec;

        protected ObjectArrayFieldCodec(Field field, Codec<T[], E> codec) {
            super(field);
            this.codec = Objects.requireNonNull(codec);
        }

        @Override
        public E encodeField(Object obj, E enc) throws Exception {
            setAccessible(true);
            final T[] fieldVal = (T[]) field.get(obj);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) throws Exception {
            final T[] fieldVal;
            fieldVal = codec.decode((Class<T[]>) field.getType(), enc);
            setAccessible(true);
            field.set(obj, fieldVal);
            setAccessible(false);
        }
    }

    /**
     * Encode a field with an object into an encoded type {@code E}.
     * @param obj       the object to which the field belongs
     * @param enc       the encoded parent value
     * @return          the encoded value
     * @throws Exception if the operation fails
     */
    E encodeField(Object obj, E enc) throws Exception;

    /**
     * One of the two {@code decode} methods must be implemented by sub-classes.
     * @param obj       object to which the field belongs
     * @param enc       the encoded value
     * @throws Exception if the operation fails
     */
    void decodeField(Object obj, E enc) throws Exception;
}
