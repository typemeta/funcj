package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.utils.ReflectionUtils;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Base class for classes which implement an encoding into a specific target type.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
@SuppressWarnings("unchecked")
public abstract class BaseCodecCore<IN, OUT> implements CodecCoreInternal<IN, OUT> {

    /**
     * A map that associates a class with a {@code Codec}.
     * Although {@code Codec}s can be registered by the caller prior to en/decoding,
     * the primary populator of the registry is this {@code CodecCore} implementation.
     * As and when new classes are encountered, they are inspected via Reflection,
     * and a {@code Codec} is constructed and registered.
     */
    protected final ConcurrentMap<ClassKey<?>, Codec<?, IN, OUT>> codecRegistry = new ConcurrentHashMap<>();

    /**
     * A map that associates a class with a {@code NoArgsCtor}.
     * Although {@code TypeConstructor}s can be registered by the caller prior to en/decoding,
     * the primary populator of the registry is this {@code CodecCore} implementation.
     * As and when new classes are encountered, they are inspected via Reflection,
     * and a {@code TypeConstructor} is constructed and registered.
     */
    protected final ConcurrentMap<ClassKey<?>, NoArgsTypeCtor<?>> noArgsCtorRegistry = new ConcurrentHashMap<>();

    protected final ConcurrentMap<ClassKey<?>, ArgArrayTypeCtor<?>> argArrayCtorRegistry = new ConcurrentHashMap<>();

