package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.FunctionsGenEx.F0;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Interface for constructing an uninitialised value of type {@code T},
 * using a no-args constructor
 * @param <T>       the type of value to be constructed
 */
@SuppressWarnings("unchecked")
public interface NoArgsTypeCtor<T> {
    /**
     * Create a {@code NoArgsTypeCtor} for the specified class.
     * @param clazz     type descriptor which conveys the type argument
     * @param <T>       the type we want a {@code NoArgsTypeCtor} for
     * @return          an {@code Optional} wrapping a {@code NoArgsTypeCtor} if one exists,
     *                  otherwise an empty Optional
     */
    static <T> Optional<NoArgsTypeCtor<T>> create(Class<T> clazz)
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
                // No default constructor.
                return Optional.empty();
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
        if (!noArgsCtor.isAccessible()) {
            noArgsCtor.setAccessible(true);
        }

        accCtor = () -> noArgsCtor.newInstance((Object[])null);

        return Optional.of(() -> {
            try {
                return accCtor.apply();
            } catch (ReflectiveOperationException ex) {
                throw new CodecException("Unable to construct object of type '" + clazz.getName() + "'", ex);
            }
        });
    }

    /**
     * Construct a value of type {@code T}.
     * @return          the newly constructed value
     */
    T construct();
}
