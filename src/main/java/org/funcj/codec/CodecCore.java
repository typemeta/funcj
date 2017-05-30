package org.funcj.codec;

import org.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.*;

public abstract class CodecCore<E> {

    protected final Map<String, Codec<?, E>> codecs = new HashMap<>();

    public <T> E encode(Class<T> type, T val, E out) {
        return dynamicCodec(type).encode(val, out);
    }

    public <T> T decode(Class<T> type, E in) {
        return dynamicCodec(type).decode(in);
    }


    protected String classToName(Class<?> clazz) {
        return clazz.getName();
    }

    protected <T> Class<T> nameToClass(String name) {
        return (Class<T>) Exceptions.wrap(() -> Class.forName(name));
    }

    protected <T> Codec<T, E> nullSafeCodec(Codec<T, E> codec) {
        final Codec.NullCodec<E> nullCodec = nullCodec();
        return new Codec<T, E>() {
            @Override
            public E encode(T val, E out) {
                if (val == null) {
                    return nullCodec.encode(null, out);
                } else {
                    return codec.encode(val, out);
                }
            }

            @Override
            public T decode(Class<T> dynType, E in) {
                if (nullCodec.isNull(in)) {
                    return (T)nullCodec.decode(in);
                } else {
                    return codec.decode(dynType, in);
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

    protected abstract Codec.NullCodec<E> nullCodec();

    protected abstract Codec.BooleanCodec<E> booleanCodec();

    protected abstract Codec<boolean[], E> booleanArrayCodec();

    protected abstract Codec.IntegerCodec<E> integerCodec();

    protected abstract Codec<int[], E> integerArrayCodec();

    protected abstract Codec<String, E> stringCodec();

    protected abstract <EM extends Enum<EM>> Codec<EM, E> enumCodec(Class<? super EM> enumType);

    protected abstract <K, V> Codec<Map<K, V>, E> mapCodec(
            Class<K> keyType,
            Class<V> valType);

    protected abstract <T> Codec<T[], E> objectArrayCodec(
            Class<T> elemType,
            Codec<T, E> elemCodec);

    protected abstract <T> Codec<T, E> dynamicCodec(Class<T> stcType);

    protected abstract <T> Codec<T, E> dynamicCodec(Codec<T, E> codec, Class<T> stcType);

    protected <T> Codec<T, E> getCodec(Class<T> dynType) {
        final String name = classToName(dynType);
        return (Codec<T, E>)codecs.computeIfAbsent(name, n -> getCodecImpl(dynType));
    }

    protected <T> Codec<T, E> getCodecImpl(Class<T> dynType) {
        if (dynType.isPrimitive()) {
            if (dynType.equals(boolean.class)) {
                return (Codec<T, E>)booleanCodec();
            } else if (dynType.equals(int.class)) {
                return (Codec<T, E>)integerCodec();
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + dynType);
            }
        } else {
            final Codec<T, E> codec;
            if (dynType.isArray()) {
                final Class<?> elemType = dynType.getComponentType();
                if (elemType.equals(boolean.class)) {
                    codec = (Codec<T, E>) booleanArrayCodec();
                } else if (elemType.equals(int.class)) {
                    codec = (Codec<T, E>) integerArrayCodec();
                } else {
                    if (elemType.equals(Boolean.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Boolean.class, booleanCodec());
                    } else if (elemType.equals(Integer.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Integer.class, integerCodec());
                    } else {
                        final Codec<Object, E> elemCodec = dynamicCodec((Class<Object>) elemType);
                        codec = (Codec<T, E>) objectArrayCodec((Class<Object>) elemType, elemCodec);
                    }
                }
            } else if (dynType.isEnum()) {
                codec = enumCodec((Class) dynType);
            } else if (dynType.equals(Boolean.class)) {
                codec = (Codec<T, E>) booleanCodec();
            } else if (dynType.equals(Integer.class)) {
                codec = (Codec<T, E>) integerCodec();
            } else if (dynType.equals(String.class)) {
                codec = (Codec<T, E>) stringCodec();
            } else if (Map.class.isAssignableFrom(dynType)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(dynType, Map.class);
                final Class<?> keyType = typeArgs.get(0);
                final Class<?> valueType = typeArgs.get(1);
                codec = (Codec<T, E>) mapCodec(keyType, valueType);
            } else {
                codec = createObjectCodec(dynType);
            }

            return nullSafeCodec(codec);
        }
    }

    protected <T> Codec<T, E> createObjectCodec(Class<T> type) {
        final Map<String, FieldCodec<E>> fieldCodecs = new LinkedHashMap<>();
        Class<?> clazz = type;
        for (int depth = 0; !clazz.equals(Object.class); depth++) {
            final Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = getFieldName(field, depth, fieldCodecs.keySet());
                    fieldCodecs.put(fieldName, getFieldCodec(field));
                }
            }
            clazz = clazz.getSuperclass();
        }

        return createObjectCodec(type, fieldCodecs);
    }

    protected abstract <T> Codec<T, E> createObjectCodec(
            Class<T> type,
            Map<String, FieldCodec<E>> fieldCodecs);

    protected String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "*" + name;
        }
        return name;
    }

    protected <T> FieldCodec<E> getFieldCodec(Field field) {
        final Class<T> stcType = (Class<T>)field.getType();
        if (stcType.isPrimitive()) {
            if (stcType.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<E>(field, booleanCodec());
            } else if (stcType.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<E>(field, integerCodec());
            } else {
                throw new RuntimeException(stcType.getName());
            }
        } else {
            if (stcType.isArray()) {
                final Class<?> elemType = stcType.getComponentType();
                if (elemType.equals(boolean.class)) {
                    final Codec<boolean[], E> codec = getCodec((Class<boolean[]>)stcType);
                    return new FieldCodec.BooleanArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(int.class)) {
                    final Codec<int[], E> codec = getCodec((Class<int[]>)stcType);
                    return new FieldCodec.IntegerArrayFieldCodec<E>(field, codec);
                } else {
                    final Codec<T[], E> codec = getCodec((Class<T[]>)stcType);
                    return new FieldCodec.ObjectArrayFieldCodec<>(field, codec);
                }
            } else if (stcType.isEnum() ||
                        stcType.equals(Boolean.class) ||
                        stcType.equals(Integer.class) ||
                        stcType.equals(String.class)) {
                final Codec<T, E> codec = getCodec(stcType);
                return new FieldCodec.ObjectFieldCodec<>(field, codec);
            } else if (Map.class.isAssignableFrom(stcType)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                final Class keyType = typeArgs.get(0);
                final Class valType = typeArgs.get(1);
                final Codec<Map, E> mapCodec = mapCodec(keyType, valType);
                if (Modifier.isFinal(stcType.getModifiers())) {
                    return new FieldCodec.ObjectFieldCodec<Object, E>(field, (Codec) nullSafeCodec(mapCodec));
                } else {
                    final Codec<Object, E> dynCodec = dynamicCodec((Codec) mapCodec, stcType);
                    return new FieldCodec.ObjectFieldCodec<Object, E>(field, nullSafeCodec(dynCodec));
                }
            } else {
                final Codec<Object, E> codec = dynamicCodec((Class<Object>) stcType);
                return new FieldCodec.ObjectFieldCodec<Object, E>(field, nullSafeCodec(codec));
            }
        }
    }
}
