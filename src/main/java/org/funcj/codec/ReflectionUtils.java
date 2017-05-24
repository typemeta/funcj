package org.funcj.codec;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ReflectionUtils {

    public static List<Class<?>> getTypeArgs(Field field, Class iface) {
        final Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            final ParameterizedType pt =(ParameterizedType)type;
            final Type rawType = pt.getRawType();
            if (rawType.equals(iface)) {
                return getGenTypeArgs(pt);
            }
        }
        return Collections.emptyList();
    }

    public static List<Class<?>> getTypeArgs(Class implClass, Class iface) {
        final List<ParameterizedType> genIfaces =
                Arrays.stream(implClass.getGenericInterfaces())
                        .filter(t -> t instanceof ParameterizedType)
                        .map(t -> (ParameterizedType) t)
                        .collect(toList());
        return genIfaces.stream()
                .filter(pt -> pt.getRawType().equals(iface))
                .findFirst()
                .map(pt -> getGenTypeArgs(pt))
                .orElse(Collections.emptyList());
    }

    public static List<Class<?>> getGenTypeArgs(ParameterizedType type) {
        return Arrays.stream(type.getActualTypeArguments())
                .filter(t -> t instanceof Class)
                .map(t -> (Class<?>)t)
                .collect(toList());
    }
}
