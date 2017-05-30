package org.funcj.codec;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ReflectionUtils {

    public static class TypeArgs {
        public final List<Class<?>> typeArgs;

        public TypeArgs(List<Class<?>> typeArgs) {
            this.typeArgs = typeArgs;
        }

        public TypeArgs() {
            this.typeArgs = Collections.emptyList();
        }

        public Class<?> get(int index) {
            if (index < typeArgs.size()) {
                return typeArgs.get(index);
            } else {
                return Object.class;
            }
        }
    }

    public static TypeArgs getTypeArgs(Field field, Class iface) {
        final Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)type;
            final Type[] typeArgs = pt.getActualTypeArguments();
            final List<Class<?>> results = new ArrayList<>(typeArgs.length);
            for (Type typeArg : pt.getActualTypeArguments()) {
                if (typeArg instanceof Class) {
                    results.add((Class<?>)typeArg);
                } else if (typeArg instanceof TypeVariable) {
                    final TypeVariable tv = (TypeVariable)typeArg;
                    final Type[] bounds = tv.getBounds();
                    if (bounds.length == 1 && bounds[0] instanceof Class) {
                        results.add((Class<?>) bounds[0]);
                    } else {
                        results.add(Object.class);
                    }
                } else {
                    results.add(Object.class);
                }
            }

            return new TypeArgs(results);
        }

        return new TypeArgs();
    }

    public static TypeArgs getTypeArgs(Class implClass, Class iface) {
        final List<ParameterizedType> genIfaces =
                Arrays.stream(implClass.getGenericInterfaces())
                        .filter(t -> t instanceof ParameterizedType)
                        .map(t -> (ParameterizedType) t)
                        .collect(toList());
        return genIfaces.stream()
                .filter(pt -> pt.getRawType().equals(iface))
                .findFirst()
                .map(ReflectionUtils::getGenTypeArgs)
                .orElseGet(TypeArgs::new);
    }

    public static TypeArgs getGenTypeArgs(ParameterizedType type) {
        final List<Class<?>> typeArgs =
                Arrays.stream(type.getActualTypeArguments())
                        .filter(t -> t instanceof Class)
                        .map(t -> (Class<?>)t)
                        .collect(toList());
        return new TypeArgs(typeArgs);
    }
}
