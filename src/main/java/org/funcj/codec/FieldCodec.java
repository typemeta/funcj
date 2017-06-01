package org.funcj.codec;

import org.funcj.control.Exceptions;

import java.lang.reflect.*;

public abstract class FieldCodec<E> {

    static class BooleanFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.BooleanCodec<E> codec;

        BooleanFieldCodec(Field field, Codec.BooleanCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encode(Object obj, E out) {
            setAccessible(true);
            final boolean fieldVal = Exceptions.wrap(() -> field.getBoolean(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final boolean fieldVal = codec.decode(in);
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
        public E encode(Object obj, E out) {
            setAccessible(true);
            final boolean[] fieldVal = (boolean[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final boolean[] fieldVal = codec.decode(in);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal), CodecException::new);
            setAccessible(false);
        }
    }

    static class IntegerFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.IntegerCodec<E> codec;

        IntegerFieldCodec(Field field, Codec.IntegerCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encode(Object obj, E out) {
            setAccessible(true);
            final int fieldVal = Exceptions.wrap(() -> field.getInt(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, out);
        }

        public void decode(Object obj, E in) {
            final int fieldVal = codec.decode(in);
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
        public E encode(Object obj, E out) {
            setAccessible(true);
            final int[] fieldVal = (int[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final int[] fieldVal = codec.decode(int[].class, in);
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
        public E encode(Object obj, E out) {
            setAccessible(true);
            final T fieldVal = (T)Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final T fieldVal = codec.decode((Class<T>)field.getType(), in);
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
        public E encode(Object obj, E out) {
            setAccessible(true);
            final T[] fieldVal = (T[])Exceptions.wrap(() -> field.get(obj), CodecException::new);
            setAccessible(false);
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final T[] fieldVal = codec.decode((Class<T[]>)field.getType(), in);
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

    public abstract E encode(Object obj, E out);

    public abstract void decode(Object obj, E in);
}
