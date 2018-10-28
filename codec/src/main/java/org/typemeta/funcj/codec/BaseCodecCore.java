package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.ReflectionUtils;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Base class for classes which implement an encoding into a specific target type.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
@SuppressWarnings("unchecked")
public abstract class BaseCodecCore<IN, OUT> implements CodecCoreInternal<IN, OUT> {

    /**
     * A map from class name to {@code Codec}, associating a class with its {@code Codec}.
     * Although {@code Codec}s can be registered by the caller prior to en/decoding,
     * the primary populator of the registry is this {@code CodecCore} implementation.
     * As and when new classes are encountered, they are inspected via Reflection,
     * and a {@code Codec} is constructed and registered.
     */
    protected final ConcurrentMap<String, Codec<?, IN, OUT>> codecRegistry = new ConcurrentHashMap<>();

    /**
     * A map from class name to {@code TypeConstructor}, associating a class with its {@code TypeConstructor}.
     * Although {@code TypeConstructor}s can be registered by the caller prior to en/decoding,
     * the primary populator of the registry is this {@code CodecCore} implementation.
     * As and when new classes are encountered, they are inspected via Reflection,
     * and a {@code TypeConstructor} is constructed and registered.
     */
    protected final ConcurrentMap<String, TypeConstructor<?>> typeCtorRegistry = new ConcurrentHashMap<>();

    /**
     * A map from class name to its type proxy, associating a class with its type proxy.
     */
    protected final Map<String, Class<?>> typeProxyRegistry = new HashMap<>();

    protected BaseCodecCore() {
    }

    @Override
    public <T> void registerCodec(Class<? extends T> clazz, Codec<T, IN, OUT> codec) {
        registerCodec(classToName(clazz), codec);
    }

    @Override
    public <T> void registerCodec(String name, Codec<T, IN, OUT> codec) {
        synchronized (codecRegistry) {
            codecRegistry.put(name, codec);
        }
    }

    @Override
    public <T> ObjectCodecBuilder<T, IN, OUT> registerCodec(Class<T> clazz) {
        return objectCodecDeferredRegister(clazz);
    }

    @Override
    public <T> void registerStringProxyCodec(
            Class<T> type,
            Functions.F<T, String> encode,
            Functions.F<String, T> decode) {
        registerCodec(type, new Codecs.StringProxyCodec<T, IN, OUT>(this, type, encode, decode));
    }

    @Override
    public void registerTypeProxy(Class<?> type, Class<?> proxyType) {
        registerTypeProxy(classToName(type), proxyType);
    }

    @Override
    public void registerTypeProxy(String typeName, Class<?> proxyType) {
        typeProxyRegistry.put(typeName, proxyType);
    }

    @Override
    public <T> void registerTypeConstructor(
            Class<? extends T> clazz,
            TypeConstructor<T> typeCtor) {
        typeCtorRegistry.put(classToName(clazz), typeCtor);
    }

    @Override
    public <T> OUT encode(Class<T> type, T val, OUT out) {
        return getCodec(remapType(type)).encodeWithCheck(val, out);
    }

    @Override
    public <T> T decode(Class<T> type, IN in) {
        return getCodec(remapType(type)).decodeWithCheck(in);
    }

