package org.typemeta.funcj.codec.utils;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Utility methods relating to Reflection.
 */
public abstract class ReflectionUtils {
    private static final int ENUM      = 0x00004000;

    /**
     * Determine whether a class is an enum type.
     * @param clazz     the class to check
     * @return          true if the class is an enum type.
     */
    public static boolean isEnumSubType(Class<?> clazz) {
        return (clazz.getModifiers() & ENUM) != 0 &&
                (clazz.getSuperclass() != null &&
                        clazz.getSuperclass().getSuperclass() == java.lang.Enum.class
                );
    }

    /**
     * Return the {@link Class} for the given name.
     * Alternative to {@link Class#forName(String)}, that throws a {@link RuntimeException}.
     * @param className the class name
     * @return          the class
     */
    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Data structure representing the type arguments of a generic type.
     */
    public static class TypeArgs {
        public final List<Class<?>> typeArgs;

        public TypeArgs(List<Class<?>> typeArgs) {
            this.typeArgs = typeArgs;
        }

        public TypeArgs() {
            this.typeArgs = Collections.emptyList();
        }

        public int size() {
            return typeArgs.size();
        }

        public Class<?> get(int index) {
            if (index < typeArgs.size()) {
                return typeArgs.get(index);
            } else {
                return Object.class;
            }
        }
    }

    /**
     * Inspect the given field. If it implements the given generic interface
     * then extract and return the type arguments.
     * @param field     the field to inspect
     * @param iface     the generic interface
     * @return          the type arguments
     */
    public static TypeArgs getTypeArgs(Field field, Class<?> iface) {
        final Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType() instanceof Class &&
                    iface.isAssignableFrom((Class)pt.getRawType())) {
                return new TypeArgs(getTypeArgsImpl(pt));
            }
        }

        return new TypeArgs();
    }

    /**
     * Inspect the given type. If it implements the given generic interface
     * then extract and return the type arguments.
     * @param implClass the type to inspect
     * @param iface     the generic interface
     * @return          the type arguments
     */
    public static TypeArgs getTypeArgs(Class<?> implClass, Class<?> iface) {
        return new TypeArgs(
                getTypeArgsOpt(implClass, iface)
                        .orElseGet(() -> getParentTypeArgs(implClass, iface))
        );
    }

    private static Optional<List<Class<?>>> getTypeArgsOpt(Class<?> implClass, Class<?> iface) {
        return Arrays.stream(implClass.getGenericInterfaces())
                .filter(t -> t instanceof ParameterizedType)
                .map(t -> (ParameterizedType) t)
                .filter(pt -> pt.getRawType() instanceof Class)
                .filter(pt -> iface.isAssignableFrom((Class)pt.getRawType()))
                .findFirst()
                .map(ReflectionUtils::getTypeArgsImpl);
    }

    private static List<Class<?>> getParentTypeArgs(Class<?> implClass, Class<?> iface) {
        // Work our way up the class hierarchy.
        Class<?> parent = implClass.getSuperclass();
        while (parent != null) {
            // Does parent implement iface?
            if (iface.isAssignableFrom(parent)) {
                final Type type = implClass.getGenericSuperclass();
                if (type instanceof ParameterizedType) {
                    return getTypeArgsImpl((ParameterizedType)type);
                }
            }

            // Can we get the type args from the parent?
            final Optional<List<Class<?>>> typeArgsOpt = getTypeArgsOpt(parent, iface);
            if (typeArgsOpt.isPresent()) {
                return typeArgsOpt.get();
            }

            // Both failed, so move ot the next parent.
            implClass = parent;
            parent = implClass.getSuperclass();
        }

        // This indicates a failure in the sense that we were not
        // able to determine the generic type arguments via any of the class parents.
        return Collections.emptyList();
    }

    private static List<Class<?>> getTypeArgsImpl(ParameterizedType pt) {
        return Arrays.stream(pt.getActualTypeArguments())
                .map(ReflectionUtils::determineConcreteType)
                .collect(toList());
    }

    private static Class<?> determineConcreteType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof TypeVariable) {
            final TypeVariable<?> tv = (TypeVariable<?>) type;
            final Type[] bounds = tv.getBounds();
            if (bounds.length == 1 && bounds[0] instanceof Class) {
                return (Class<?>) bounds[0];
            } else {
                return Object.class;
            }
        } else if (type instanceof ParameterizedType) {
            final ParameterizedType typeArgPt = (ParameterizedType) type;
            return (Class)typeArgPt.getRawType();
        } else {
            return Object.class;
        }
    }
}
