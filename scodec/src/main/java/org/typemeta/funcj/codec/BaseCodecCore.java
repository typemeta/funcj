package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.ReflectionUtils;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

/**
 * Base class for classes which implement an encoding into a specific target type.
 * @param <IN, OUT> the encoded type
 */
@SuppressWarnings("unchecked")
public abstract class BaseCodecCore<IN, OUT> implements CodecCoreIntl<IN, OUT> {

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
            Class<T> clazz,
            Functions.F<T, String> encode,
            Functions.F<String, T> decode) {
        registerCodec(clazz, new Codecs.StringProxyCodec<T, IN, OUT>(this, encode, decode));
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
    public <T> OUT encode(T val, OUT out) {
        return encode((Class<T>)val.getClass(), val, out);
    }

    @Override
    public <T> OUT encode(Class<T> type, T val, OUT out) {
        return dynamicCodec(type).encode(val, out);
    }

    @Override
    public <T> T decode(Class<T> type, IN in) {
        return dynamicCodec(type).decode(in);
    }

    /**
     * Map a class to a class name.
     * This method exists primarily to allow it to be overridden in one place.
     * @param clazz the class
     * @return the class name
     */
    @Override
    public String classToName(Class<?> clazz) {
        return clazz.getName();
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
    public <T> Class<T> nameToClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new CodecException("Cannot create class from class name '" + name + "'", ex);
        }
    }

    @Override
    public <T> TypeConstructor<T> getTypeConstructor(Class<T> clazz) {
        final String name = classToName(clazz);
        return (TypeConstructor<T>) typeCtorRegistry.computeIfAbsent(
                name,
                n -> TypeConstructor.create(clazz));
    }

    @Override
    public <T> Codec<T, IN, OUT> makeNullSafeCodec(Codec<T, IN, OUT> codec) {
        final Codec.NullCodec<IN, OUT> nullCodec = nullCodec();
        return new Codec<T, IN, OUT>() {
            @Override
            public OUT encode(T val, OUT out) {
                if (val == null) {
                    return nullCodec.encode(null, out);
                } else {
                    return codec.encode(val, out);
                }
            }

            @Override
            public T decode(Class<T> dynType, IN in) {
                if (nullCodec.isNull(in)) {
                    return (T)nullCodec.decode(in);
                } else {
                    return codec.decode(dynType, in);
                }
            }

            @Override
            public T decode(IN in) {
                if (nullCodec.isNull(in)) {
                    return (T)nullCodec.decode(in);
                } else {
                    return codec.decode(in);
                }
            }
        };
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT> mapCodec(Class<K> keyType, Class<V> valType)  {
        final Codec<V, IN, OUT> valueCodec = dynamicCodec(valType);
        if (String.class.equals(keyType)) {
            return (Codec)mapCodec(valueCodec);
        } else {
            final Codec<K, IN, OUT> keyCodec = dynamicCodec(keyType);
            return mapCodec(keyCodec, valueCodec);
        }
    }

    @Override
    public Codec<Object, IN, OUT> dynamicCodec() {
        return dynamicCodec(Object.class);
    }

    @Override
    public <T> Codec<T, IN, OUT> getNullSafeCodec(Class<T> type) {
        return makeNullSafeCodec(getNullUnsafeCodec(type));
    }

    /**
     * Lookup a {@code Codec} for a name, and, if one doesn't exist,
     * then use the {@code codecSupp} to create a new one.
     * <p>
     * This is slightly tricky as it needs to be re-entrant in case the
     * type in question is recursive.
     * I.e. {@code codecSupp}, when invoked, may call this method again for the same type.
     * @param name      the type name
     * @param codecSupp a supplier of the {@code Codec} value
     * @param <T>       the raw type to be encoded/decoded
     * @return          the {@code Codec} for the specified name
     */
    @Override
    public <T> Codec<T, IN, OUT> getCodec(String name, Functions.F0<Codec<T, IN, OUT>> codecSupp) {
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
            codecRegistry.put(name, codecRef.setIfUninitialised(codecSupp));

            return (Codec<T, IN, OUT>)codecRegistry.get(name);
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> getNullUnsafeCodec(Class<T> type) {
        final Class<T> type2 = remapType(type);
        final String name = classToName(type2);
        return getCodec(name, () -> getNullUnsafeCodecImplDyn(type2));
    }

    @Override
    public <T> Codec<T, IN, OUT> getNullUnsafeCodecImplDyn(Class<T> dynType) {
        final Codec<T, IN, OUT> codec = getNullUnsafeCodecImpl(dynType);
        if (codec == null) {
            return createObjectCodec(dynType);
        } else {
            return codec;
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> getNullUnsafeCodecImplStc(Class<T> stcType) {
        final Codec<T, IN, OUT> codec = getNullUnsafeCodecImpl(stcType);
        if (codec == null) {
            if (Modifier.isFinal(stcType.getModifiers())) {
                final String name = classToName(stcType);
                return getCodec(name, () -> createObjectCodec(stcType));
            } else {
                return dynamicCodec(stcType);
            }
        } else {
            return codec;
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> getNullUnsafeCodecImpl(Class<T> type) {
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
                        return (Codec<T, IN, OUT>) objectArrayCodec(Boolean.class, booleanCodec());
                    } else if (elemType.equals(Byte.class)) {
                        return (Codec<T, IN, OUT>) objectArrayCodec(Byte.class, byteCodec());
                    } else if (elemType.equals(Character.class)) {
                        return (Codec<T, IN, OUT>) objectArrayCodec(Character.class, charCodec());
                    } else if (elemType.equals(Short.class)) {
                        return (Codec<T, IN, OUT>) objectArrayCodec(Short.class, shortCodec());
                    } else if (elemType.equals(Integer.class)) {
                        return (Codec<T, IN, OUT>) objectArrayCodec(Integer.class, intCodec());
                    } else if (elemType.equals(Long.class)) {
                        return (Codec<T, IN, OUT>) objectArrayCodec(Long.class, longCodec());
                    } else if (elemType.equals(Float.class)) {
                        return (Codec<T, IN, OUT>) objectArrayCodec(Float.class, floatCodec());
                    } else if (elemType.equals(Double.class)) {
                        return (Codec<T, IN, OUT>) objectArrayCodec(Double.class, doubleCodec());
                    } else {
                        final Codec<Object, IN, OUT> elemCodec = makeNullSafeCodec(dynamicCodec((Class<Object>) elemType));
                        return (Codec<T, IN, OUT>) objectArrayCodec((Class<Object>) elemType, elemCodec);
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
                final Class<?> keyType = typeArgs.get(0);
                final Class<?> valueType = typeArgs.get(1);
                return dynamicCheck((Codec<T, IN, OUT>) mapCodec(keyType, valueType), type);
            } else if (Collection.class.isAssignableFrom(type)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(type, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    final Codec<Object, IN, OUT> elemCodec = makeNullSafeCodec(dynamicCodec(elemType));
                    return dynamicCheck((Codec<T, IN, OUT>) collCodec(elemType, elemCodec), type);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(Class<T> type) {
        final Map<String, FieldCodec<IN, OUT>> fieldCodecs = new LinkedHashMap<>();
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

        return createObjectCodec(fieldCodecs);
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(Map<String, FieldCodec<IN, OUT>> fieldCodecs) {
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
                            return (ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>)new ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>() {
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

        return createObjectCodec(new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {

            @Override
            public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                return fieldMetas.iterator();
            }

            @Override
            public ResultAccumlatorImpl startDecode(Class<T> type) {
                return new ResultAccumlatorImpl(type);
            }

            @Override
            public int size() {
                return fieldMetas.size();
            }
        });
    }

    @Override
    public <T> ObjectCodecBuilder<T, IN, OUT> objectCodec(Class<T> clazz) {
        return new ObjectCodecBuilder<T, IN, OUT>(this);
    }

    @Override
    public <T> ObjectCodecBuilder<T, IN, OUT> objectCodecDeferredRegister(Class<T> clazz) {
        return new ObjectCodecBuilder<T, IN, OUT>(this) {
            @Override
            protected Codec<T, IN, OUT> registration(Codec<T, IN, OUT> codec) {
                registerCodec(clazz, codec);
                return codec;
            }
        };
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(
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

        final List<ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>> fieldMetas = fieldCodecs.entrySet().stream()
                .map(en -> {
                    final String name = en.getKey();
                    final ObjectCodecBuilder.FieldCodec<T, IN, OUT> codec = en.getValue();
                    return (ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>)new ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>() {
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


        return createObjectCodec(new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {

            @Override
            public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                return fieldMetas.iterator();
            }

            @Override
            public ResultAccumlatorImpl startDecode(Class<T> type) {
                return new ResultAccumlatorImpl(type);
            }

            @Override
            public int size() {
                return fieldMetas.size();
            }
        });
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
    public <T> FieldCodec<IN, OUT> getFieldCodec(Field field) {
        final Class<T> stcType = (Class<T>)field.getType();
        if (stcType.isPrimitive()) {
            if (stcType.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<IN, OUT>(field, booleanCodec());
            } else if (stcType.equals(byte.class)) {
                return new FieldCodec.ByteFieldCodec<IN, OUT>(field, byteCodec());
            } else if (stcType.equals(char.class)) {
                return new FieldCodec.CharFieldCodec<IN, OUT>(field, charCodec());
            } else if (stcType.equals(short.class)) {
                return new FieldCodec.ShortFieldCodec<IN, OUT>(field, shortCodec());
            } else if (stcType.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<IN, OUT>(field, intCodec());
            } else if (stcType.equals(long.class)) {
                return new FieldCodec.LongFieldCodec<IN, OUT>(field, longCodec());
            } else if (stcType.equals(float.class)) {
                return new FieldCodec.FloatFieldCodec<IN, OUT>(field, floatCodec());
            } else if (stcType.equals(double.class)) {
                return new FieldCodec.DoubleFieldCodec<IN, OUT>(field, doubleCodec());
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + stcType);
            }
        } else {
            if (stcType.isArray()) {
                final Class<?> elemType = stcType.getComponentType();
                if (elemType.equals(boolean.class)) {
                    final Codec<boolean[], IN, OUT> codec = getNullSafeCodec((Class<boolean[]>)stcType);
                    return new FieldCodec.BooleanArrayFieldCodec<IN, OUT>(field, codec);
                } else if (elemType.equals(byte.class)) {
                    final Codec<byte[], IN, OUT> codec = getNullSafeCodec((Class<byte[]>)stcType);
                    return new FieldCodec.ByteArrayFieldCodec<IN, OUT>(field, codec);
                } else if (elemType.equals(char.class)) {
                    final Codec<char[], IN, OUT> codec = getNullSafeCodec((Class<char[]>)stcType);
                    return new FieldCodec.CharArrayFieldCodec<IN, OUT>(field, codec);
                } else if (elemType.equals(short.class)) {
                    final Codec<short[], IN, OUT> codec = getNullSafeCodec((Class<short[]>)stcType);
                    return new FieldCodec.ShortArrayFieldCodec<IN, OUT>(field, codec);
                } else if (elemType.equals(int.class)) {
                    final Codec<int[], IN, OUT> codec = getNullSafeCodec((Class<int[]>)stcType);
                    return new FieldCodec.IntegerArrayFieldCodec<IN, OUT>(field, codec);
                } else if (elemType.equals(long.class)) {
                    final Codec<long[], IN, OUT> codec = getNullSafeCodec((Class<long[]>)stcType);
                    return new FieldCodec.LongArrayFieldCodec<IN, OUT>(field, codec);
                } else if (elemType.equals(float.class)) {
                    final Codec<float[], IN, OUT> codec = getNullSafeCodec((Class<float[]>)stcType);
                    return new FieldCodec.FloatArrayFieldCodec<IN, OUT>(field, codec);
                } else if (elemType.equals(double.class)) {
                    final Codec<double[], IN, OUT> codec = getNullSafeCodec((Class<double[]>)stcType);
                    return new FieldCodec.DoubleArrayFieldCodec<IN, OUT>(field, codec);
                } else {
                    final Codec<T[], IN, OUT> codec = getNullSafeCodec((Class<T[]>)stcType);
                    return new FieldCodec.ObjectArrayFieldCodec<>(field, codec);
                }
            } else {
                Codec<T, IN, OUT> codec = null;

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
                        final Codec<Object, IN, OUT> elemCodec = makeNullSafeCodec(getNullUnsafeCodecImplStc(elemType));
                        final Codec<Collection<Object>, IN, OUT> collCodec = collCodec(elemType, elemCodec);
                        codec = makeNullSafeCodec(dynamicCheck((Codec) collCodec, stcType));
                    }
                } else if (Map.class.isAssignableFrom(stcType)) {
                    final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                    final Class keyType = typeArgs.get(0);
                    final Class valType = typeArgs.get(1);
                    final Codec<Map<?, ?>, IN, OUT> mapCodec = mapCodec(keyType, valType);
                    codec = makeNullSafeCodec(dynamicCheck((Codec) mapCodec, stcType));
                }

                if (codec == null) {
                    codec = makeNullSafeCodec(getNullUnsafeCodecImplStc(stcType));
                }

                return new FieldCodec.ObjectFieldCodec<>(field, codec);
            }
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> dynamicCheck(Codec<T, IN, OUT> codec, Class<T> stcType) {
        if (Modifier.isFinal(stcType.getModifiers())) {
            return codec;
        } else {
            return dynamicCodec(codec, stcType);
        }
    }
}
