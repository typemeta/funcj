package org.funcj.codec;

import org.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.funcj.control.Exceptions.TODO;

public abstract class CodecCore<E> {

    protected final Map<String, Codec<?, E>> codecs = new HashMap<>();

    protected void initialise() {
        //codecs.put(classToName(Boolean.class), booleanCodec());
        //codecs.put(classToName(new boolean[0].getClass()), booleanArrayCodec());
    }

    public <T> E encode(Class<T> stcClass, T val, E out) {
        return dynamicCodec(stcClass).encode(val, out);
    }

    public <T> T                                                                                                                                                                                                                                                                        decode(Class<T> stcClass, E in) {
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

    protected abstract Codec.IntegerCodec<E> integerCodec();

    protected abstract Codec<int[], E> integerArrayCodec();

    protected abstract Codec<String, E> stringCodec();

    protected abstract <EM extends Enum<EM>> Codec<EM, E> enumCodec(
            Class<? super EM> stcClass,
            Class<EM> dynClass);

    protected abstract <K, V> Codec<Map<K, V>, E> mapCodec(
            Class<Map<K, V>> stcClass,
            Class<K> stcKeyClass,
            Class<V> stcValClass);

    protected abstract <T> Codec<T, E> dynamicCodec(Class<T> stcClass);

    protected abstract <T> Codec<T[], E> objectArrayCodec(
            Class<T> elemClass,
            Codec<T, E> elemCodec);

    protected <T> Codec<T, E> getCodec(Class<T> stcClass, Class<? extends T> dynClass) {
        if (dynClass.isPrimitive()) {
            if (dynClass.equals(boolean.class)) {
                return (Codec<T, E>)booleanCodec();
            } else if (dynClass.equals(int.class)) {
                return (Codec<T, E>)integerCodec();
            } else {
                throw TODO();
            }
        } else if (dynClass.isArray()) {
            final Class<?> elemType = dynClass.getComponentType();
            if (elemType.equals(boolean.class)) {
                return (Codec<T, E>) booleanArrayCodec();
            } else if (elemType.equals(int.class)) {
                return (Codec<T, E>) integerArrayCodec();
            } else {
                if (elemType.equals(Boolean.class)) {
                    return (Codec<T, E>) objectArrayCodec(Boolean.class, booleanCodec());
                } else if (elemType.equals(Integer.class)) {
                    return (Codec<T, E>) objectArrayCodec(Integer.class, integerCodec());
                } else {
                    final Codec<Object, E> elemCodec = dynamicCodec((Class<Object>) dynClass);
                    return (Codec<T, E>) objectArrayCodec((Class<Object>) elemType, elemCodec);
                }
            }
        } else if (dynClass.isEnum()) {
            return enumCodec((Class)stcClass, (Class)dynClass);
        } else {
            if (dynClass.equals(Boolean.class)) {
                return (Codec<T, E>)booleanCodec();
            } else if (dynClass.equals(Integer.class)) {
                return (Codec<T, E>)integerCodec();
            } else if (dynClass.equals(String.class)) {
                return (Codec<T, E>)stringCodec();
            } else {
                if (Map.class.isAssignableFrom(dynClass)) {
                    final List<Class<?>> typeArgs = ReflectionUtils.getTypeArgs(dynClass, Map.class);
                    final Class<?> stcKeyClass = typeArgs.size() == 2 ? typeArgs.get(0) : null;
                    final Class<?> stcValClass = typeArgs.size() == 2 ? typeArgs.get(1) : null;
                    return (Codec<T, E>)mapCodec((Class)stcClass, stcKeyClass, stcValClass);
                } else {
                    final String name = classToName(dynClass);
                    return (Codec<T, E>) codecs.computeIfAbsent(name, n -> createObjectCodec(stcClass, dynClass));
                }
            }
        }
    }

    protected <T> Codec<T, E> createObjectCodec(Class<T> stcClass, Class<? extends T> dynClass) {
        final Map<String, FieldCodec<E>> fieldCodecs = new LinkedHashMap<>();
        Class<?> clazz = dynClass;
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
            } else if (type.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<E>(field, integerCodec());
            } else {
                throw new RuntimeException(type.getName());
            }
        } else if (type.isArray()) {
            final Class<?> elemType = type.getComponentType();
            if (elemType.equals(boolean.class)) {
                return new FieldCodec.BooleanArrayFieldCodec<E>(field, booleanArrayCodec());
            } else if (elemType.equals(int.class)) {
                return new FieldCodec.IntegerArrayFieldCodec<E>(field, integerArrayCodec());
            } else {
                if (elemType.equals(Boolean.class)) {
                    final Codec<Boolean[], E> codec = objectArrayCodec(Boolean.class, booleanCodec());
                    return new FieldCodec.ObjectFieldCodec<Boolean[], E>(field, codec);
                } else if (elemType.equals(Integer.class)) {
                    final Codec<Integer[], E> codec = objectArrayCodec(Integer.class, integerCodec());
                    return new FieldCodec.ObjectFieldCodec<Integer[], E>(field, codec);
                } else {
                    final Codec<Object, E> elemCodec = dynamicCodec((Class<Object>)elemType);
                    final Codec<Object[], E> codec = objectArrayCodec((Class<Object>)elemType, elemCodec);
                    return new FieldCodec.ObjectArrayFieldCodec<Object, E>(field, codec);
                }
            }
        } else if (type.isEnum()) {
            final Codec<Object, E> codec = enumCodec((Class)type, null);
            return new FieldCodec.ObjectFieldCodec<Object, E>(field, codec);
        } else {
            if (type.equals(Boolean.class)) {
                return new FieldCodec.ObjectFieldCodec<Boolean, E>(field, booleanCodec());
            } else if (type.equals(Integer.class)) {
                return new FieldCodec.ObjectFieldCodec<Integer, E>(field, integerCodec());
            } else if (type.equals(String.class)) {
                return new FieldCodec.ObjectFieldCodec<String, E>(field, stringCodec());
            } else {
                if (Map.class.isAssignableFrom(type)) {
                    final List<Class<?>> typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                    final Class<?> stcKeyClass = typeArgs.size() == 2 ? typeArgs.get(0) : null;
                    final Class<?> stcValClass = typeArgs.size() == 2 ? typeArgs.get(1) : null;
                    final Codec<Object, E> codec = mapCodec((Class)type, stcKeyClass, stcValClass);
                    return new FieldCodec.ObjectFieldCodec<Object, E>(field, codec);
                } else {
                    final Codec<Object, E> codec = dynamicCodec((Class<Object>) type);
                    return new FieldCodec.ObjectFieldCodec<Object, E>(field, codec);
                }
            }
        }
    }
}
