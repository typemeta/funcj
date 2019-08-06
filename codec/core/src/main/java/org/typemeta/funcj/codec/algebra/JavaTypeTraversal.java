package org.typemeta.funcj.codec.algebra;

import org.typemeta.funcj.codec.utils.ReflectionUtils;
import org.typemeta.funcj.data.IList;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class JavaTypeTraversal<T> {
    private static final String ARRAY_NAME = "array";

    private final JavaTypeAlg<T> alg;
    private final Map<String, T> cache = new HashMap<>();

    public JavaTypeTraversal(JavaTypeAlg<T> alg) {
        this.alg = alg;
    }

    private T getCache(Class<?> clazz, Function<Class<?>, T> generator) {
        return cache.computeIfAbsent(clazz.getName(), u -> generator.apply(clazz));
    }

    private void putCache(Class<?> clazz, T value) {
        cache.put(clazz.getName(), value);
    }

    public T apply(Class<?> clazz) {
        return getCache(clazz, c -> applyAlg(IList.empty(), c));
    }

    public T apply(IList<String> path, Class<?> clazz) {
        return getCache(clazz, c -> applyAlg(path, c));
    }

    private T applyAlg(IList<String> path, Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return alg.booleanP(path, clazz.getSimpleName());
            } else if (clazz.equals(byte.class)) {
                return alg.byteP(path, clazz.getSimpleName());
            } else if (clazz.equals(char.class)) {
                return alg.charP(path, clazz.getSimpleName());
            } else if (clazz.equals(short.class)) {
                return alg.shortP(path, clazz.getSimpleName());
            } else if (clazz.equals(int.class)) {
                return alg.integerP(path, clazz.getSimpleName());
            } else if (clazz.equals(long.class)) {
                return alg.longP(path, clazz.getSimpleName());
            } else if (clazz.equals(float.class)) {
                return alg.floatP(path, clazz.getSimpleName());
            } else if (clazz.equals(double.class)) {
                return alg.doubleP(path, clazz.getSimpleName());
            } else {
                throw new IllegalStateException("Unexpected primitive type - " + clazz);
            }
        } else {
            if (clazz.isArray()) {
                final IList<String> arrPath = path.add(ARRAY_NAME);
                final Class<?> elemType = clazz.getComponentType();
                if (elemType.equals(boolean.class)) {
                    return alg.booleanArr(arrPath, clazz.getSimpleName());
                } else if (elemType.equals(byte.class)) {
                    return alg.byteArr(arrPath, clazz.getSimpleName());
                } else if (elemType.equals(char.class)) {
                    return alg.charArr(arrPath, clazz.getSimpleName());
                } else if (elemType.equals(short.class)) {
                    return alg.shortArr(arrPath, clazz.getSimpleName());
                } else if (elemType.equals(int.class)) {
                    return alg.integerArr(arrPath, clazz.getSimpleName());
                } else if (elemType.equals(long.class)) {
                    return alg.longArr(arrPath, clazz.getSimpleName());
                } else if (elemType.equals(float.class)) {
                    return alg.floatArr(arrPath, clazz.getSimpleName());
                } else if (elemType.equals(double.class)) {
                    return alg.doubleArr(arrPath, clazz.getSimpleName());
                } else {
                    if (elemType.equals(Boolean.class)) {
                        return alg.booleanArr(arrPath, clazz.getSimpleName());
                    } else if (elemType.equals(Byte.class)) {
                        return alg.byteArr(arrPath, clazz.getSimpleName());
                    } else if (elemType.equals(Character.class)) {
                        return alg.charArr(arrPath, clazz.getSimpleName());
                    } else if (elemType.equals(Short.class)) {
                        return alg.shortArr(arrPath, clazz.getSimpleName());
                    } else if (elemType.equals(Integer.class)) {
                        return alg.integerArr(arrPath, clazz.getSimpleName());
                    } else if (elemType.equals(Long.class)) {
                        return alg.longArr(arrPath, clazz.getSimpleName());
                    } else if (elemType.equals(Float.class)) {
                        return alg.floatArr(arrPath, clazz.getSimpleName());
                    } else if (elemType.equals(Double.class)) {
                        return alg.doubleArr(arrPath, clazz.getSimpleName());
                    } else {
                        return alg.objectArr(
                                arrPath,
                                clazz.getSimpleName(),
                                apply(arrPath.add(elemType.getSimpleName()), elemType)
                        );
                    }
                }
            } else if (clazz.isEnum()) {
                return alg.enumT(path, clazz.getSimpleName(), clazz);
            } else if (ReflectionUtils.isEnumSubType(clazz)) {
                return alg.enumT(path, clazz.getSimpleName(), clazz.getSuperclass());
            } else if (clazz.equals(Boolean.class)) {
                return alg.booleanB(path, clazz.getSimpleName());
            } else if (clazz.equals(Byte.class)) {
                return alg.byteB(path, clazz.getSimpleName());
            } else if (clazz.equals(Character.class)) {
                return alg.charB(path, clazz.getSimpleName());
            } else if (clazz.equals(Short.class)) {
                return alg.shortB(path, clazz.getSimpleName());
            } else if (clazz.equals(Integer.class)) {
                return alg.integerB(path, clazz.getSimpleName());
            } else if (clazz.equals(Long.class)) {
                return alg.longB(path, clazz.getSimpleName());
            } else if (clazz.equals(Float.class)) {
                return alg.floatB(path, clazz.getSimpleName());
            } else if (clazz.equals(Double.class)) {
                return alg.doubleB(path, clazz.getSimpleName());
            } else if (clazz.equals(String.class)) {
                return alg.string(path, clazz.getSimpleName());
            } else if (Map.class.isAssignableFrom(clazz)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Map.class);
                if (typeArgs.size() == 2) {
                    final Class<?> keyType = typeArgs.get(0);
                    final Class<?> valueType = typeArgs.get(1);
                    if (keyType.equals(String.class)) {
                        return alg.stringMap(path, clazz.getSimpleName(), apply(valueType));
                    } else {
                        return alg.map(path, clazz.getSimpleName(), apply(keyType), apply(valueType));
                    }
                } else {
                    return alg.map(path, clazz.getSimpleName(), apply(Object.class), apply(Object.class));
                }
            } else if (Collection.class.isAssignableFrom(clazz)) {
                final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(clazz, Collection.class);
                if (typeArgs.size() == 1) {
                    @SuppressWarnings("unchecked")
                    final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                    return alg.coll(path, clazz.getSimpleName(), apply(elemType));
                } else {
                    return alg.coll(path, clazz.getSimpleName(), apply(Object.class));
                }
            } else if (clazz.isInterface()) {
                return alg.interfaceT(path, clazz.getSimpleName(), clazz);
            } else {
                return applyObject(path, clazz);
            }
        }
    }

    private T applyObject(IList<String> path, Class<?> clazz) {
        final Map<String, T> fieldTs = new HashMap<>();
        Class<?> clazz2 = clazz;
        for (int depth = 0; !clazz2.equals(Object.class); depth++) {
            final Field[] fields = clazz2.getDeclaredFields();
            for (Field field : fields) {
                final int fm = field.getModifiers();
                if (!Modifier.isStatic(fm) && !Modifier.isTransient(fm)) {
                    final String fieldName = field.getName();
                    final Class<?> fldClass = field.getType();

                    final T valueT;
                    if (Map.class.isAssignableFrom(fldClass)) {
                        final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Map.class);
                        if (typeArgs.size() == 2) {
                            final Class<?> keyType = typeArgs.get(0);
                            final Class<?> valueType = typeArgs.get(1);
                            if (keyType.equals(String.class)) {
                                valueT = alg.stringMap(path, fldClass.getSimpleName(), apply(valueType));
                            } else {
                                valueT = alg.map(path, fldClass.getSimpleName(), apply(keyType), apply(valueType));
                            }
                        } else {
                            valueT = alg.map(path, fldClass.getSimpleName(), apply(Object.class), apply(Object.class));
                        }
                    } else if (Collection.class.isAssignableFrom(fldClass)) {
                        final ReflectionUtils.TypeArgs typeArgs = ReflectionUtils.getTypeArgs(field, Collection.class);
                        if (typeArgs.size() == 1) {
                            @SuppressWarnings("unchecked")
                            final Class<Object> elemType = (Class<Object>) typeArgs.get(0);
                            valueT = alg.coll(path, fldClass.getSimpleName(), apply(elemType));
                        } else {
                            valueT = alg.coll(path, fldClass.getSimpleName(), apply(Object.class));
                        }
                    } else {
                        valueT = apply(path.add(fieldName), fldClass);
                    }

                    fieldTs.put(fieldName, valueT);
                }
            }
            clazz2 = clazz2.getSuperclass();
        }

        return alg.object(path, clazz.getSimpleName(), fieldTs);
    }
}
