package org.typemeta.funcj.codec2.core;

import org.typemeta.funcj.codec2.core.fields.FieldReflCodecGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CodecCoreImpl<IN, OUT> implements CodecCore<IN, OUT> {

    protected final CodecFormat<IN, OUT> format;
    protected final CodecConfig config;
    protected final CodecGenerator<IN, OUT> codecGenerator;

    protected final ConcurrentMap<Class<?>, Codec<?, IN, OUT>> codecMap;

    protected CodecCoreImpl(
            CodecFormat<IN, OUT> format,
            CodecConfig config,
            CodecGenerator<IN, OUT> codecGenerator,
            ConcurrentMap<Class<?>, Codec<?, IN, OUT>> codecMap
    ) {
        this.format = format;
        this.config = config;
        this.codecGenerator = codecGenerator;
        this.codecMap = codecMap;
    }

    protected CodecCoreImpl(
            CodecFormat<IN, OUT> format,
            CodecConfig config,
            CodecGenerator<IN, OUT> codecGenerator
    ) {
        this(format, config, codecGenerator, new ConcurrentHashMap<>());
    }

    protected CodecCoreImpl(
            CodecFormat<IN, OUT> format,
            CodecConfig config
    ) {
        this(format, config, new FieldReflCodecGenerator<>(), new ConcurrentHashMap<>());
    }

    @Override
    public CodecFormat<IN, OUT> format() {
        return format;
    }

    @Override
    public CodecConfig config() {
        return config;
    }

    @Override
    public <T> Decoder<T, IN> getDecoder(Class<T> type) {
        return getCodec(type);
    }

    @Override
    public <T> Encoder<T, OUT> getEncoder(Class<T> type) {
        return getCodec(type);
    }

    @Override
    public <T> Codec<T, IN, OUT> getCodec(Class<T> type) {
        // First attempt, without locking.
        if (codecMap.containsKey(type)) {
            return (Codec<T, IN, OUT>)codecMap.get(type);
        } else {
            final CodecRef<T, IN, OUT> codecRef;
            // Lock the map and try again.
            synchronized(codecMap) {
                if (codecMap.containsKey(type)) {
                    return (Codec<T, IN, OUT>)codecMap.get(type);
                } else {
                    // Ok, it's definitely not there, so add a CodecRef
                    // (in case the class has a recursive self-reference).
                    codecRef = new CodecRef<>();
                    codecMap.put(type, codecRef);
                }
            }

            // Initialise the CodecRef, and overwrite the registry entry with the real Codec.
            codecMap.put(type, codecRef.setIfUninitialised(() -> createCodec(type)));

            return (Codec<T, IN, OUT>)codecMap.get(type);
        }
    }

    public <T> Codec<T, IN, OUT> createCodec(Class<T> type) {
        if (type.isPrimitive()) {
            if (type.equals(boolean.class)) {
                return (Codec<T, IN, OUT>)format.booleanCodec();
//            } else if (type.equals(byte.class)) {
//                return (Codec<T, IN, OUT>) format.byteCodec();
//            } else if (type.equals(char.class)) {
//                return (Codec<T, IN, OUT>) format.charCodec();
//            } else if (type.equals(short.class)) {
//                return (Codec<T, IN, OUT>) format.shortCodec();
            } else if (type.equals(int.class)) {
                return (Codec<T, IN, OUT>) format.integerCodec();
//            } else if (type.equals(long.class)) {
//                return (Codec<T, IN, OUT>) format.longCodec();
//            } else if (type.equals(float.class)) {
//                return (Codec<T, IN, OUT>) format.floatCodec();
//            } else if (type.equals(double.class)) {
//                return (Codec<T, IN, OUT>) format.doubleCodec();
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + type);
            }
        } else {
            if (type.isArray()) {
                final Class<?> elemType = type.getComponentType();
                if (elemType.isPrimitive()) {
                    if (elemType.equals(boolean.class)) {
                        return (Codec<T, IN, OUT>) format.booleanArrayCodec();
    //                } else if (elemType.equals(byte.class)) {
    //                    return (Codec<T, IN, OUT>) format.byteArrayCodec();
    //                } else if (elemType.equals(char.class)) {
    //                    return (Codec<T, IN, OUT>) format.charArrayCodec();
    //                } else if (elemType.equals(short.class)) {
    //                    return (Codec<T, IN, OUT>) format.shortArrayCodec();
                    } else if (elemType.equals(int.class)) {
                        return (Codec<T, IN, OUT>) format.integerArrayCodec();
    //                } else if (elemType.equals(long.class)) {
    //                    return (Codec<T, IN, OUT>) format.longArrayCodec();
    //                } else if (elemType.equals(float.class)) {
    //                    return (Codec<T, IN, OUT>) format.floatArrayCodec();
    //                } else if (elemType.equals(double.class)) {
    //                    return (Codec<T, IN, OUT>) format.doubleArrayCodec();
                    } else {
                        throw new IllegalStateException("Unexpected array element primitive type - " + elemType);
                    }
                } else {
                    if (elemType.equals(Boolean.class)) {
                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Boolean.class, format.booleanCodec());
//                    } else if (elemType.equals(Byte.class)) {
//                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Byte.class, format.byteCodec());
//                    } else if (elemType.equals(Character.class)) {
//                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Character.class, format.charCodec());
//                    } else if (elemType.equals(Short.class)) {
//                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Short.class, format.shortCodec());
                    } else if (elemType.equals(Integer.class)) {
                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Integer.class, format.integerCodec());
//                    } else if (elemType.equals(Long.class)) {
//                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Long.class, format.longCodec());
//                    } else if (elemType.equals(Float.class)) {
//                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Float.class, format.floatCodec());
//                    } else if (elemType.equals(Double.class)) {
//                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, Double.class, format.doubleCodec());
                    } else {
                        final Codec<Object, IN, OUT> elemCodec = getCodec((Class<Object>)elemType);
                        return (Codec<T, IN, OUT>) format().objectArrayCodec((Class)type, (Class<Object>) elemType, elemCodec);
                    }
                }
//            } else if (type.isEnum()) {
//                return format.enumCodec((Class) type);
//            } else if (ReflectionUtils.isEnumSubType(type)) {
//                return format.enumCodec((Class) type.getSuperclass());
            } else if (type.equals(Boolean.class)) {
                return (Codec<T, IN, OUT>) format.booleanCodec();
//            } else if (type.equals(Byte.class)) {
//                return (Codec<T, IN, OUT>) format.byteCodec();
//            } else if (type.equals(Character.class)) {
//                return (Codec<T, IN, OUT>) format.charCodec();
//            } else if (type.equals(Short.class)) {
//                return (Codec<T, IN, OUT>) format.shortCodec();
            } else if (type.equals(Integer.class)) {
                return (Codec<T, IN, OUT>) format.integerCodec();
//            } else if (type.equals(Long.class)) {
//                return (Codec<T, IN, OUT>) format.longCodec();
//            } else if (type.equals(Float.class)) {
//                return (Codec<T, IN, OUT>) format.floatCodec();
//            } else if (type.equals(Double.class)) {
//                return (Codec<T, IN, OUT>) format.doubleCodec();
            } else if (type.equals(String.class)) {
                return (Codec<T, IN, OUT>) format.stringCodec();
//            } else if (Map.class.isAssignableFrom(type)) {
//                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(type, Map.class);
//                if (typeArgs.size() == 2) {
//                    final Class<?> keyType = typeArgs.get(0);
//                    final Class<?> valueType = typeArgs.get(1);
//                    return (Codec<T, IN, OUT>) getMapCodec((Class)type, keyType, valueType);
//                } else {
//                    return (Codec<T, IN, OUT>) getMapCodec((Class)type, Object.class, Object.class);
//                }
//            } else if (Collection.class.isAssignableFrom(type)) {
//                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(type, Collection.class);
//                if (typeArgs.size() == 1) {
//                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
//                    return (Codec<T, IN, OUT>)getCollCodec((Class<Collection<Object>>) type, elemType);
//                } else {
//                    return (Codec<T, IN, OUT>)getCollCodec((Class<Collection<Object>>) type, Object.class);
//                }
            } else if (type.isInterface()) {
                return new InterfaceCodec<>(type);
            } else {
                return createObjectCodec(type);
            }
        }
    }

    protected <T> Codec<T, IN, OUT> createObjectCodec(Class<T> type) {
        return codecGenerator.generate(this, type);
    }
}
