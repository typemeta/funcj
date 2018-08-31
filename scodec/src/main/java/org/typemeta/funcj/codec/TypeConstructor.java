package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.FunctionsGenEx.F0;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Interface for constructing an uninitialised value of type {@code T}.
 * @param <T>       the type of value to be constructed
 */
@SuppressWarnings("unchecked")
public interface TypeConstructor<T> {
    /**
     * Create a {@code TypeConstructor} for the specified class.
     * @param clazz     type descriptor which conveys the type argument
     * @param <T>       the type we want a {@code TypeConstructor} for
     * @return          a {@code TypeConstructor}
     * @throws CodecException if type has no constructors
     */
    static <T> TypeConstructor<T> create(Class<T> clazz)
            throws CodecException {
        // Get the empty-arg constructors.
        final List<Constructor<T>> ctors =
                Arrays.stream(clazz.getDeclaredConstructors())
                        .filter(ctor -> ctor.getParameterCount() == 0)
                        .map(ctor -> (Constructor<T>)ctor)
                        .collect(toList());

        // Select the accessible ctor if there is only one, otherwise just use
        // the first one.
        final Constructor<T> defCtor;
        switch (ctors.size()) {
            case 0:
                throw new CodecException(clazz.getName() + " has no default constructor");
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

        final F0<T, ReflectiveOperationException> ctor = () -> defCtor.newInstance((Object[])null);

        final F0<T, ReflectiveOperationException> accCtor;
        if (defCtor.isAccessible()) {
            accCtor = ctor;
        } else {
            accCtor = () -> {
                defCtor.setAccessible(true);
                final T result = ctor.apply();
                defCtor.setAccessible(false);
                return result;
            };
        }

        return () -> {
            try {
                return accCtor.apply();
            } catch (ReflectiveOperationException ex) {
                throw new CodecException("Unable to construct object of type '" + clazz.getName() + "'", ex);
            }
        };
    }

    /**
     * Construct a value of type {@code T}.
     * @return          newly constructed value
     */
    T construct();
}
