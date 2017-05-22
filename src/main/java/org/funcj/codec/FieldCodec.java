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
            final boolean fieldVal = Exceptions.wrap(() -> field.getBoolean(obj));
            return codec.encode(fieldVal, out);
        }

        public void decode(Object obj, E in) {
            final boolean fieldVal = codec.decode(in);
            setAccessible(true);
            Exceptions.wrap(() -> field.setBoolean(obj, fieldVal));
            setAccessible(false);
        }
    }

    static class BooleanArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.BooleanArrayCodec<E> codec;

        BooleanArrayFieldCodec(Field field, Codec.BooleanArrayCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encode(Object obj, E out) {
            final boolean[] fieldVal = (boolean[])Exceptions.wrap(() -> field.get(obj));
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final boolean[] fieldVal = codec.decode(in);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal));
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
            final int fieldVal = Exceptions.wrap(() -> field.getInt(obj));
            return codec.encode(fieldVal, out);
        }

        public void decode(Object obj, E in) {
            final int fieldVal = codec.decode(in);
            setAccessible(true);
            Exceptions.wrap(() -> field.setInt(obj, fieldVal));
            setAccessible(false);
        }
    }

    static class IntegerArrayFieldCodec<E> extends FieldCodec<E> {

        protected final Codec.IntegerArrayCodec<E> codec;

        IntegerArrayFieldCodec(Field field, Codec.IntegerArrayCodec<E> codec) {
            super(field);
            this.codec = codec;
        }

        @Override
        public E encode(Object obj, E out) {
            final int[] fieldVal = (int[])Exceptions.wrap(() -> field.get(obj));
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final int[] fieldVal = codec.decode(in);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal));
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
            final T fieldVal = (T)Exceptions.wrap(() -> field.get(obj));
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final T fieldVal = codec.decode(in);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal));
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
            final T[] fieldVal = (T[])Exceptions.wrap(() -> field.get(obj));
            return codec.encode(fieldVal, out);
        }

        @Override
        public void decode(Object obj, E in) {
            final T[] fieldVal = codec.decode(in);
            setAccessible(true);
            Exceptions.wrap(() -> field.set(obj, fieldVal));
            setAccessible(false);
        }
    }

    protected final Field field;
    protected boolean isFinal;

    protected FieldCodec(Field field) {
        this.field = field;
        this.isFinal = Modifier.isFinal(field.getModifiers());
    }

    protected void setAccessible(boolean flag) {
        if (isFinal) {
            field.setAccessible(flag);
        }
    }

    public abstract E encode(Object obj, E out);

    public abstract void decode(Object obj, E in);
}
