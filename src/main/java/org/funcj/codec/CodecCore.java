package org.funcj.codec;

import org.funcj.codec.utils.ReflectionUtils;
import org.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.*;

public abstract class CodecCore<E> {

    protected final Map<String, Codec<?, E>> codecs = new HashMap<>();

    public <T> void registerCodec(Class<T> clazz, Codec<T, E> codec) {
        codecs.put(classToName(clazz), codec);
    }

    abstract public <T> E encode(Class<T> type, T val);

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
        return (Class<T>) Exceptions.wrap(() -> Class.forName(name), CodecException::new);
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

    public abstract Codec.NullCodec<E> nullCodec();

    public abstract Codec.BooleanCodec<E> booleanCodec();

    public abstract Codec<boolean[], E> booleanArrayCodec();

    public abstract Codec.ByteCodec<E> byteCodec();

    public abstract Codec<byte[], E> byteArrayCodec();

    public abstract Codec.CharCodec<E> charCodec();

    public abstract Codec<char[], E> charArrayCodec();

    public abstract Codec.ShortCodec<E> shortCodec();

    public abstract Codec<short[], E> shortArrayCodec();

    public abstract Codec.IntCodec<E> intCodec();

    public abstract Codec<int[], E> intArrayCodec();

    public abstract Codec.LongCodec<E> longCodec();

    public abstract Codec<long[], E> longArrayCodec();

    public abstract Codec.FloatCodec<E> floatCodec();

    public abstract Codec<float[], E> floatArrayCodec();

    public abstract Codec.DoubleCodec<E> doubleCodec();

    public abstract Codec<double[], E> doubleArrayCodec();

    public abstract Codec<String, E> stringCodec();

    public abstract <EM extends Enum<EM>> Codec<EM, E> enumCodec(Class<? super EM> enumType);

    public <K, V> Codec<Map<K, V>, E> mapCodec(
            Class<K> keyType,
            Class<V> valType)  {
        final Codec<V, E> valueCodec = dynamicCodec(valType);
        if (String.class.equals(keyType)) {
            return (Codec)mapCodec(valueCodec);
        } else {
            final Codec<K, E> keyCodec = dynamicCodec(keyType);
            return mapCodec(keyCodec, valueCodec);
        }
    }

    public abstract <V> Codec<Map<String, V>, E> mapCodec(Codec<V, E> valueCodec);

    public abstract <K, V> Codec<Map<K, V>, E> mapCodec(
            Codec<K, E> keyCodec,
            Codec<V, E> valueCodec);

    public abstract <T> Codec<Collection<T>, E> collCodec(
            Class<T> elemType,
            Codec<T, E> elemCodec);

    public abstract <T> Codec<T[], E> objectArrayCodec(
            Class<T> elemType,
            Codec<T, E> elemCodec);

    public abstract <T> Codec<T, E> dynamicCodec(Class<T> stcType);

    public abstract <T> Codec<T, E> dynamicCodec(Codec<T, E> codec, Class<T> stcType);

    public <T> Codec<T, E> getNullSafeCodec(Class<T> dynType) {
        return nullSafeCodec(getNullUnsafeCodec(dynType));
    }

    public <T> Codec<T, E> getNullUnsafeCodec(Class<T> dynType) {
        final String name = classToName(dynType);
        return (Codec<T, E>)codecs.computeIfAbsent(name, n -> getCodecImpl(dynType));
    }

