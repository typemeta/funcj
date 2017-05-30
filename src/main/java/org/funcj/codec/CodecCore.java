package org.funcj.codec;

import org.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.funcj.control.Exceptions.TODO;

public abstract class CodecCore<E> {

    protected final Map<String, Codec<?, E>> codecs = new HashMap<>();

    protected void initialise() {
    }

    public <T> E encode(Class<T> stcType, T val, E out) {
        return dynamicCodec(stcType).encode(val, out);
    }

    public <T> T decode(Class<T> stcType, E in) {
        return dynamicCodec(stcType).decode(in);
    }


    protected String classToName(Class<?> clazz) {
        return clazz.getName();
    }

    protected <T> Class<T> nameToClass(String name) {
        return (Class<T>) Exceptions.wrap(() -> Class.forName(name));
    }

    protected abstract <T> Codec<T, E> nullSafeCodec(Codec<T, E> codec);

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
        if (dynType.isPrimitive()) {
            if (dynType.equals(boolean.class)) {
                return (Codec<T, E>)booleanCodec();
            } else if (dynType.equals(int.class)) {
                return (Codec<T, E>)integerCodec();
            } else {
                throw TODO();
            }
        } else {
            final Codec<T, E> codec;
            if (dynType.isArray()) {
                final Class<?> elemType = dynType.getComponentType();
                if (elemType.equals(boolean.class)) {
                    codec =  (Codec<T, E>) booleanArrayCodec();
                } else if (elemType.equals(int.class)) {
                    codec =  (Codec<T, E>) integerArrayCodec();
                } else {
                    if (elemType.equals(Boolean.class)) {
                        codec =  (Codec<T, E>) objectArrayCodec(Boolean.class, booleanCodec());
                    } else if (elemType.equals(Integer.class)) {
                        codec =  (Codec<T, E>) objectArrayCodec(Integer.class, integerCodec());
                    } else {
                        final Codec<Object, E> elemCodec = dynamicCodec((Class<Object>) elemType);
                        codec =  (Codec<T, E>) objectArrayCodec((Class<Object>) elemType, elemCodec);
                    }
                }
            } else if (dynType.isEnum()) {
                codec =  enumCodec((Class)dynType);
            } else {
                if (dynType.equals(Boolean.class)) {
                    codec =  (Codec<T, E>)booleanCodec();
                } else if (dynType.equals(Integer.class)) {
                    codec =  (Codec<T, E>)integerCodec();
                } else if (dynType.equals(String.class)) {
                    codec =  (Codec<T, E>)stringCodec();
                } else {
                    if (Map.class.isAssignableFrom(dynType)) {
                        final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(dynType, Map.class);
                        final Class<?> keyType = typeArgs.get(0);
                        final Class<?> valueType = typeArgs.get(1);
                        codec =  (Codec<T, E>)mapCodec(keyType, valueType);
                    } else {
                        final String name = classToName(dynType);
                        codec =  (Codec<T, E>) codecs.computeIfAbsent(name, n -> createObjectCodec(dynType));
                    }
                }
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

    protected FieldCodec<E> getFieldCodec(Field field) {
        final Class<?> stcType = field.getType();
        if (stcType.isPrimitive()) {
            if (stcType.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<E>(field, booleanCodec());
            } else if (stcType.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<E>(field, integerCodec());
            } else {
                throw new RuntimeException(stcType.getName());
            }
        } else if (stcType.isArray()) {
            final Class<?> elemType = stcType.getComponentType();
            if (elemType.equals(boolean.class)) {
                return new FieldCodec.BooleanArrayFieldCodec<E>(field, booleanArrayCodec());
            } else {
                if (elemType.equals(Boolean.class)) {
                    final Codec<Boolean[], E> codec = objectArrayCodec(Boolean.class, booleanCodec());
                    return new FieldCodec.ObjectFieldCodec<Boolean[], E>(field, nullSafeCodec(codec));
                } else if (elemType.equals(Boolean.class)) {
                    final Codec<Integer[], E> codec = objectArrayCodec(Integer.class, integerCodec());
                    return new FieldCodec.ObjectFieldCodec<Integer[], E>(field, nullSafeCodec(codec));
                } else {
                    final Codec<Object, E> elemCodec = dynamicCodec((Class<Object>)elemType);
                    final Codec<Object[], E> codec = objectArrayCodec((Class<Object>)elemType, elemCodec);
                    return new FieldCodec.ObjectArrayFieldCodec<Object, E>(field, nullSafeCodec(codec));
                }
            }
        } else if (stcType.isEnum()) {
            final Codec<Object, E> codec = enumCodec((Class)stcType);
            return new FieldCodec.ObjectFieldCodec<Object, E>(field, nullSafeCodec(codec));
        } else {
            if (stcType.equals(Boolean.class)) {
                return new FieldCodec.ObjectFieldCodec<Boolean, E>(field, nullSafeCodec(booleanCodec()));
            } else if (stcType.equals(Integer.class)) {
                return new FieldCodec.ObjectFieldCodec<Integer, E>(field, nullSafeCodec(integerCodec()));
            } else if (stcType.equals(String.class)) {
                return new FieldCodec.ObjectFieldCodec<String, E>(field, nullSafeCodec(stringCodec()));
            } else {
                if (Map.class.isAssignableFrom(stcType)) {
                    final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                    final Class keyType = typeArgs.get(0);
                    final Class valType = typeArgs.get(1);
                    final Codec<Map, E> mapCodec = mapCodec(keyType, valType);
                    if (Modifier.isFinal(stcType.getModifiers())) {
                        return new FieldCodec.ObjectFieldCodec<Object, E>(field, (Codec) mapCodec);
                    } else{
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
}
