package org.typemeta.funcj.codec2.core.fields;

import org.typemeta.funcj.codec2.core.*;
import org.typemeta.funcj.codec2.core.fields.PrimitiveFieldCodecs.BooleanFieldCodec;
import org.typemeta.funcj.codec2.core.fields.PrimitiveFieldCodecs.IntegerFieldCodec;
import org.typemeta.funcj.codec2.core.fields.PrimitiveFieldCodecs.ObjectFieldCodec;
import org.typemeta.funcj.codec2.core.utils.CodecException;
import org.typemeta.funcj.codec2.core.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FieldReflCodecGenerator<IN, OUT> implements CodecGenerator<IN, OUT> {
    private final Map<Class<?>, ObjectCreator<?>> objectCreatorMap;

    public FieldReflCodecGenerator(Map<Class<?>, ObjectCreator<?>> objectCreatorMap) {
        this.objectCreatorMap = objectCreatorMap;
    }

    public FieldReflCodecGenerator() {
        this(new ConcurrentHashMap<>());
    }

    protected <T> ObjectCreator<T> getObjectCreator(Class<T> type) {
        final ObjectCreator<?> objCreator =
                objectCreatorMap.computeIfAbsent(
                        type,
                        ReflectionUtils::createObjectCreator
                );

        return (ObjectCreator<T>)objCreator;
    }

    @Override
    public <T> Codec<T, IN, OUT> generate(CodecCore<IN, OUT> core, Class<T> type) {
        final ObjectCreator<T> ctor = getObjectCreator(type);
        final Map<String, FieldCodec<?, IN, OUT>> fieldCodecs = new LinkedHashMap<>();
        Class<?> clazz = type;
        for (int depth = 0; !clazz.equals(Object.class); depth++) {
            final Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = getFieldName(field, depth, fieldCodecs.keySet());
                    fieldCodecs.put(fieldName, createFieldCodec(core, field));
                }
            }
            clazz = clazz.getSuperclass();
        }
        return core.format().objectCodec(type, fieldCodecs, ctor);
    }

    protected String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        if (existingNames.contains(name)) {
            name = field.getDeclaringClass().getSimpleName() + "." +  field.getName();
            if (existingNames.contains(name)) {
                return field.getDeclaringClass().getName() + "." +  field.getName();
            }
        }
        return name;
    }

    protected <T> FieldCodec<T, IN, OUT> createFieldCodec(CodecCore<IN, OUT> core, Field field) {
        final CodecFormat<IN, OUT> format = core.format();
        final Class<T> clazz = (Class<T>) field.getType();
        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return new BooleanFieldCodec<>(field, format.booleanCodec());
//            } else if (clazz.equals(byte.class)) {
//                return new FieldCodec.ByteFieldCodec<>(field, format.byteCodec());
//            } else if (clazz.equals(char.class)) {
//                return new FieldCodec.CharFieldCodec<>(field, format.charCodec());
//            } else if (clazz.equals(short.class)) {
//                return new FieldCodec.ShortFieldCodec<>(field, format.shortCodec());
            } else if (clazz.equals(int.class)) {
                return new IntegerFieldCodec<>(field, format.integerCodec());
//            } else if (clazz.equals(long.class)) {
//                return new FieldCodec.LongFieldCodec<>(field, format.longCodec());
//            } else if (clazz.equals(float.class)) {
//                return new FieldCodec.FloatFieldCodec<>(field, format.floatCodec());
//            } else if (clazz.equals(double.class)) {
//                return new FieldCodec.DoubleFieldCodec<>(field, format.doubleCodec());
            } else {
                throw new CodecException("Unexpected primitive type - " + clazz);
            }
        } else if (clazz.isArray()) {
            final Class<?> elemType = clazz.getComponentType();
            if (elemType.equals(boolean.class)) {
                return new ObjectFieldCodec<>(field, format.booleanArrayCodec());
//            } else if (elemType.equals(byte.class)) {
//                return new FieldCodec.ByteArrayFieldCodec<>(field, format.byteArrayCodec());
//            } else if (elemType.equals(char.class)) {
//                return new FieldCodec.CharArrayFieldCodec<>(field, format.charArrayCodec());
//            } else if (elemType.equals(short.class)) {
//                return new FieldCodec.ShortArrayFieldCodec<>(field, format.shortArrayCodec());
            } else if (elemType.equals(int.class)) {
                return new ObjectFieldCodec<>(field, format.integerArrayCodec());
//            } else if (elemType.equals(long.class)) {
//                return new FieldCodec.LongArrayFieldCodec<>(field, format.longArrayCodec());
//            } else if (elemType.equals(float.class)) {
//                return new FieldCodec.FloatArrayFieldCodec<>(field, format.floatArrayCodec());
//            } else if (elemType.equals(double.class)) {
//                return new FieldCodec.DoubleArrayFieldCodec<>(field, format.doubleArrayCodec());
            } else {
                final Codec<Object[], IN, OUT> codec = core.getCodec((Class<Object[]>) clazz);
                return new ObjectFieldCodec<>(field, codec);
            }
        } else {
            final Codec<?, IN, OUT> codec;

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
                codec = core.getCodec(clazz);
//            } else if (Map.class.isAssignableFrom(clazz)) {
//                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
//                if (typeArgs.size() == 2) {
//                    final Class<?> keyType = typeArgs.get(0);
//                    final Class<?> valueType = typeArgs.get(1);
//                    codec = (Codec<?, IN, OUT>) getMapCodec((Class) clazz, keyType, valueType);
//                } else {
//                    codec = (Codec<?, IN, OUT>) getMapCodec((Class) clazz, Object.class, Object.class);
//                }
//            } else if (Collection.class.isAssignableFrom(clazz)) {
//                final Codec<Object, IN, OUT> elemCodec;
//                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Collection.class);
//                if (typeArgs.size() == 1) {
//                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
//                    elemCodec = getCodec(elemType);
//                } else {
//                    elemCodec = getCodec(Object.class);
//                }
//                codec = getCollCodec((Class<Collection<Object>>) clazz, elemCodec);
            } else {
                codec = core.getCodec(clazz);
            }

            return new ObjectFieldCodec<>(field, codec);
        }
    }
}