    public <T> Codec<T, E> getCodecImpl(Class<T> dynType) {
        if (dynType.isPrimitive()) {
            if (dynType.equals(boolean.class)) {
                return (Codec<T, E>)booleanCodec();
            } else if (dynType.equals(byte.class)) {
                return (Codec<T, E>) byteCodec();
            } else if (dynType.equals(char.class)) {
                return (Codec<T, E>) charCodec();
            } else if (dynType.equals(short.class)) {
                return (Codec<T, E>) shortCodec();
            } else if (dynType.equals(int.class)) {
                return (Codec<T, E>) intCodec();
            } else if (dynType.equals(long.class)) {
                return (Codec<T, E>) longCodec();
            } else if (dynType.equals(float.class)) {
                return (Codec<T, E>) floatCodec();
            } else if (dynType.equals(double.class)) {
                return (Codec<T, E>) doubleCodec();
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + dynType);
            }
        } else {
            final Codec<T, E> codec;
            if (dynType.isArray()) {
                final Class<?> elemType = dynType.getComponentType();
                if (elemType.equals(boolean.class)) {
                    codec = (Codec<T, E>) booleanArrayCodec();
                } else if (elemType.equals(byte.class)) {
                    codec = (Codec<T, E>) byteArrayCodec();
                } else if (elemType.equals(char.class)) {
                    codec = (Codec<T, E>) charArrayCodec();
                } else if (elemType.equals(short.class)) {
                    codec = (Codec<T, E>) shortArrayCodec();
                } else if (elemType.equals(int.class)) {
                    codec = (Codec<T, E>) intArrayCodec();
                } else if (elemType.equals(long.class)) {
                    codec = (Codec<T, E>) longArrayCodec();
                } else if (elemType.equals(float.class)) {
                    codec = (Codec<T, E>) floatArrayCodec();
                } else if (elemType.equals(double.class)) {
                    codec = (Codec<T, E>) doubleArrayCodec();
                } else {
                    if (elemType.equals(Boolean.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Boolean.class, booleanCodec());
                    } else if (elemType.equals(Byte.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Byte.class, byteCodec());
                    } else if (elemType.equals(Character.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Character.class, charCodec());
                    } else if (elemType.equals(Short.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Short.class, shortCodec());
                    } else if (elemType.equals(Integer.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Integer.class, intCodec());
                    } else if (elemType.equals(Long.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Long.class, longCodec());
                    } else if (elemType.equals(Float.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Float.class, floatCodec());
                    } else if (elemType.equals(Double.class)) {
                        codec = (Codec<T, E>) objectArrayCodec(Double.class, doubleCodec());
                    } else {
                        final Codec<Object, E> elemCodec = nullSafeCodec(dynamicCodec((Class<Object>) elemType));
                        codec = (Codec<T, E>) objectArrayCodec((Class<Object>) elemType, elemCodec);
                    }
                }
            } else if (dynType.isEnum()) {
                codec = enumCodec((Class) dynType);
            } else if (dynType.equals(Boolean.class)) {
                codec = (Codec<T, E>) booleanCodec();
            } else if (dynType.equals(Byte.class)) {
                codec = (Codec<T, E>) byteCodec();
            } else if (dynType.equals(Character.class)) {
                codec = (Codec<T, E>) charCodec();
            } else if (dynType.equals(Short.class)) {
                codec = (Codec<T, E>) shortCodec();
            } else if (dynType.equals(Integer.class)) {
                codec = (Codec<T, E>) intCodec();
            } else if (dynType.equals(Long.class)) {
                codec = (Codec<T, E>) longCodec();
            } else if (dynType.equals(Float.class)) {
                codec = (Codec<T, E>) floatCodec();
            } else if (dynType.equals(Double.class)) {
                codec = (Codec<T, E>) doubleCodec();
            } else if (dynType.equals(String.class)) {
                codec = (Codec<T, E>) stringCodec();
            } else if (Map.class.isAssignableFrom(dynType)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(dynType, Map.class);
                final Class<?> keyType = typeArgs.get(0);
                final Class<?> valueType = typeArgs.get(1);
                codec = (Codec<T, E>) mapCodec(keyType, valueType);
            } else if (Collection.class.isAssignableFrom(dynType)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(dynType, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    final Codec<Object, E> elemCodec = nullSafeCodec(dynamicCodec(elemType));
                    codec = (Codec<T, E>) collCodec(elemType, elemCodec);
                } else {
                    codec = createObjectCodec(dynType);
                }
            } else {
                codec = createObjectCodec(dynType);
            }

            return codec;
        }
    }

    public <T> Codec<T, E> createObjectCodec(Class<T> type) {
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

    public abstract <T> Codec<T, E> createObjectCodec(
            Class<T> type,
            Map<String, FieldCodec<E>> fieldCodecs);

    public String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "*" + name;
        }
        return name;
    }

    public <T> FieldCodec<E> getFieldCodec(Field field) {
        final Class<T> stcType = (Class<T>)field.getType();
        if (stcType.isPrimitive()) {
            if (stcType.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<E>(field, booleanCodec());
            } else if (stcType.equals(byte.class)) {
                return new FieldCodec.ByteFieldCodec<E>(field, byteCodec());
            } else if (stcType.equals(char.class)) {
                return new FieldCodec.CharFieldCodec<E>(field, charCodec());
            } else if (stcType.equals(short.class)) {
                return new FieldCodec.ShortFieldCodec<E>(field, shortCodec());
            } else if (stcType.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<E>(field, intCodec());
            } else if (stcType.equals(long.class)) {
                return new FieldCodec.LongFieldCodec<E>(field, longCodec());
            } else if (stcType.equals(float.class)) {
                return new FieldCodec.FloatFieldCodec<E>(field, floatCodec());
            } else if (stcType.equals(double.class)) {
                return new FieldCodec.DoubleFieldCodec<E>(field, doubleCodec());
            } else {
                throw new RuntimeException(stcType.getName());
            }
        } else {
            if (stcType.isArray()) {
                final Class<?> elemType = stcType.getComponentType();
                if (elemType.equals(boolean.class)) {
                    final Codec<boolean[], E> codec = getNullSafeCodec((Class<boolean[]>)stcType);
                    return new FieldCodec.BooleanArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(byte.class)) {
                    final Codec<byte[], E> codec = getNullSafeCodec((Class<byte[]>)stcType);
                    return new FieldCodec.ByteArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(char.class)) {
                    final Codec<char[], E> codec = getNullSafeCodec((Class<char[]>)stcType);
                    return new FieldCodec.CharArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(short.class)) {
                    final Codec<short[], E> codec = getNullSafeCodec((Class<short[]>)stcType);
                    return new FieldCodec.ShortArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(int.class)) {
                    final Codec<int[], E> codec = getNullSafeCodec((Class<int[]>)stcType);
                    return new FieldCodec.IntegerArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(long.class)) {
                    final Codec<long[], E> codec = getNullSafeCodec((Class<long[]>)stcType);
                    return new FieldCodec.LongArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(float.class)) {
                    final Codec<float[], E> codec = getNullSafeCodec((Class<float[]>)stcType);
                    return new FieldCodec.FloatArrayFieldCodec<E>(field, codec);
                } else if (elemType.equals(double.class)) {
                    final Codec<double[], E> codec = getNullSafeCodec((Class<double[]>)stcType);
                    return new FieldCodec.DoubleArrayFieldCodec<E>(field, codec);
                } else {
                    final Codec<T[], E> codec = getNullSafeCodec((Class<T[]>)stcType);
                    return new FieldCodec.ObjectArrayFieldCodec<>(field, codec);
                }
            } else {
                final Codec<T, E> codec;

                if (stcType.isEnum() ||
                        stcType.equals(Boolean.class) ||
                        stcType.equals(Byte.class) ||
                        stcType.equals(Character.class) ||
                        stcType.equals(Short.class) ||
                        stcType.equals(Integer.class) ||
                        stcType.equals(Long.class) ||
                        stcType.equals(Float.class) ||
                        stcType.equals(Double.class) ||
                        stcType.equals(String.class)) {
                    codec = getNullSafeCodec(stcType);
                } else if (Collection.class.isAssignableFrom(stcType)) {
                    final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Collection.class);
                    if (typeArgs.size() == 1) {
                        final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                        final Codec<Object, E> elemCodec = nullSafeCodec(dynamicCodec(elemType));
                        final Codec<Collection<Object>, E> collCodec = collCodec(elemType, elemCodec);
                        codec = nullSafeCodec(dynamicCheck((Codec) collCodec, stcType));
                    } else {
                        codec = nullSafeCodec(dynamicCodec((Class) stcType));
                    }
                } else if (Map.class.isAssignableFrom(stcType)) {
                    final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                    final Class keyType = typeArgs.get(0);
                    final Class valType = typeArgs.get(1);
                    final Codec<Map<?, ?>, E> mapCodec = mapCodec(keyType, valType);
                    codec = nullSafeCodec(dynamicCheck((Codec) mapCodec, stcType));
                } else {
                    codec = nullSafeCodec(dynamicCodec((Class) stcType));
                }

                return new FieldCodec.ObjectFieldCodec<>(field, codec);
            }
        }
    }

    public <T> Codec<T, E> dynamicCheck(Codec<T, E> codec, Class<T> stcType) {
        if (Modifier.isFinal(stcType.getModifiers())) {
            return codec;
        } else {
            return dynamicCodec(codec, stcType);
        }
    }
}
