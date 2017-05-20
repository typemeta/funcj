package org.funcj.codec;

import org.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.funcj.control.Exceptions.TODO;

public interface Codec<T, E> {

    interface NullCodec<E> extends Codec<Object, E> {
        boolean isNull(E in);
    }

    abstract class BooleanCodec<E> implements Codec<Boolean, E> {

        @Override
        public E encode(Boolean val, E out) {
            return encodePrim(val, out) ;
        }

        public Boolean decode(E in) {
            return decodePrim(in);
        }

        abstract E encodePrim(boolean val, E out);

        abstract boolean decodePrim(E in);

    }

    interface BooleanArrayCodec<E> {
        E encode(boolean[] vals, E out);
        boolean[] decode(E in);
    }

    abstract class ObjectArrayCodec<T, E> implements Codec<T[], E> {
        private final Codec<T, E> elemCodec;

        protected ObjectArrayCodec(Codec<T, E> elemCodec) {
            this.elemCodec = elemCodec;
        }
    }

    abstract class ObjectCodec<T, E> implements Codec<T, E> {

        protected final Map<String, DynamicCodec.FieldCodec<E>> fieldCodecs;

        public ObjectCodec(Map<String, DynamicCodec.FieldCodec<E>> fieldCodecs) {
            this.fieldCodecs = fieldCodecs;
        }
    }

    abstract class DynamicCodec<E> implements Codec<Object, E> {
        static abstract class FieldCodec<E> {
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

        static class BooleanFieldCodec<E> extends FieldCodec<E> {

            protected final BooleanCodec<E> codec;

            BooleanFieldCodec(Field field, BooleanCodec<E> codec) {
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

            protected final BooleanArrayCodec<E> codec;

            BooleanArrayFieldCodec(Field field, BooleanArrayCodec<E> codec) {
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
                Exceptions.wrap(() -> field.set(obj, fieldVal));
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
                Exceptions.wrap(() -> field.set(obj, fieldVal));
            }
        }

        protected final Map<String, Codec<?, E>> codecs = new HashMap<>();

        @Override
        public E encode(Object val, E out) {
            if (val == null) {
                return nullCodec().encode(val, out);
            } else {
                final Codec<Object, E> codec = objectCodec((Class)val.getClass());
                return codec.encode(val, out);
            }
        }

        public Object decode(E in, Class<?> clazz) {
            return objectCodec(clazz).decode(in);
        }

        protected String classToName(Class<?> clazz) {
            return clazz.getName();
        }

        protected <T> Class<T> nameToClass(String name) {
            return (Class<T>)Exceptions.wrap(() -> Class.forName(name));
        }

        protected <T> Codec<T, E> getCodec(Class<T> clazz) {
            final String name = classToName(clazz);
            return (Codec<T, E>)codecs.computeIfAbsent(name, n -> {
                final Map<String, FieldCodec<E>> fieldCodecs = new LinkedHashMap<>();
                Class<?> cl = clazz;
                for (int depth = 0; !cl.equals(Object.class); depth++) {
                    final Field[] fields = cl.getDeclaredFields();
                    for (Field field : fields) {
                        final String fieldName = getFieldName(field, depth, fieldCodecs.keySet());
                        fieldCodecs.put(fieldName, getFieldCodec(field));
                    }
                    cl = cl.getSuperclass();
                }
                return getCodec(clazz, fieldCodecs);
            });
        }

        protected String getFieldName(Field field, int depth, Set<String> existingNames) {
            String name = field.getName();

            while (existingNames.contains(name)) {
                name = "*" + name;
            }

            return name;
        }

        protected <T> Codec<T, E> getCodec(
                Class<T> clazz,
                Map<String, DynamicCodec.FieldCodec<E>> fieldCodecs) {
            final Codec<T, E> codec = getCodecImpl (clazz, fieldCodecs);
            final NullCodec<E> nullCodec = nullCodec();
            return new Codec<T, E>() {
                @Override
                public E encode(T val, E out) {
                    if (val == null) {
                        return nullCodec().encode(val, out);
                    } else {
                        return codec.encode(val, out);
                    }
                }

                @Override
                public T decode(E in) {
                    if (nullCodec.isNull(in)) {
                        return (T)nullCodec.decode(in);
                    } else {
                        return codec.decode(in);
                    }
                }
            };
        }

        protected abstract <T> Codec<T, E> getCodecImpl(
                Class<T> clazz,
                Map<String, DynamicCodec.FieldCodec<E>> fieldCodecs);

        protected FieldCodec<E> getFieldCodec(Field field) {
            final Class<?> type = field.getType();
            if (type.isPrimitive()) {
                if (type.equals(boolean.class)) {
                    return new BooleanFieldCodec<E>(field, booleanCodec());
                } else {
                    throw TODO();
                }
            } else if (type.isArray()) {
                final Class<?> elemType = type.getComponentType();
                if (elemType.equals(boolean.class)) {
                    return new BooleanArrayFieldCodec<E>(field, booleanArrayCodec());
                } else {
                    if (elemType.equals(Boolean.class)) {
                        final ObjectArrayCodec<Boolean, E> codec = objectArrayCodec(Boolean.class, booleanCodec());
                        return new ObjectFieldCodec<Boolean[], E>(field, codec);
                    } else {
                        final ObjectArrayCodec<Object, E> codec =
                                objectArrayCodec(
                                        (Class)elemType,
                                        DynamicCodec.this);
                        return new ObjectFieldCodec<Object[], E>(field, codec);
                    }
                }
            } else {
                if (type.equals(Boolean.class)) {
                    return new ObjectFieldCodec<Boolean, E>(field, booleanCodec());
                } else {
                    final Codec<Object, E> codec = DynamicCodec.this;
                    return new ObjectFieldCodec<Object, E>(field, codec);
                }
            }
        }

        protected abstract NullCodec<E> nullCodec();

        protected abstract BooleanCodec<E> booleanCodec();

        protected abstract BooleanArrayCodec<E> booleanArrayCodec();

        protected <T> Codec<T, E> objectCodec(Class<T> clazz) {
            return getCodec(clazz);
        }

        protected abstract <T> ObjectArrayCodec<T, E> objectArrayCodec(
                Class<T> elemClass,
                Codec<T, E> elemCodec);

        protected abstract <T> Codec<T[], E> objectArrayCodec();
    }

    E encode(T val, E out);
    T decode(E in);
}
