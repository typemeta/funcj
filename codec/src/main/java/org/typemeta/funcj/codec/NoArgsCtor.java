package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.FunctionsGenEx.F0;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Interface for constructing an uninitialised value of type {@code T},
 * using a no-args constructor
 * @param <T>       the type of value to be constructed
 */
@SuppressWarnings("unchecked")
public interface NoArgsCtor<T> {
    /**
     * Create a {@code TypeConstructor} for the specified class.
     * @param clazz     type descriptor which conveys the type argument
     * @param <T>       the type we want a {@codeNoArgsCtor} for
     * @return          a {@codeNoArgsCtor}
     * @throws CodecException if the class has no no-args constructors,
     *                  or if it is not accessible.
     */
    static <T> NoArgsCtor<T> create(Class<T> clazz)
            throws CodecException {
        // Get the empty-arg constructors.
        final List<Constructor<T>> ctors =
                Arrays.stream(clazz.getDeclaredConstructors())
                        .filter(ctor -> ctor.getParameterCount() == 0)
                        .map(ctor -> (Constructor<T>)ctor)
                        .collect(toList());

        // Select the accessible ctor if there is only one, otherwise just use any one.
        final Constructor<T> noArgsCtor;
        switch (ctors.size()) {
            case 0:
                throw new CodecException(clazz.getName() + " has no default constructor");
            case 1:
                noArgsCtor = ctors.get(0);
                break;
            default:
                noArgsCtor = ctors.stream()
                        .filter(AccessibleObject::isAccessible)
                        .findAny()
                        .orElse(ctors.get(0));
                break;
        }

        final F0<T, ReflectiveOperationException> accCtor;
        if (noArgsCtor.isAccessible()) {
            accCtor = () -> noArgsCtor.newInstance((Object[])null);
        } else {
            noArgsCtor.setAccessible(true);
            accCtor = () -> {
                return noArgsCtor.newInstance((Object[])null);
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