    @Override
    public <T> void registerCodec(Class<? extends T> clazz, Codec<T, IN, OUT> codec) {
        config().registerAllowedClass(clazz);
        synchronized (codecRegistry) {
            codecRegistry.put(ClassKey.valueOf(clazz), codec);
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
        config().registerAllowedClass(clazz);
        registerCodec(clazz, new Codecs.StringProxyCodec<T, IN, OUT>(this, clazz, encode, decode));
    }

    @Override
    public <T> void registerNoArgsCtor(
            Class<? extends T> clazz,
            NoArgsTypeCtor<T> typeCtor) {
        config().registerAllowedClass(clazz);
        noArgsCtorRegistry.put(ClassKey.valueOf(clazz), typeCtor);
    }

    @Override
    public <T> void registerArgArrayCtor(
            Class<? extends T> clazz,
            ArgArrayTypeCtor<T> typeCtor) {
        config().registerAllowedClass(clazz);
        argArrayCtorRegistry.put(ClassKey.valueOf(clazz), typeCtor);
    }

    @Override
    public <T> OUT encode(Class<? super T> clazz, T val, OUT out) {
        return getCodec(clazz).encodeWithCheck(val, out);
    }

    @Override
    public <T> T decode(Class<? super T> clazz, IN in) {
        return (T)getCodec(clazz).decodeWithCheck(in);
    }

    @Override
    public <T> Optional<NoArgsTypeCtor<T>> getNoArgsCtorOpt(Class<T> clazz) {
        final ClassKey<T> key = ClassKey.valueOf(clazz);
        NoArgsTypeCtor<T> ctor = (NoArgsTypeCtor<T>)noArgsCtorRegistry.get(key);

        if (ctor == null) {
            final Optional<NoArgsTypeCtor<T>> newCtor = NoArgsTypeCtor.create(clazz);
            if (newCtor.isPresent()) {
                ctor = (NoArgsTypeCtor<T>) noArgsCtorRegistry.putIfAbsent(key, newCtor.get());
                if (ctor == null) {
                    return newCtor;
                } else {
                    return Optional.of(ctor);
                }
            } else {
                return newCtor;
            }
        } else {
            return Optional.of(ctor);
        }
    }
    @Override
    public <T> NoArgsTypeCtor<T> getNoArgsCtor(Class<T> clazz) {
        return getNoArgsCtorOpt(clazz)
                .orElseThrow(() ->
                        new CodecException("A no-args constructor was not found for class " + clazz)
                );
    }


    @Override
    public <T> Optional<ArgArrayTypeCtor<T>> getArgArrayCtorOpt(Class<T> clazz) {
        final ClassKey<T> key = ClassKey.valueOf(clazz);
        return Optional.ofNullable((ArgArrayTypeCtor<T>)argArrayCtorRegistry.get(key));
    }

    @Override
    public <T> Codec<T, IN, OUT> getCodec(Class<T> clazz) {
        return getCodec(
                ClassKey.valueOf(config().remapType(clazz)),
                () -> createCodec(clazz)
        );
    }

    @Override
    public <T> Codec<T, IN, OUT> getCodec(
            ClassKey<T> key,
            Supplier<Codec<T, IN, OUT>> codecSupp) {
        // First attempt, without locking.
        if (codecRegistry.containsKey(key)) {
            return (Codec<T, IN, OUT>)codecRegistry.get(key);
        } else {
            final CodecRef<T, IN, OUT> codecRef;
            // Lock and try again.
            synchronized(codecRegistry) {
                if (codecRegistry.containsKey(key)) {
                    return (Codec<T, IN, OUT>) codecRegistry.get(key);
                } else {
                    // Ok, it's definitely not there, so add a CodecRef.
                    codecRef = new CodecRef<T, IN, OUT>();
                    codecRegistry.put(key, codecRef);
                }
            }

            // Initialise the CodecRef, and overwrite the registry entry with the real Codec.
            codecRegistry.put(key, codecRef.setIfUninitialised(codecSupp::get));

            return (Codec<T, IN, OUT>)codecRegistry.get(key);
        }
    }

    @Override
    public <T> Codec<Collection<T>, IN, OUT> getCollCodec(
            Class<Collection<T>> collType,
            Codec<T, IN, OUT> elemCodec) {
        final ClassKey<Collection<T>> key = ClassKey.valueOf(collType, elemCodec.type());
        return getCodec(key, () -> createCollCodec(collType, elemCodec));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT> getMapCodec(
            Class<Map<K, V>> mapType,
            Class<K> keyType,
            Class<V> valType) {
        final ClassKey<Map<K, V>> key = ClassKey.valueOf(mapType, keyType, valType);
        return getCodec(key, () -> createMapCodec(mapType, keyType, valType));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT> getMapCodec(
            Class<Map<K, V>> mapType,
            Codec<K, IN, OUT> keyCodec,
            Codec<V, IN, OUT> valueCodec) {
        final ClassKey<Map<K, V>> key = ClassKey.valueOf(mapType, keyCodec.type(), valueCodec.type());
        return getCodec(key, () -> createMapCodec(mapType, keyCodec, valueCodec));
    }

    @Override
    public <V> Codec<Map<String, V>, IN, OUT> getMapCodec(
            Class<Map<String, V>> mapType,
            Codec<V, IN, OUT> valueCodec) {
        final ClassKey<Map<String, V>> key = ClassKey.valueOf(mapType, String.class, valueCodec.type());
        return getCodec(key, () -> createMapCodec(mapType, valueCodec));
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
    public <T> Codec<T, IN, OUT> createCodec(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return (Codec<T, IN, OUT>)booleanCodec();
            } else if (clazz.equals(byte.class)) {
                return (Codec<T, IN, OUT>) byteCodec();
            } else if (clazz.equals(char.class)) {
                return (Codec<T, IN, OUT>) charCodec();
            } else if (clazz.equals(short.class)) {
                return (Codec<T, IN, OUT>) shortCodec();
            } else if (clazz.equals(int.class)) {
                return (Codec<T, IN, OUT>) intCodec();
            } else if (clazz.equals(long.class)) {
                return (Codec<T, IN, OUT>) longCodec();
            } else if (clazz.equals(float.class)) {
                return (Codec<T, IN, OUT>) floatCodec();
            } else if (clazz.equals(double.class)) {
                return (Codec<T, IN, OUT>) doubleCodec();
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + clazz);
            }
        } else {
            if (clazz.isArray()) {
                final Class<?> elemType = clazz.getComponentType();
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
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Boolean.class, booleanCodec());
                    } else if (elemType.equals(Byte.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Byte.class, byteCodec());
                    } else if (elemType.equals(Character.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Character.class, charCodec());
                    } else if (elemType.equals(Short.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Short.class, shortCodec());
                    } else if (elemType.equals(Integer.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Integer.class, intCodec());
                    } else if (elemType.equals(Long.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Long.class, longCodec());
                    } else if (elemType.equals(Float.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Float.class, floatCodec());
                    } else if (elemType.equals(Double.class)) {
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, Double.class, doubleCodec());
                    } else {
                        final Codec<Object, IN, OUT> elemCodec = getCodec((Class<Object>)elemType);
                        return (Codec<T, IN, OUT>) createObjectArrayCodec((Class)clazz, (Class<Object>) elemType, elemCodec);
                    }
                }
            } else if (clazz.isEnum()) {
                return enumCodec((Class) clazz);
            } else if (clazz.equals(Boolean.class)) {
                return (Codec<T, IN, OUT>) booleanCodec();
            } else if (clazz.equals(Byte.class)) {
                return (Codec<T, IN, OUT>) byteCodec();
            } else if (clazz.equals(Character.class)) {
                return (Codec<T, IN, OUT>) charCodec();
            } else if (clazz.equals(Short.class)) {
                return (Codec<T, IN, OUT>) shortCodec();
            } else if (clazz.equals(Integer.class)) {
                return (Codec<T, IN, OUT>) intCodec();
            } else if (clazz.equals(Long.class)) {
                return (Codec<T, IN, OUT>) longCodec();
            } else if (clazz.equals(Float.class)) {
                return (Codec<T, IN, OUT>) floatCodec();
            } else if (clazz.equals(Double.class)) {
                return (Codec<T, IN, OUT>) doubleCodec();
            } else if (clazz.equals(String.class)) {
                return (Codec<T, IN, OUT>) stringCodec();
            } else if (Map.class.isAssignableFrom(clazz)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Map.class);
                if (typeArgs.size() == 2) {
                    final Class<?> keyType = typeArgs.get(0);
                    final Class<?> valueType = typeArgs.get(1);
                    return (Codec<T, IN, OUT>) getMapCodec((Class)clazz, keyType, valueType);
                } else {
                    return (Codec<T, IN, OUT>) getMapCodec((Class)clazz, Object.class, Object.class);
                }
            } else if (Collection.class.isAssignableFrom(clazz)) {
                final Codec<Object, IN, OUT> elemCodec;
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    elemCodec = getCodec(elemType);
                } else {
                    elemCodec = getCodec(Object.class);
                }
                return (Codec<T, IN, OUT>) getCollCodec((Class<Collection<Object>>) clazz, elemCodec);
            } else {
                return createObjectCodec(clazz);
            }
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(Class<T> clazz) {
        final Optional<NoArgsTypeCtor<T>> ctorOpt = getNoArgsCtorOpt(clazz);
        if (ctorOpt.isPresent()) {
            return createObjectCodec(clazz, ctorOpt.get());
        } else {
            final ArgArrayTypeCtor<T> ctor = getArgArrayCtorOpt(clazz)
                    .orElseThrow(() -> new CodecException("No constructore was found for class " + clazz));
            return createObjectCodec(clazz, ctor);
        }
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(Class<T> clazz, NoArgsTypeCtor<T> ctor) {
        final Map<String, FieldCodec<IN, OUT>> fieldCodecs = new LinkedHashMap<>();
        Class<?> clazz2 = clazz;
        for (int depth = 0; !clazz2.equals(Object.class); depth++) {
            final Field[] fields = clazz2.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = config().getFieldName(field, depth, fieldCodecs.keySet());
                    fieldCodecs.put(fieldName, createFieldCodec(field));
                }
            }
            clazz2 = clazz2.getSuperclass();
        }

        return createObjectCodec(clazz, fieldCodecs, ctor);
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(
            Class<T> clazz,
            Map<String, FieldCodec<IN, OUT>> fieldCodecs,
            NoArgsTypeCtor<T> ctor) {
        final class ResultAccumlatorImpl implements ObjectMeta.ResultAccumlator<T> {
            final T val;

            ResultAccumlatorImpl(Class<T> clazz) {
                this.val = ctor.construct();
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
                clazz,
                new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {
                        @Override
                        public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                            return fieldMetas.iterator();
                        }

                        @Override
                        public ResultAccumlatorImpl startDecode() {
                            return new ResultAccumlatorImpl(clazz);
                        }

                        @Override
                        public int size() {
                            return fieldMetas.size();
                        }
                }
        );
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(Class<T> clazz, ArgArrayTypeCtor<T> ctor) {
        Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT>> fieldCodecs = new LinkedHashMap<>();
        Class<?> clazz2 = clazz;
        for (int depth = 0; !clazz2.equals(Object.class); depth++) {
            final Field[] fields = clazz2.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = config().getFieldName(field, depth, fieldCodecs.keySet());
                    final Codec<?, IN, OUT> codec = createCodec(field.getType());
                    final ObjectCodecBuilder.FieldCodec<T, IN, OUT> fcodec = createFieldCodec(field, codec);
                    fieldCodecs.put(fieldName, fcodec);
                }
            }
            clazz2 = clazz2.getSuperclass();
        }

        return createObjectCodec(clazz, fieldCodecs, ctor);
    }

    <T, FT> ObjectCodecBuilder.FieldCodec<T, IN, OUT> createFieldCodec(
            Field field,
            Codec<FT, IN, OUT> codec
    ) {
        return new ObjectCodecBuilder.FieldCodec<T, IN, OUT>(
                t -> CodecException.wrap(() -> (FT)field.get(t)),
                codec
        );
    }

    @Override
    public <T> ObjectCodecBuilder<T, IN, OUT> objectCodecDeferredRegister(Class<T> clazz) {
        return new ObjectCodecBuilder<T, IN, OUT>(this, clazz) {
            @Override
            protected Codec<T, IN, OUT> registration(Codec<T, IN, OUT> codec) {
                registerCodec(clazz, codec);
                return codec;
            }
        };
    }

    @Override
    public <T> Codec<T, IN, OUT> createObjectCodec(
            Class<T> clazz,
            Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT>> fieldCodecs,
            ArgArrayTypeCtor<T> ctor) {
        final class ResultAccumlatorImpl implements ObjectMeta.ResultAccumlator<T> {
            final Object[] ctorArgs;
            int i = 0;

            ResultAccumlatorImpl(Class<T> clazz) {
                this.ctorArgs = new Object[fieldCodecs.size()];
            }

            @Override
            public T construct() {
                return ctor.construct(ctorArgs);
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
                clazz,
                new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {
                        @Override
                        public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                            return fieldMetas.iterator();
                        }

                        @Override
                        public ResultAccumlatorImpl startDecode() {
                            return new ResultAccumlatorImpl(clazz);
                        }

                        @Override
                        public int size() {
                            return fieldMetas.size();
                        }
                }
        );
    }

    @Override
    public <T> FieldCodec<IN, OUT> createFieldCodec(Field field) {
        final Class<T> clazz = (Class<T>)field.getType();
        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<IN, OUT>(field, booleanCodec());
            } else if (clazz.equals(byte.class)) {
                return new FieldCodec.ByteFieldCodec<IN, OUT>(field, byteCodec());
            } else if (clazz.equals(char.class)) {
                return new FieldCodec.CharFieldCodec<IN, OUT>(field, charCodec());
            } else if (clazz.equals(short.class)) {
                return new FieldCodec.ShortFieldCodec<IN, OUT>(field, shortCodec());
            } else if (clazz.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<IN, OUT>(field, intCodec());
            } else if (clazz.equals(long.class)) {
                return new FieldCodec.LongFieldCodec<IN, OUT>(field, longCodec());
            } else if (clazz.equals(float.class)) {
                return new FieldCodec.FloatFieldCodec<IN, OUT>(field, floatCodec());
            } else if (clazz.equals(double.class)) {
                return new FieldCodec.DoubleFieldCodec<IN, OUT>(field, doubleCodec());
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + clazz);
            }
        } else if (clazz.isArray()) {
            final Class<?> elemType = clazz.getComponentType();
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
                final Codec<Object[], IN, OUT> codec = getCodec((Class<Object[]>)clazz);
                return new FieldCodec.ObjectArrayFieldCodec<>(field, codec);
            }
        } else {
            final Codec<T, IN, OUT> codec;

            if (clazz.isEnum() ||
                    clazz.equals(Boolean.class) ||
                    clazz.equals(Byte.class) ||
                    clazz.equals(Character.class) ||
                    clazz.equals(Short.class) ||
                    clazz.equals(Integer.class) ||
                    clazz.equals(Long.class) ||
                    clazz.equals(Float.class) ||
                    clazz.equals(Double.class) ||
                    clazz.equals(String.class)) {
                codec = getCodec(clazz);
            } else if (Map.class.isAssignableFrom(clazz)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                if (typeArgs.size() == 2) {
                    final Class<?> keyType = typeArgs.get(0);
                    final Class<?> valueType = typeArgs.get(1);
                    codec = (Codec<T, IN, OUT>) getMapCodec((Class)clazz, keyType, valueType);
                } else {
                    codec = (Codec<T, IN, OUT>) getMapCodec((Class)clazz, Object.class, Object.class);
                }
            } else if (Collection.class.isAssignableFrom(clazz)) {
                final Codec<Object, IN, OUT> elemCodec;
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    elemCodec = getCodec(elemType);
                } else {
                    elemCodec = getCodec(Object.class);
                }
                codec = (Codec<T, IN, OUT>) getCollCodec((Class<Collection<Object>>) clazz, elemCodec);
            } else {
                codec = getCodec(clazz);
            }

            return new FieldCodec.ObjectFieldCodec<>(field, codec);
        }
    }
}
