package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.CodecFormat.*;
import org.typemeta.funcj.codec.bytes.ArgMapTypeCtor;
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
 * @param <CFG>     the config type
 */
@SuppressWarnings("unchecked")
public class CodecCoreImpl<
        IN extends Input<IN>,
        OUT extends Output<OUT>,
        CFG extends CodecConfig
        > implements CodecCoreEx<IN, OUT, CFG> {

    /**
     * A map that associates a class with a {@code Codec}.
     * Although {@code Codec}s can be registered by the caller prior to en/decoding,
     * the primary populator of the registry is this {@code CodecCore} implementation.
     * As and when new classes are encountered, they are inspected via Reflection,
     * and a {@code Codec} is constructed and registered.
     */
    protected final ConcurrentMap<ClassKey<?>, Codec<?, IN, OUT, CFG>> codecRegistry = new ConcurrentHashMap<>();

    /**
     * A map that associates a class with a {@code NoArgsCtor}.
     * Although {@code TypeConstructor}s can be registered by the caller prior to en/decoding,
     * the primary populator of the registry is this {@code CodecCore} implementation.
     * As and when new classes are encountered, they are inspected via Reflection,
     * and a {@code TypeConstructor} is constructed and registered.
     */
    protected final ConcurrentMap<ClassKey<?>, NoArgsTypeCtor<?>> noArgsCtorRegistry = new ConcurrentHashMap<>();

    protected final ConcurrentMap<ClassKey<?>, ArgArrayTypeCtor<?>> argArrayCtorRegistry = new ConcurrentHashMap<>();

    protected final ConcurrentMap<ClassKey<?>, ArgMapTypeCtor<?>> argMapCtorRegistry = new ConcurrentHashMap<>();

    protected final CodecFormat<IN, OUT, CFG> format;

    public CodecCoreImpl(CodecFormat<IN, OUT, CFG> format) {
        this.format = format;
    }

    @Override
    public CodecFormat<IN, OUT, CFG> format() {
        return format;
    }

    @Override
    public CFG config() {
        return format.config();
    }

    @Override
    public <T> void registerCodec(Class<? extends T> clazz, Codec<T, IN, OUT, CFG> codec) {
        config().registerAllowedClass(clazz);
        synchronized (codecRegistry) {
            codecRegistry.put(ClassKey.valueOf(clazz), codec);
        }
    }

    @Override
    public <T> ObjectCodecBuilderWithArgArray<T, IN, OUT, CFG> registerCodecWithArgArray(Class<T> clazz) {
        return new ObjectCodecBuilderWithArgArray<T, IN, OUT, CFG>(this, clazz) {
            @Override
            protected Codec<T, IN, OUT, CFG> registration(Codec<T, IN, OUT, CFG> codec) {
                registerCodec(clazz, codec);
                return codec;
            }
        };
    }

    @Override
    public <T> ObjectCodecBuilderWithArgMap<T, IN, OUT, CFG> registerCodecWithArgMap(Class<T> clazz) {
        return new ObjectCodecBuilderWithArgMap<T, IN, OUT, CFG>(this, clazz) {
            @Override
            protected Codec<T, IN, OUT, CFG> registration(Codec<T, IN, OUT, CFG> codec) {
                registerCodec(clazz, codec);
                return codec;
            }
        };
    }

    @Override
    public <T> void registerStringProxyCodec(
            Class<T> clazz,
            Functions.F<T, String> encode,
            Functions.F<String, T> decode) {
        config().registerAllowedClass(clazz);
        registerCodec(clazz, new Codecs.StringProxyCodec<>(clazz, encode, decode));
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
    public <T> void registerArgMapTypeCtor(Class<? extends T> clazz, ArgMapTypeCtor<T> typeCtor) {
        config().registerAllowedClass(clazz);
        argMapCtorRegistry.put(ClassKey.valueOf(clazz), typeCtor);
    }

    @Override
    public <T> void encode(Class<? super T> clazz, T val, OUT out) {
        getCodec(clazz).encodeWithCheck(this, val, out);
    }

    @Override
    public <T> T decode(Class<? super T> clazz, IN in) {
        return (T)getCodec(clazz).decodeWithCheck(this, in);
    }

    @Override
    public <T> NoArgsTypeCtor<T> getNoArgsCtor(Class<T> clazz) {
        final ClassKey<?> key = ClassKey.valueOf(clazz);

        NoArgsTypeCtor<T> ctor = (NoArgsTypeCtor<T>)noArgsCtorRegistry.get(key);

        if (ctor != null) {
            return ctor;
        } else {
            // TODO: review this.
            final NoArgsTypeCtor<T> newCtor = NoArgsTypeCtor.create(clazz);
            if (newCtor != null) {
                ctor = (NoArgsTypeCtor<T>) noArgsCtorRegistry.putIfAbsent(key, newCtor);
                if (ctor != null) {
                    return ctor;
                } else {
                    return newCtor;
                }
            } else {
                throw new CodecException("No suitable type constructor was found for " + clazz);
            }
        }
    }

    @Override
    public <T> ArgArrayTypeCtor<T> getArgArrayCtor(Class<T> clazz) {
        return (ArgArrayTypeCtor<T>)argArrayCtorRegistry.get(ClassKey.valueOf(clazz));
    }

    @Override
    public <T> ArgMapTypeCtor<T> getArgMapTypeCtor(Class<T> clazz) {
        return (ArgMapTypeCtor<T>)argMapCtorRegistry.get(ClassKey.valueOf(clazz));
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> getCodec(Class<T> clazz) {
        return getCodec(
                ClassKey.valueOf(config().mapToProxy(clazz)),
                () -> createCodec(clazz)
        );
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> getCodec(
            ClassKey<?> key,
            Supplier<Codec<T, IN, OUT, CFG>> codecSupp) {
        // First attempt, without locking.
        if (codecRegistry.containsKey(key)) {
            return (Codec<T, IN, OUT, CFG>)codecRegistry.get(key);
        } else {
            final CodecRef<T, IN, OUT, CFG> codecRef;
            // Lock and try again.
            synchronized(codecRegistry) {
                if (codecRegistry.containsKey(key)) {
                    return (Codec<T, IN, OUT, CFG>) codecRegistry.get(key);
                } else {
                    // Ok, it's definitely not there, so add a CodecRef.
                    codecRef = new CodecRef<>();
                    codecRegistry.put(key, codecRef);
                }
            }

            // Initialise the CodecRef, and overwrite the registry entry with the real Codec.
            codecRegistry.put(key, codecRef.setIfUninitialised(codecSupp::get));

            return (Codec<T, IN, OUT, CFG>)codecRegistry.get(key);
        }
    }

    @Override
    public <T> Codec<Collection<T>, IN, OUT, CFG> getCollCodec(
            Class<Collection<T>> collType,
            Codec<T, IN, OUT, CFG> elemCodec) {
        final ClassKey<?> key = ClassKey.valueOf(collType, elemCodec.type());
        return getCodec(key, () -> format().createCollCodec(collType, elemCodec));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT, CFG> getMapCodec(
            Class<Map<K, V>> mapType,
            Class<K> keyType,
            Class<V> valType) {
        final ClassKey<?> key = ClassKey.valueOf(mapType, keyType, valType);
        return getCodec(key, () -> createMapCodec(mapType, keyType, valType));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT, CFG> getMapCodec(
            Class<Map<K, V>> mapType,
            Codec<K, IN, OUT, CFG> keyCodec,
            Codec<V, IN, OUT, CFG> valueCodec) {
        final ClassKey<?> key = ClassKey.valueOf(mapType, keyCodec.type(), valueCodec.type());
        return getCodec(key, () -> format().createMapCodec(mapType, keyCodec, valueCodec));
    }

    @Override
    public <V> Codec<Map<String, V>, IN, OUT, CFG> getMapCodec(
            Class<Map<String, V>> mapType,
            Codec<V, IN, OUT, CFG> valueCodec) {
        final ClassKey<?> key = ClassKey.valueOf(mapType, String.class, valueCodec.type());
        return getCodec(key, () -> format().createMapCodec(mapType, valueCodec));
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT, CFG> createMapCodec(
            Class<Map<K, V>> mapType,
            Class<K> keyType,
            Class<V> valType)  {
        final Codec<V, IN, OUT, CFG> valueCodec = getCodec(valType);
        if (String.class.equals(keyType)) {
            return (Codec)format().createMapCodec((Class<Map<String, V>>)(Class)mapType, valueCodec);
        } else {
            final Codec<K, IN, OUT, CFG> keyCodec = getCodec(keyType);
            return format().createMapCodec(mapType, keyCodec, valueCodec);
        }
    }

    protected static class InterfaceCodec<
            T,
            IN extends Input<IN>,
            OUT extends Output<OUT>,
            CFG extends CodecConfig> implements Codec<T, IN, OUT, CFG> {

        protected final Class<T> clazz;

        protected InterfaceCodec(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Class<T> type() {
            return clazz;
        }

        @Override
        public OUT encode(CodecCoreEx<IN, OUT, CFG> core, T value, OUT out) {
            throw new CodecException("Internal error - can't encode an interface");
        }

        @Override
        public T decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            throw new CodecException("Internal error - can't decode an interface");
        }
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> createCodec(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return (Codec<T, IN, OUT, CFG>)format.booleanCodec();
            } else if (clazz.equals(byte.class)) {
                return (Codec<T, IN, OUT, CFG>) format.byteCodec();
            } else if (clazz.equals(char.class)) {
                return (Codec<T, IN, OUT, CFG>) format.charCodec();
            } else if (clazz.equals(short.class)) {
                return (Codec<T, IN, OUT, CFG>) format.shortCodec();
            } else if (clazz.equals(int.class)) {
                return (Codec<T, IN, OUT, CFG>) format.intCodec();
            } else if (clazz.equals(long.class)) {
                return (Codec<T, IN, OUT, CFG>) format.longCodec();
            } else if (clazz.equals(float.class)) {
                return (Codec<T, IN, OUT, CFG>) format.floatCodec();
            } else if (clazz.equals(double.class)) {
                return (Codec<T, IN, OUT, CFG>) format.doubleCodec();
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + clazz);
            }
        } else {
            if (clazz.isArray()) {
                final Class<?> elemType = clazz.getComponentType();
                if (elemType.equals(boolean.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.booleanArrayCodec();
                } else if (elemType.equals(byte.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.byteArrayCodec();
                } else if (elemType.equals(char.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.charArrayCodec();
                } else if (elemType.equals(short.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.shortArrayCodec();
                } else if (elemType.equals(int.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.intArrayCodec();
                } else if (elemType.equals(long.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.longArrayCodec();
                } else if (elemType.equals(float.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.floatArrayCodec();
                } else if (elemType.equals(double.class)) {
                    return (Codec<T, IN, OUT, CFG>) format.doubleArrayCodec();
                } else {
                    if (elemType.equals(Boolean.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Boolean.class, format.booleanCodec());
                    } else if (elemType.equals(Byte.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Byte.class, format.byteCodec());
                    } else if (elemType.equals(Character.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Character.class, format.charCodec());
                    } else if (elemType.equals(Short.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Short.class, format.shortCodec());
                    } else if (elemType.equals(Integer.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Integer.class, format.intCodec());
                    } else if (elemType.equals(Long.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Long.class, format.longCodec());
                    } else if (elemType.equals(Float.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Float.class, format.floatCodec());
                    } else if (elemType.equals(Double.class)) {
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, Double.class, format.doubleCodec());
                    } else {
                        final Codec<Object, IN, OUT, CFG> elemCodec = getCodec((Class<Object>)elemType);
                        return (Codec<T, IN, OUT, CFG>) format().createObjectArrayCodec((Class)clazz, (Class<Object>) elemType, elemCodec);
                    }
                }
            } else if (clazz.isEnum()) {
                return format.enumCodec((Class) clazz);
            } else if (ReflectionUtils.isEnumSubType(clazz)) {
                return format.enumCodec((Class) clazz.getSuperclass());
            } else if (clazz.equals(Boolean.class)) {
                return (Codec<T, IN, OUT, CFG>) format.booleanCodec();
            } else if (clazz.equals(Byte.class)) {
                return (Codec<T, IN, OUT, CFG>) format.byteCodec();
            } else if (clazz.equals(Character.class)) {
                return (Codec<T, IN, OUT, CFG>) format.charCodec();
            } else if (clazz.equals(Short.class)) {
                return (Codec<T, IN, OUT, CFG>) format.shortCodec();
            } else if (clazz.equals(Integer.class)) {
                return (Codec<T, IN, OUT, CFG>) format.intCodec();
            } else if (clazz.equals(Long.class)) {
                return (Codec<T, IN, OUT, CFG>) format.longCodec();
            } else if (clazz.equals(Float.class)) {
                return (Codec<T, IN, OUT, CFG>) format.floatCodec();
            } else if (clazz.equals(Double.class)) {
                return (Codec<T, IN, OUT, CFG>) format.doubleCodec();
            } else if (clazz.equals(String.class)) {
                return (Codec<T, IN, OUT, CFG>) format.stringCodec();
            } else if (Map.class.isAssignableFrom(clazz)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Map.class);
                if (typeArgs.size() == 2) {
                    final Class<?> keyType = typeArgs.get(0);
                    final Class<?> valueType = typeArgs.get(1);
                    return (Codec<T, IN, OUT, CFG>) getMapCodec((Class)clazz, keyType, valueType);
                } else {
                    return (Codec<T, IN, OUT, CFG>) getMapCodec((Class)clazz, Object.class, Object.class);
                }
            } else if (Collection.class.isAssignableFrom(clazz)) {
                final Codec<Object, IN, OUT, CFG> elemCodec;
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    elemCodec = getCodec(elemType);
                } else {
                    elemCodec = getCodec(Object.class);
                }
                return (Codec<T, IN, OUT, CFG>) getCollCodec((Class<Collection<Object>>) clazz, elemCodec);
            } else if (clazz.isInterface()) {
                return new InterfaceCodec<>(clazz);
            } else {
                return createObjectCodec(clazz);
            }
        }
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> createObjectCodec(Class<T> clazz) {
        final ArgArrayTypeCtor<T> ctor = getArgArrayCtor(clazz);
        if (ctor != null) {
            return createObjectCodecWithArgArray(clazz, ctor);
        } else {
            final ArgMapTypeCtor<T> ctor2 = getArgMapTypeCtor(clazz);
            if (ctor2 != null) {
                return createObjectCodecWithArgMap(clazz, ctor2);
            } else {
                final NoArgsTypeCtor<T> ctor3 = getNoArgsCtor(clazz);
                return createObjectCodec(clazz, ctor3);
            }
        }
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> createObjectCodec(Class<T> clazz, NoArgsTypeCtor<T> ctor) {
        final Map<String, FieldCodec<IN, OUT, CFG>> fieldCodecs = new LinkedHashMap<>();
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
    public <T> Codec<T, IN, OUT, CFG> createObjectCodec(
            Class<T> clazz,
            Map<String, FieldCodec<IN, OUT, CFG>> fieldCodecs,
            NoArgsTypeCtor<T> ctor) {
        final class ResultAccumlatorImpl implements ObjectMeta.ResultAccumlator<T> {
            final T val;

            ResultAccumlatorImpl() {
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
                            final FieldCodec<IN, OUT, CFG> codec = en.getValue();
                            return new ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>() {
                                @Override
                                public String name() {
                                    return name;
                                }

                                @Override
                                public OUT encodeField(T val, OUT out) {
                                    return codec.encodeField(CodecCoreImpl.this, val, out);
                                }

                                @Override
                                public ResultAccumlatorImpl decodeField(ResultAccumlatorImpl acc, IN in) {
                                    codec.decodeField(CodecCoreImpl.this, acc.val, in);
                                    return acc;
                                }
                            };
                        }).collect(toList());

        return format().createObjectCodec(
                clazz,
                new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {
                        @Override
                        public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                            return fieldMetas.iterator();
                        }

                        @Override
                        public ResultAccumlatorImpl startDecode() {
                            return new ResultAccumlatorImpl();
                        }
                }
        );
    }

    protected <T> Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG>> buildFieldCodecs(Class<T> clazz) {
        final Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG>> fieldCodecs = new LinkedHashMap<>();
        Class<?> clazz2 = clazz;
        for (int depth = 0; !clazz2.equals(Object.class); depth++) {
            final Field[] fields = clazz2.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = config().getFieldName(field, depth, fieldCodecs.keySet());
                    final Codec<?, IN, OUT, CFG> codec = createCodec(field.getType());
                    final ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG> fcodec = createFieldCodec(field, codec);
                    fieldCodecs.put(fieldName, fcodec);
                }
            }
            clazz2 = clazz2.getSuperclass();
        }

        return fieldCodecs;
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> createObjectCodecWithArgMap(Class<T> clazz, ArgMapTypeCtor<T> ctor) {
        final Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG>> fieldCodecs = buildFieldCodecs(clazz);
        return createObjectCodecWithArgMap(clazz, fieldCodecs, ctor);
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> createObjectCodecWithArgMap(
            Class<T> clazz,
            Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG>> fieldCodecs,
            ArgMapTypeCtor<T> ctor) {
        final class ResultAccumlatorImpl implements ObjectMeta.ResultAccumlator<T> {
            final Map<String, Object> ctorArgs;

            ResultAccumlatorImpl() {
                this.ctorArgs = new HashMap<>();
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
                            final ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG> codec = en.getValue();
                            return new ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>() {
                                @Override
                                public String name() {
                                    return name;
                                }

                                @Override
                                public OUT encodeField(T val, OUT out) {
                                    return codec.encodeField(CodecCoreImpl.this, val, out);
                                }

                                @Override
                                public ResultAccumlatorImpl decodeField(ResultAccumlatorImpl acc, IN in) {
                                    acc.ctorArgs.put(name, codec.decodeField(CodecCoreImpl.this, in));
                                    return acc;
                                }
                            };
                        }).collect(toList());

        return format().createObjectCodec(
                clazz,
                new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {
                    @Override
                    public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                        return fieldMetas.iterator();
                    }

                    @Override
                    public ResultAccumlatorImpl startDecode() {
                        return new ResultAccumlatorImpl();
                    }
                }
        );
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> createObjectCodecWithArgArray(
            Class<T> clazz,
            ArgArrayTypeCtor<T> ctor) {
        final Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG>> fieldCodecs = buildFieldCodecs(clazz);
        return createObjectCodecWithArgArray(clazz, fieldCodecs, ctor);
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> createObjectCodecWithArgArray(
            Class<T> clazz,
            Map<String, ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG>> fieldCodecs,
            ArgArrayTypeCtor<T> ctor) {
        final class ResultAccumlatorImpl implements ObjectMeta.ResultAccumlator<T> {
            final Object[] ctorArgs;
            int i = 0;

            ResultAccumlatorImpl() {
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
                            final ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG> codec = en.getValue();
                            return new ObjectMeta.Field<T, IN, OUT, ResultAccumlatorImpl>() {
                                @Override
                                public String name() {
                                    return name;
                                }

                                @Override
                                public OUT encodeField(T val, OUT out) {
                                    return codec.encodeField(CodecCoreImpl.this, val, out);
                                }

                                @Override
                                public ResultAccumlatorImpl decodeField(ResultAccumlatorImpl acc, IN in) {
                                    acc.ctorArgs[acc.i++] = codec.decodeField(CodecCoreImpl.this, in);
                                    return acc;
                                }
                            };
                        }).collect(toList());

        return format().createObjectCodec(
                clazz,
                new ObjectMeta<T, IN, OUT, ResultAccumlatorImpl>() {
                    @Override
                    public Iterator<Field<T, IN, OUT, ResultAccumlatorImpl>> iterator() {
                        return fieldMetas.iterator();
                    }

                    @Override
                    public ResultAccumlatorImpl startDecode() {
                        return new ResultAccumlatorImpl();
                    }
                }
        );
    }

    protected <T, FT> ObjectCodecBuilder.FieldCodec<T, IN, OUT, CFG> createFieldCodec(
            Field field,
            Codec<FT, IN, OUT, CFG> codec) {
        return new ObjectCodecBuilder.FieldCodec<>(
                t -> CodecException.wrap(() -> {
                    field.setAccessible(true);
                    final FT fv = (FT)field.get(t);
                    field.setAccessible(false);
                    return fv;
                }),
                codec
        );
    }

    @Override
    public <T> FieldCodec<IN, OUT, CFG> createFieldCodec(Field field) {
        final Class<T> clazz = (Class<T>)field.getType();
        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return new FieldCodec.BooleanFieldCodec<>(field, format.booleanCodec());
            } else if (clazz.equals(byte.class)) {
                return new FieldCodec.ByteFieldCodec<>(field, format.byteCodec());
            } else if (clazz.equals(char.class)) {
                return new FieldCodec.CharFieldCodec<>(field, format.charCodec());
            } else if (clazz.equals(short.class)) {
                return new FieldCodec.ShortFieldCodec<>(field, format.shortCodec());
            } else if (clazz.equals(int.class)) {
                return new FieldCodec.IntegerFieldCodec<>(field, format.intCodec());
            } else if (clazz.equals(long.class)) {
                return new FieldCodec.LongFieldCodec<>(field, format.longCodec());
            } else if (clazz.equals(float.class)) {
                return new FieldCodec.FloatFieldCodec<>(field, format.floatCodec());
            } else if (clazz.equals(double.class)) {
                return new FieldCodec.DoubleFieldCodec<>(field, format.doubleCodec());
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + clazz);
            }
        } else if (clazz.isArray()) {
            final Class<?> elemType = clazz.getComponentType();
            if (elemType.equals(boolean.class)) {
                return new FieldCodec.BooleanArrayFieldCodec<>(field, format.booleanArrayCodec());
            } else if (elemType.equals(byte.class)) {
                return new FieldCodec.ByteArrayFieldCodec<>(field, format.byteArrayCodec());
            } else if (elemType.equals(char.class)) {
                return new FieldCodec.CharArrayFieldCodec<>(field, format.charArrayCodec());
            } else if (elemType.equals(short.class)) {
                return new FieldCodec.ShortArrayFieldCodec<>(field, format.shortArrayCodec());
            } else if (elemType.equals(int.class)) {
                return new FieldCodec.IntegerArrayFieldCodec<>(field, format.intArrayCodec());
            } else if (elemType.equals(long.class)) {
                return new FieldCodec.LongArrayFieldCodec<>(field, format.longArrayCodec());
            } else if (elemType.equals(float.class)) {
                return new FieldCodec.FloatArrayFieldCodec<>(field, format.floatArrayCodec());
            } else if (elemType.equals(double.class)) {
                return new FieldCodec.DoubleArrayFieldCodec<>(field, format.doubleArrayCodec());
            } else {
                final Codec<Object[], IN, OUT, CFG> codec = getCodec((Class<Object[]>)clazz);
                return new FieldCodec.ObjectArrayFieldCodec<>(field, codec);
            }
        } else {
            final Codec<T, IN, OUT, CFG> codec;

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
                    codec = (Codec<T, IN, OUT, CFG>) getMapCodec((Class)clazz, keyType, valueType);
                } else {
                    codec = (Codec<T, IN, OUT, CFG>) getMapCodec((Class)clazz, Object.class, Object.class);
                }
            } else if (Collection.class.isAssignableFrom(clazz)) {
                final Codec<Object, IN, OUT, CFG> elemCodec;
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    elemCodec = getCodec(elemType);
                } else {
                    elemCodec = getCodec(Object.class);
                }
                codec = (Codec<T, IN, OUT, CFG>) getCollCodec((Class<Collection<Object>>) clazz, elemCodec);
            } else {
                codec = getCodec(clazz);
            }

            return new FieldCodec.ObjectFieldCodec<>(field, codec);
        }
    }
}
