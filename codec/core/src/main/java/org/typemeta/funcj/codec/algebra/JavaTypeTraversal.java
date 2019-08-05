package org.typemeta.funcj.codec.algebra;

import org.typemeta.funcj.codec.utils.ReflectionUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class JavaTypeTraversal<T> {
    private final Map<String, T> cache = new HashMap<>();

    private T getCache(Class<?> clazz, Function<Class<?>, T> generator) {
        return cache.computeIfAbsent(clazz.getName(), u -> generator.apply(clazz));
    }

    private void putCache(Class<?> clazz, T value) {
        cache.put(clazz.getName(), value);
    }

    public T apply(JavaTypeAlg<T> alg, Class<?> clazz) {
        return getCache(clazz, c -> applyAlg(alg, c));
    }

    private T applyAlg(JavaTypeAlg<T> alg, Class<?> clazz) {

        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return alg.booleanP();
            } else if (clazz.equals(byte.class)) {
                return alg.byteP();
            } else if (clazz.equals(char.class)) {
                return alg.charP();
            } else if (clazz.equals(short.class)) {
                return alg.shortP();
            } else if (clazz.equals(int.class)) {
                return alg.integerP();
            } else if (clazz.equals(long.class)) {
                return alg.longP();
            } else if (clazz.equals(float.class)) {
                return alg.floatP();
            } else if (clazz.equals(double.class)) {
                return alg.doubleP();
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + clazz);
            }
        } else {
            if (clazz.isArray()) {
                final Class<?> elemType = clazz.getComponentType();
                if (elemType.equals(boolean.class)) {
                    return alg.booleanArr();
                } else if (elemType.equals(byte.class)) {
                    return alg.byteArr();
                } else if (elemType.equals(char.class)) {
                    return alg.charArr();
                } else if (elemType.equals(short.class)) {
                    return alg.shortArr();
                } else if (elemType.equals(int.class)) {
                    return alg.integerArr();
                } else if (elemType.equals(long.class)) {
                    return alg.longArr();
                } else if (elemType.equals(float.class)) {
                    return alg.floatArr();
                } else if (elemType.equals(double.class)) {
                    return alg.doubleArr();
                } else {
                    if (elemType.equals(Boolean.class)) {
                        return alg.booleanArr();
                    } else if (elemType.equals(Byte.class)) {
                        return alg.byteArr();
                    } else if (elemType.equals(Character.class)) {
                        return alg.charArr();
                    } else if (elemType.equals(Short.class)) {
                        return alg.shortArr();
                    } else if (elemType.equals(Integer.class)) {
                        return alg.integerArr();
                    } else if (elemType.equals(Long.class)) {
                        return alg.longArr();
                    } else if (elemType.equals(Float.class)) {
                        return alg.floatArr();
                    } else if (elemType.equals(Double.class)) {
                        return alg.doubleArr();
                    } else {
                        return alg.objectArr(alg.object());
                    }
                }
            } else if (clazz.isEnum()) {
                return alg.enumT(clazz);
            } else if (ReflectionUtils.isEnumSubType(clazz)) {
                return alg.enumT(clazz.getSuperclass());
            } else if (clazz.equals(Boolean.class)) {
                return alg.booleanB();
            } else if (clazz.equals(Byte.class)) {
                return alg.booleanB();
            } else if (clazz.equals(Character.class)) {
                return alg.booleanB();
            } else if (clazz.equals(Short.class)) {
                return alg.booleanB();
            } else if (clazz.equals(Integer.class)) {
                return alg.booleanB();
            } else if (clazz.equals(Long.class)) {
                return alg.booleanB();
            } else if (clazz.equals(Float.class)) {
                return alg.booleanB();
            } else if (clazz.equals(Double.class)) {
                return alg.booleanB();
            } else if (clazz.equals(String.class)) {
                return alg.booleanB();
            } else if (Map.class.isAssignableFrom(clazz)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Map.class);
                if (typeArgs.size() == 2) {
                    final Class<?> keyType = typeArgs.get(0);
                    final Class<?> valueType = typeArgs.get(1);
                    if (keyType.equals(String.class)) {

                        return alg.stringMap(apply(alg, valueType));
                    } else {
                        return alg.map(apply(alg, keyType), apply(alg, valueType));
                    }
                } else {
                    return alg.map(apply(alg, Object.class), apply(alg, Object.class));
                }
            } else if (Collection.class.isAssignableFrom(clazz)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Collection.class);
                if (typeArgs.size() == 1) {
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    return alg.coll(apply(alg, elemType));
                } else {
                    return alg.coll(apply(alg, Object.class));
                }
            } else if (clazz.isInterface()) {
                return alg.interfaceT(clazz);
            } else {
                return applyObject(alg, clazz);
            }
        }
    }

    T applyObject(JavaTypeAlg<T> alg, Class<?> clazz) {
        final Map<String, T> fieldTs = new HashMap<>();
        Class<?> clazz2 = clazz;
        for (int depth = 0; !clazz2.equals(Object.class); depth++) {
            final Field[] fields = clazz2.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = field.getName();
                    final Class<?> fldClass = field.getType();
                    fieldTs.put(fieldName, apply(alg, fldClass));
                }
            }
            clazz2 = clazz2.getSuperclass();
        }

        return alg.object(fieldTs);
    }
}
