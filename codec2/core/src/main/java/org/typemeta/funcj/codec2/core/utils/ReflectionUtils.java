package org.typemeta.funcj.codec2.core.utils;

import org.typemeta.funcj.codec2.core.ObjectCreator;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class ReflectionUtils {
    private ReflectionUtils() {}

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

    public static <T> ObjectCreator<T> createObjectCreator(Class<T> type) {
        // Get the empty-arg constructors.
        final List<Constructor<T>> ctors =
                Arrays.stream(type.getDeclaredConstructors())
                        .filter(ctor -> ctor.getParameterCount() == 0)
                        .map(ctor -> (Constructor<T>)ctor)
                        .collect(toList());

        // Select the accessible ctor if there is only one, otherwise just use any one.
        final Constructor<T> noArgsCtor;
        switch (ctors.size()) {
            case 0:
                // No default constructor.
                return null;
            case 1:
                noArgsCtor = ctors.get(0);
                break;
            default:
                noArgsCtor = ctors.stream()
                        .filter(ReflectionUtils::isAccessible)
                        .findAny()
                        .orElse(ctors.get(0));
                break;
        }

        if (!isAccessible(noArgsCtor)) {
            noArgsCtor.setAccessible(true);
        }

        final ObjectCreator.Checked<T, ReflectiveOperationException> accCtor =
                () -> noArgsCtor.newInstance((Object[]) null);

        return accCtor.unchecked();
    }

    private static boolean isAccessible(AccessibleObject ao) {
        // JDK11: return ao.canAccess(null);
        return ao.isAccessible();
    }
}