    @Override
    public <T> Class<T> nameToClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new CodecException("Cannot create class from class name '" + name + "'", ex);
        }
    }

    /**
     * Map a class to a class name.
     * @param type      the class
     * @return          the class name
     */
    @Override
    public String classToName(Class<?> type) {
        return type.getName();
    }

    @Override
    public String classToName(Class<?> type, Class<?>... classes) {
        switch (classes.length) {
            case 1:
                return classToName(type) + '|' + classToName(classes[0]);
            case 2:
                return classToName(type) + '|' + classToName(classes[0])
                        + '|' + classToName(classes[1]);
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append(classToName(type)).append('|');
                for (Class<?> cls : classes) {
                    sb.append(classToName(cls)).append('|');
                }
                return sb.toString();
            }
        }
    }

    @Override
    public <X> Class<X> remapType(Class<X> type) {
        final String typeName = classToName(type);
        if (typeProxyRegistry.containsKey(typeName)) {
            return (Class<X>) typeProxyRegistry.get(typeName);
        } else {
            return type;
        }
    }

    @Override
    public <T> TypeConstructor<T> getTypeConstructor(Class<T> type) {
        final String name = classToName(type);
        return (TypeConstructor<T>) typeCtorRegistry.computeIfAbsent(
                name,
                n -> TypeConstructor.create(type));
    }
    /**
     * Lookup a {@code Codec} for a name, and, if one doesn't exist,
     * then create a new one.
     * <p>
     * This is slightly tricky as it needs to be re-entrant in case the
     * type in question is recursive.
     * @param type      the type
     * @param <T>       the raw type to be encoded/decoded
     * @return          the {@code Codec} for the specified name
     */
    @Override
    public <T> Codec<T, IN, OUT> getCodec(Class<T> type) {
        return getCodec(classToName(type), () -> createCodec(type));
    }

    /**
     * Lookup a {@code Codec} for a name, and, if one doesn't exist,
     * then create a new one.
     * <p>
     * This is slightly tricky as it needs to be re-entrant in case the
     * type in question is recursive.
     * @param name      the type name
     * @param <T>       the raw type to be encoded/decoded
     * @return          the {@code Codec} for the specified name
     */
    @Override
    public <T> Codec<T, IN, OUT> getCodec(
            String name,
            Supplier<Codec<T, IN, OUT>> supp) {
        // First attempt, without locking.
        if (codecRegistry.containsKey(name)) {
            return (Codec<T, IN, OUT>)codecRegistry.get(name);
        } else {
            final CodecRef<T, IN, OUT> codecRef;
            // Lock and try again.
            synchronized(codecRegistry) {
                if (codecRegistry.containsKey(name)) {
                    return (Codec<T, IN, OUT>) codecRegistry.get(name);
                } else {
                    // Ok, it's definitely not there, so add a CodecRef.
                    codecRef = new CodecRef<T, IN, OUT>();
                    codecRegistry.put(name, codecRef);
                }
            }

            // Initialise the CodecRef, and overwrite the registry entry with the real Codec.
            codecRegistry.put(name, codecRef.setIfUninitialised(supp::get));

            return (Codec<T, IN, OUT>)codecRegistry.get(name);
        }
    }

    @Override
    public <T> Codec<Collection<T>, IN, OUT> getCollCodec(
            Class<Collection<T>> collType,
            Codec<T, IN, OUT> elemCodec) {
        final String name = classToName(collType, elemCodec.type());
        return getCodec(name, () -> createCollCodec(collType, elemCodec));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT> getMapCodec(
            Class<Map<K, V>> mapType,
            Class<K> keyType,
            Class<V> valType) {
        final String name = classToName(mapType, keyType, valType);
        return getCodec(name, () -> createMapCodec(mapType, keyType, valType));
    }

    @Override
    public <V> Codec<Map<String, V>, IN, OUT> createMapCodec(
            Class<Map<String, V>> mapType,
            Class<V> valType) {
        final String name = classToName(mapType, String.class, valType);
        return getCodec(name, () -> createMapCodec(mapType, valType));
    }

    @Override
    public <V> Codec<Map<String, V>, IN, OUT> getMapCodec(
            Class<Map<String, V>> mapType,
            Class<V> valType) {
        final String name = classToName(mapType, String.class, valType);
        return getCodec(name, () -> createMapCodec(mapType, valType));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT> getMapCodec(
            Class<Map<K, V>> mapType,
            Codec<K, IN, OUT> keyCodec,
            Codec<V, IN, OUT> valueCodec) {
        final String name = classToName(mapType, keyCodec.type(), valueCodec.type());
        return getCodec(name, () -> createMapCodec(mapType, keyCodec, valueCodec));
    }

    @Override
    public <V> Codec<Map<String, V>, IN, OUT> getMapCodec(
            Class<Map<String, V>> mapType,
            Codec<V, IN, OUT> valueCodec) {
        final String name = classToName(mapType, String.class, valueCodec.type());
        return getCodec(name, () -> createMapCodec(mapType, valueCodec));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT> createMapCodec(
            Class<Map<K, V>> mapType,
            Class<K> keyType,
            Class<V> valType)  {
        final Codec<V, IN, OUT> valueCodec = getCodec(valType);
        if (String.class.equals(keyType)) {
            return (Codec)createMapCodec((Class<Map<String, V>>)(Class)mapType, valueCodec);
        } else {
            final Codec<K, IN, OUT> keyCodec = getCodec(keyType);
            return createMapCodec(mapType, keyCodec, valueCodec);
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> createCodec(Class<T> type) {
        if (type.isPrimitive()) {
            if (type.equals(boolean.class)) {
                return (Codec<T, IN, OUT>)booleanCodec();
            } else if (type.equals(byte.class)) {
                return (Codec<T, IN, OUT>) byteCodec();
            } else if (type.equals(char.class)) {
                return (Codec<T, IN, OUT>) charCodec();
            } else if (type.equals(short.class)) {
                return (Codec<T, IN, OUT>) shortCodec();
            } else if (type.equals(int.class)) {
                return (Codec<T, IN, OUT>) intCodec();
            } else if (type.equals(long.class)) {
                return (Codec<T, IN, OUT>) longCodec();
            } else if (type.equals(float.class)) {
                return (Codec<T, IN, OUT>) floatCodec();
            } else if (type.equals(double.class)) {
                return (Codec<T, IN, OUT>) doubleCodec();
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + type);
            }
        } else {
            if (type.isArray()) {
                final Class<?> elemType = type.getComponentType();
                if (elemType.equals(boolean.class)) {
                    return (Codec<T, IN, OUT>) booleanArrayCodec();
                } else if (elemType.equals(byte.class)) {
                    return (Codec<T, IN, OUT>) byteArrayCodec();
                } else if (elemType.equals(char.class)) {
                    return (Codec<T, IN, OUT>) charArrayCodec();
                } else if (elemType.equals(short.class)) {
                    return (Codec<T, IN, OUT>) shortArrayCodec();
                } else if (elemType.equals(int.class)) {
                    return (Codec<T, IN, OUT>) intArrayCodec();
                } else if (elemType.equals(long.class)) {
                    return (Codec<T, IN, OUT>) longArrayCodec();
                } else if (elemType.equals(float.class)) {
                    return (Codec<T, IN, OUT>) floatArrayCodec();
                } else if (elemType.equals(double.class)) {
                    return (Codec<T, IN, OUT>) doubleArrayCodec();
                } else {
                    if (elemType.equals(Boolean.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Boolean.class, booleanCodec());
                    } else if (elemType.equals(Byte.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Byte.class, byteCodec());
                    } else if (elemType.equals(Character.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Character.class, charCodec());
                    } else if (elemType.equals(Short.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Short.class, shortCodec());
                    } else if (elemType.equals(Integer.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Integer.class, intCodec());
                    } else if (elemType.equals(Long.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Long.class, longCodec());
                    } else if (elemType.equals(Float.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Float.class, floatCodec());
                    } else if (elemType.equals(Double.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, Double.class, doubleCodec());
                    } else {
                        final Codec<Object, IN, OUT> elemCodec = getCodec((Class<Object>)elemType);
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)type, (Class<Object>) elemType, elemCodec);
                    }
                }
            } else if (type.isEnum()) {
                return enumCodec((Class) type);
            } else if (type.equals(Boolean.class)) {
                return (Codec<T, IN, OUT>) booleanCodec();
            } else if (type.equals(Byte.class)) {
                return (Codec<T, IN, OUT>) byteCodec();
            } else if (type.equals(Character.class)) {
                return (Codec<T, IN, OUT>) charCodec();
            } else if (type.equals(Short.class)) {
                return (Codec<T, IN, OUT>) shortCodec();
            } else if (type.equals(Integer.class)) {
                return (Codec<T, IN, OUT>) intCodec();
            } else if (type.equals(Long.class)) {
                return (Codec<T, IN, OUT>) longCodec();
            } else if (type.equals(Float.class)) {
                return (Codec<T, IN, OUT>) floatCodec();
            } else if (type.equals(Double.class)) {
                return (Codec<T, IN, OUT>) doubleCodec();
            } else if (type.equals(String.class)) {
                return (Codec<T, IN, OUT>) stringCodec();
            } else if (Map.class.isAssignableFrom(type)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(type, Map.class);
                if (typeArgs.size() == 2) {
                    final Class keyType = typeArgs.get(0);
                    final Class valueType = typeArgs.get(1);
                    return (Codec<T, IN, OUT>) getMapCodec((Class)type, keyType, valueType);
                } else {
                    return (Codec<T, IN, OUT>) getMapCodec((Class)type, Object.class, Object.class);
                }
            } else if (Collection.class.isAssignableFrom(type)) {
                final Codec<Object, IN, OUT> elemCodec;
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(type, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    elemCodec = getCodec(elemType);
                } else {
                    elemCodec = getCodec(Object.class);
                }
                return (Codec<T, IN, OUT>) getCollCodec((Class<Collection<Object>>) type, elemCodec);
            } else {
                return createObjectCodec(type);
            }
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(Class<T> type) {
        final Map<String, FieldCodec<IN, OUT>> fieldCodecs = new LinkedHashMap<>();
        Class<?> type2 = type;
        for (int depth = 0; !type2.equals(Object.class); depth++) {
            final Field[] fields = type2.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = getFieldName(field, depth, fieldCodecs.keySet());
                    fieldCodecs.put(fieldName, createFieldCodec(field));
                }
            }
            type2 = type2.getSuperclass();
        }

        return createObjectCodec(type, fieldCodecs);
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(
            Class<T> type,
            Map<String, FieldCodec<IN, OUT>> fieldCodecs) {
        final class ResultAccumlatorImpl implements ObjectMeta.ResultAccumlator<T> {
            final T val;

            ResultAccumlatorImpl(Class<T> type) {
                this.val = getTypeConstructor(type).construct();
            }

            @Override
            public T construct() {
                return val;
            }
        }

        final List<ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>> fieldMetas =
                fieldCodecs.entrySet().stream()
                        .map(en -> {
                            final String name = en.getKey();
                            final FieldCodec<IN, OUT> codec = en.getValue();
                            return new ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>() {
                                @Override
                                public String name() {
                                    return name;
                                }

                                @Override
                                public OUT encodeField(T val, OUT out) {
                                    return codec.encodeField(val, out);
                                }

                                @Override
                                public ResultAccumlatorImpl decodeField(ResultAccumlatorImpl acc, IN in) {
                                    codec.decodeField(acc.val, in);
                                    return acc;
                                }
                            };
                        }).collect(toList());

        return createObjectCodec(
                type,
                new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {
                        @Override
                        public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                            return fieldMetas.iterator();
                        }

                        @Override
                        public ResultAccumlatorImpl startDecode() {
                            return new ResultAccumlatorImpl(type);
                        }

                        @Override
                        public int size() {
                            return fieldMetas.size();
                        }
                }
        );
    }

    @Override
    public <T> ObjectCodecBuilder<T, IN, OUT> createObjectCodecBuilder(Class<T> type) {
        return new ObjectCodecBuilder<T, IN, OUT>(this, type);
    }

    @Override
    public <T> ObjectCodecBuilder<T, IN, OUT> objectCodecDeferredRegister(Class<T> type) {
        return new ObjectCodecBuilder<T, IN, OUT>(this, type) {
            @Override
            protected Codec<T, IN, OUT> registration(Codec<T, IN, OUT> codec) {
                registerCodec(type, codec);
                return codec;
            }
        };
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(
            Class<T> type,
            Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT>> fieldCodecs,
            Functions.F<Object[], T> ctor) {
        final class ResultAccumlatorImpl implements ObjectMeta.ResultAccumlator<T> {
            final Object[] ctorArgs;
            int i = 0;

            ResultAccumlatorImpl(Class<T> type) {
                this.ctorArgs = new Object[fieldCodecs.size()];
            }

            @Override
            public T construct() {
                return ctor.apply(ctorArgs);
            }
        }

        final List<ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>> fieldMetas =
                fieldCodecs.entrySet().stream()
                        .map(en -> {
                            final String name = en.getKey();
                            final ObjectCodecBuilder.FieldCodec<T, IN, OUT> codec = en.getValue();
                            return new ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>() {
                                @Override
                                public String name() {
                                    return name;
                                }

                                @Override
                                public OUT encodeField(T val, OUT out) {
                                    return codec.encodeField(val, out);
                                }

                                @Override
                                public ResultAccumlatorImpl decodeField(ResultAccumlatorImpl acc, IN in) {
                                    acc.ctorArgs[acc.i++] = codec.decodeField(in);
                                    return acc;
                                }
                            };
                        }).collect(toList());

        return createObjectCodec(
                type,
                new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {
                        @Override
                        public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                            return fieldMetas.iterator();
                        }

                        @Override
                        public ResultAccumlatorImpl startDecode() {
                            return new ResultAccumlatorImpl(type);
                        }

                        @Override
                        public int size() {
                            return fieldMetas.size();
                        }
                }
        );
    }

    @Override
    public String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "*" + name;
        }
        return name;
    }

    @Override
    public <T> FieldCodec<IN, OUT> createFieldCodec(Field field) {
        final Class<T> type = (Class<T>)field.getType();
        if (type.isPrimitive()) {
            if (type.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<IN, OUT>(field, booleanCodec());
            } else if (type.equals(byte.class)) {
                return new FieldCodec.ByteFieldCodec<IN, OUT>(field, byteCodec());
            } else if (type.equals(char.class)) {
                return new FieldCodec.CharFieldCodec<IN, OUT>(field, charCodec());
            } else if (type.equals(short.class)) {
                return new FieldCodec.ShortFieldCodec<IN, OUT>(field, shortCodec());
            } else if (type.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<IN, OUT>(field, intCodec());
            } else if (type.equals(long.class)) {
                return new FieldCodec.LongFieldCodec<IN, OUT>(field, longCodec());
            } else if (type.equals(float.class)) {
                return new FieldCodec.FloatFieldCodec<IN, OUT>(field, floatCodec());
            } else if (type.equals(double.class)) {
                return new FieldCodec.DoubleFieldCodec<IN, OUT>(field, doubleCodec());
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + type);
            }
        } else if (type.isArray()) {
            final Class<?> elemType = type.getComponentType();
            if (elemType.equals(boolean.class)) {
                return new FieldCodec.BooleanArrayFieldCodec<IN, OUT>(field, booleanArrayCodec());
            } else if (elemType.equals(byte.class)) {
                return new FieldCodec.ByteArrayFieldCodec<IN, OUT>(field, byteArrayCodec());
            } else if (elemType.equals(char.class)) {
                return new FieldCodec.CharArrayFieldCodec<IN, OUT>(field, charArrayCodec());
            } else if (elemType.equals(short.class)) {
                return new FieldCodec.ShortArrayFieldCodec<IN, OUT>(field, shortArrayCodec());
            } else if (elemType.equals(int.class)) {
                return new FieldCodec.IntegerArrayFieldCodec<IN, OUT>(field, intArrayCodec());
            } else if (elemType.equals(long.class)) {
                return new FieldCodec.LongArrayFieldCodec<IN, OUT>(field, longArrayCodec());
            } else if (elemType.equals(float.class)) {
                return new FieldCodec.FloatArrayFieldCodec<IN, OUT>(field, floatArrayCodec());
            } else if (elemType.equals(double.class)) {
                return new FieldCodec.DoubleArrayFieldCodec<IN, OUT>(field, doubleArrayCodec());
            } else {
                final Codec<Object[], IN, OUT> codec = getCodec((Class<Object[]>)type);
                return new FieldCodec.ObjectArrayFieldCodec<>(field, codec);
            }
        } else {
            Codec<T, IN, OUT> codec;

            if (type.isEnum() ||
                    type.equals(Boolean.class) ||
                    type.equals(Byte.class) ||
                    type.equals(Character.class) ||
                    type.equals(Short.class) ||
                    type.equals(Integer.class) ||
                    type.equals(Long.class) ||
                    type.equals(Float.class) ||
                    type.equals(Double.class) ||
                    type.equals(String.class)) {
                codec = getCodec(type);
            } else if (Map.class.isAssignableFrom(type)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                if (typeArgs.size() == 2) {
                    final Class keyType = typeArgs.get(0);
                    final Class valueType = typeArgs.get(1);
                    codec = (Codec<T, IN, OUT>) getMapCodec((Class)type, keyType, valueType);
                } else {
                    codec = (Codec<T, IN, OUT>) getMapCodec((Class)type, Object.class, Object.class);
                }
            } else if (Collection.class.isAssignableFrom(type)) {
                final Codec<Object, IN, OUT> elemCodec;
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    elemCodec = getCodec(elemType);
                } else {
                    elemCodec = getCodec(Object.class);
                }
                codec = (Codec<T, IN, OUT>) getCollCodec((Class<Collection<Object>>) type, elemCodec);
            } else {
                codec = getCodec(type);
            }

            return new FieldCodec.ObjectFieldCodec<>(field, codec);
        }
    }
}
