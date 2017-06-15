package org.funcj.codec;

import org.funcj.control.Exceptions;

import java.lang.reflect.*;

/**
 * A <code>FieldCodec</code> encapsulates encoding a field
 * to encoded type <code>E</code>, and vice-versa.
 * @param <E> encoded type
 */
public abstract class FieldCodec<E> {

    static class BooleanFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.BooleanCodec<E> codec;

        BooleanFieldCodec(Field field, Codec.BooleanCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final boolean fieldVal = Exceptions.wrap(() -> field.getBoolean(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final boolean fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setBoolean(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class BooleanArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<boolean[], E> codec;

        BooleanArrayFieldCodec(Field field, Codec<boolean[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final boolean[] fieldVal = (boolean[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
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

    static class ByteFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.ByteCodec<E> codec;

        ByteFieldCodec(Field field, Codec.ByteCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final byte fieldVal = Exceptions.wrap(() -> field.getByte(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final byte fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setByte(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class ByteArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<byte[], E> codec;

        ByteArrayFieldCodec(Field field, Codec<byte[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final byte[] fieldVal = (byte[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
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

    static class CharFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.CharCodec<E> codec;

        CharFieldCodec(Field field, Codec.CharCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final char fieldVal = Exceptions.wrap(() -> field.getChar(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final char fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setChar(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class CharArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<char[], E> codec;

        CharArrayFieldCodec(Field field, Codec<char[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final char[] fieldVal = (char[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
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

    static class ShortFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.ShortCodec<E> codec;

        ShortFieldCodec(Field field, Codec.ShortCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final short fieldVal = Exceptions.wrap(() -> field.getShort(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final short fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setShort(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class ShortArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<short[], E> codec;

        ShortArrayFieldCodec(Field field, Codec<short[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final short[] fieldVal = (short[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
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

    static class IntegerFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.IntCodec<E> codec;

        IntegerFieldCodec(Field field, Codec.IntCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final int fieldVal = Exceptions.wrap(() -> field.getInt(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        public void decodeField(Object obj, E enc) {
            final int fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setInt(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class IntegerArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<int[], E> codec;

        IntegerArrayFieldCodec(Field field, Codec<int[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final int[] fieldVal = (int[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final int[] fieldVal = codec.decode(int[].class, enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class LongFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.LongCodec<E> codec;

        LongFieldCodec(Field field, Codec.LongCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final long fieldVal = Exceptions.wrap(() -> field.getLong(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final long fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setLong(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class LongArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<long[], E> codec;

        LongArrayFieldCodec(Field field, Codec<long[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final long[] fieldVal = (long[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
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

    static class FloatFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.FloatCodec<E> codec;

        FloatFieldCodec(Field field, Codec.FloatCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final float fieldVal = Exceptions.wrap(() -> field.getFloat(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final float fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setFloat(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class FloatArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<float[], E> codec;

        FloatArrayFieldCodec(Field field, Codec<float[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final float[] fieldVal = (float[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
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

    static class DoubleFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.DoubleCodec<E> codec;

        DoubleFieldCodec(Field field, Codec.DoubleCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final double fieldVal = Exceptions.wrap(() -> field.getDouble(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final double fieldVal = codec.decode(enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.setDouble(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class DoubleArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec<double[], E> codec;

        DoubleArrayFieldCodec(Field field, Codec<double[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final double[] fieldVal = (double[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
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

    static class ObjectFieldCodec<T, E> extends FieldCodec<E> {

        protected final Codec<T, E> codec;

        protected ObjectFieldCodec(Field field, Codec<T, E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final T fieldVal = (T)Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final T fieldVal = codec.decode((Class<T>)field.getType(), enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class ObjectArrayFieldCodec<T, E> extends FieldCodec<E> {

        protected final Codec<T[], E> codec;

        protected ObjectArrayFieldCodec(Field field, Codec<T[], E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encodeField(Object obj, E enc) {
            setAccessible(true);
            final T[] fieldVal = (T[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, enc);
        }

        @Override
        public void decodeField(Object obj, E enc) {
            final T[] fieldVal = codec.decode((Class<T[]>)field.getType(), enc);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    protected final Field field;

    protected boolean isAccessible;

    protected FieldCodec(Field field) {
        this.field = field;
        this.isAccessible = Modifier.isFinal(field.getModifiers()) ||
                !Modifier.isPublic(field.getModifiers());
    }

    protected void setAccessible(boolean flag) {
        if (isAccessible) {
            field.setAccessible(flag);
        }
    }

    /**
     * Encode a field with an object into an encoded type <code>E</code>.
     * @param obj object to which the field belongs
     * @param enc encoded parent value
     * @return encoded value
     */
    public abstract E encodeField(Object obj, E enc);

    /**
     * One of the two <code>decode</code> methods must be implemented by sub-classes.
     * @param obj object to which the field belongs
     * @param enc the encoded value
     */
    public abstract void decodeField(Object obj, E enc);
}
