package org.typemeta.funcj.codec;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Interface for constructing an uninitialised value of type {@code T}.
 * @param <T> type of value to be constructed
 */
public interface TypeConstructor<T> {
    /**
     * Create a {@code TypeConstructor} for the specified class.
     * @param clazz type descriptor which conveys the type argument
     * @param <T> the type we want a {@code TypeConstructor} for
     * @return a {@code TypeConstructor}
     * @throws InstantiationException if type has no constructors
     */
    static <T> TypeConstructor<T> createTypeConstructor(Class<T> clazz)
            throws InstantiationException {
        // Get the empty-arg constructors.
        final List<Constructor<T>> ctors =
                Arrays.stream(clazz.getDeclaredConstructors())
                        .filter(ctor -> ctor.getParameterCount() == 0)
                        .map(ctor -> (Constructor<T>)ctor)
                        .collect(toList());

        // Select the an accessible ctor if there is one, otherwise just the first one.
        final Constructor<T> defCtor;
        switch (ctors.size()) {
            case 0:
                throw new InstantiationException(clazz.getName() + " has no default constructor");
            case 1:
                defCtor = ctors.get(0);
                break;
            default:
                defCtor = ctors.stream()
                        .filter(AccessibleObject::isAccessible)
                        .findAny()
                        .orElse(ctors.get(0));
                break;
        }

        // Create a TypeConstructor from the ctor.
        if (defCtor.isAccessible()) {
            return () -> defCtor.newInstance((Object[])null);
        } else {
            return () -> {
                defCtor.setAccessible(true);
                final T result = defCtor.newInstance((Object[])null);
                defCtor.setAccessible(false);
                return result;
            };
        }
    }

    /**
     * Construct a value of type {@code T}.
     * @return newly constructed value
     * @throws ReflectiveOperationException typically thrown by {@link java.lang.reflect.Constructor#newInstance(Object[])}
     */
    T construct() throws ReflectiveOperationException;
}
