package org.typemeta.funcj.codec;

import org.typemeta.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.Objects;

/**
 * A {@code FieldCodec} encapsulates encoding a field
 * to an encoded type {@code E}, and vice-versa.
 * @param <E> encoded type
 */
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final boolean fieldVal = Exceptions.wrap(() -> field.getBoolean(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final boolean fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setBoolean(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final boolean[] fieldVal = (boolean[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final boolean[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final byte fieldVal = Exceptions.wrap(() -> field.getByte(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final byte fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setByte(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final byte[] fieldVal = (byte[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final byte[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final char fieldVal = Exceptions.wrap(() -> field.getChar(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final char fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setChar(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final char[] fieldVal = (char[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final char[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final short fieldVal = Exceptions.wrap(() -> field.getShort(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final short fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setShort(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final short[] fieldVal = (short[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final short[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final int fieldVal = Exceptions.wrap(() -> field.getInt(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        public void decodeField(Object obj, E enc) {
            final int fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setInt(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final int[] fieldVal = (int[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final int[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final long fieldVal = Exceptions.wrap(() -> field.getLong(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final long fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setLong(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final long[] fieldVal = (long[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final long[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final float fieldVal = Exceptions.wrap(() -> field.getFloat(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final float fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setFloat(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final float[] fieldVal = (float[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final float[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final double fieldVal = Exceptions.wrap(() -> field.getDouble(obj), CodecException::new);
            setAccessible(false);
            return codec.encodePrim(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final double fieldVal = codec.decodePrim(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setDouble(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final double[] fieldVal = (double[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final double[] fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final T fieldVal = (T) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final T fieldVal = codec.decode((Class<T>) field.getType(), enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
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
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final T[] fieldVal = (T[]) Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final T[] fieldVal = codec.decode((Class<T[]>) field.getType(), enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    /**
     * Encode a field with an object into an encoded type {@code E}.
     * @param obj object to which the field belongs
     * @param enc encoded parent value
     * @return encoded value
     */
    E encodeField(Object obj, E enc);

    /**
     * One of the two {@code decode} methods must be implemented by sub-classes.
     * @param obj object to which the field belongs
     * @param enc the encoded value
     */
    void decodeField(Object obj, E enc);
}
