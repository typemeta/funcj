package org.funcj.codec;

import org.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.*;

import static org.funcj.control.Exceptions.TODO;

public abstract class CodecCore<E> {

    protected final Map<String, Codec<?, E>> codecs = new HashMap<>();

    protected void initialise() {
        codecs.put(classToName(Boolean.class), booleanCodec());
    }

    public <T> E encode(Class<T> stcClass, T val, E out) {
        return dynamicCodec(stcClass).encode(val, out);
    }

    public <T> T decode(Class<T> stcClass, E in) {
        return dynamicCodec(stcClass).decode(in);
    }


    protected String classToName(Class<?> clazz) {
        return clazz.getName();
    }

    protected <T> Class<T> nameToClass(String name) {
        return (Class<T>) Exceptions.wrap(() -> Class.forName(name));
    }

    protected abstract Codec.NullCodec<E> nullCodec();

    protected abstract Codec.BooleanCodec<E> booleanCodec();

    protected abstract Codec.BooleanArrayCodec<E> booleanArrayCodec();

    protected abstract <T> Codec.DynamicCodec<T, E> dynamicCodec(Class<T> stcClass);

    protected abstract <T> Codec.ObjectArrayCodec<T, E> objectArrayCodec(
            Class<T> elemClass,
            Codec<T, E> elemCodec);

    protected <T> Codec<T, E> getCodec(Class<T> stcClass, Class<? extends T> dynClass) {
        final String name = classToName(dynClass);
        return (Codec<T, E>)codecs.computeIfAbsent(name, n -> createObjectCodec(stcClass, dynClass));
    }

    protected <T> Codec<T, E> createObjectCodec(Class<T> stcClass, Class<? extends T> dynClass) {
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

        return createObjectCodec(stcClass, dynClass, fieldCodecs);
    }

    protected abstract <T> Codec<T, E> createObjectCodec(
            Class<T> stcClass,
            Class<? extends T> dynClass,
            Map<String, FieldCodec<E>> fieldCodecs);

    protected String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "*" + name;
        }
        return name;
    }

    protected FieldCodec<E> getFieldCodec(Field field) {
        final Class<?> type = field.getType();
        if (type.isPrimitive()) {
            if (type.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<E>(field, booleanCodec());
            } else {
                throw TODO();
            }
//        } else if (type.isArray()) {
//            final Class<?> elemType = type.getComponentType();
//            if (elemType.equals(boolean.class)) {
//                return new FieldCodec.BooleanArrayFieldCodec<E>(field, booleanArrayCodec());
//            } else {
//                if (elemType.equals(Boolean.class)) {
//                    final Codec.ObjectArrayCodec<Boolean, E> codec = objectArrayCodec(Boolean.class, booleanCodec());
//                    return new FieldCodec.ObjectFieldCodec<Boolean[], E>(field, codec);
//                } else {
//                    final Codec<Object, E> elemCodec = new FieldObjectCodec<Object, E>(
//                            (Class<Object>)elemType,
//                            DynamicCodec.this);
//                    final ObjectArrayCodec<Object, E> codec =
//                            objectArrayCodec(
//                                    (Class)elemType,
//                                    elemCodec);
//                    return new FieldCodec.ObjectFieldCodec<Object[], E>(field, codec);
//                }
//            }
        } else {
            if (type.equals(Boolean.class)) {
                return new FieldCodec.ObjectFieldCodec<Boolean, E>(field, booleanCodec());
            } else {
                final Codec<Object, E> codec = dynamicCodec((Class<Object>)type);
                return new FieldCodec.ObjectFieldCodec<Object, E>(field, codec);
            }
        }
    }
}
