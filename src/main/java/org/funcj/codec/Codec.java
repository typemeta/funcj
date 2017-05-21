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

    interface EnumCodec<EN extends Enum<EN>, E> extends Codec<EN, E> {

    }

    abstract class DynamicCodec<E> implements Codec<Object, E> {

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

        protected <T> Codec<T, E> getCodec(Class<T> dynClass) {
            final String name = classToName(dynClass);
            return (Codec<T, E>)codecs.computeIfAbsent(name, n -> {
                final Map<String, FieldCodec<E>> fieldCodecs = new LinkedHashMap<>();
                Class<?> clazz = dynClass;
                for (int depth = 0; !clazz.equals(Object.class); depth++) {
                    final Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (!Modifier.isTransient(field.getModifiers())) {
                            final String fieldName = getFieldName(field, depth, fieldCodecs.keySet());
                            fieldCodecs.put(fieldName, getFieldCodec(field));
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
                return getCodec(dynClass, fieldCodecs);
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
                Class<T> dynClass,
                Map<String, FieldCodec<E>> fieldCodecs) {
            final Codec<T, E> codec = getCodecImpl(dynClass, fieldCodecs);
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
                Class<T> dynClass,
                Map<String, FieldCodec<E>> fieldCodecs);

        protected FieldCodec<E> getFieldCodec(Field field) {
            final Class<?> type = field.getType();
            if (type.isPrimitive()) {
                if (type.equals(boolean.class)) {
                    return new FieldCodec.BooleanFieldCodec<E>(field, booleanCodec());
                } else {
                    throw TODO();
                }
            } else if (type.isArray()) {
                final Class<?> elemType = type.getComponentType();
                if (elemType.equals(boolean.class)) {
                    return new FieldCodec.BooleanArrayFieldCodec<E>(field, booleanArrayCodec());
                } else {
                    if (elemType.equals(Boolean.class)) {
                        final ObjectArrayCodec<Boolean, E> codec = objectArrayCodec(Boolean.class, booleanCodec());
                        return new FieldCodec.ObjectFieldCodec<Boolean[], E>(field, codec);
                    } else {
                        final ObjectArrayCodec<Object, E> codec =
                                objectArrayCodec(
                                        (Class)elemType,
                                        DynamicCodec.this);
                        return new FieldCodec.ObjectFieldCodec<Object[], E>(field, codec);
                    }
                }
            } else {
                if (type.equals(Boolean.class)) {
                    return new FieldCodec.ObjectFieldCodec<Boolean, E>(field, booleanCodec());
                } else {
                    final Codec<Object, E> codec = DynamicCodec.this;
                    return new FieldCodec.ObjectFieldCodec<Object, E>(field, codec);
                }
            }
        }

        protected abstract NullCodec<E> nullCodec();

        protected abstract BooleanCodec<E> booleanCodec();

        protected abstract BooleanArrayCodec<E> booleanArrayCodec();

        protected <T> Codec<T, E> objectCodec(Class<T> dynClass) {
            return getCodec(dynClass);
        }

        protected abstract <T> ObjectArrayCodec<T, E> objectArrayCodec(
                Class<T> elemClass,
                Codec<T, E> elemCodec);
    }

    E encode(T val, E out);
    T decode(E in);
}
