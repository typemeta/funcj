package org.typemeta.funcj.codec2.core.utils;

import org.typemeta.funcj.codec2.core.ObjectCreator;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class ReflectionUtils {
    private ReflectionUtils() {}

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
                        .filter(AccessibleObject::isAccessible)
                        //JDK11: .filter(ctor -> ctor.canAccess(null))
                        .findAny()
                        .orElse(ctors.get(0));
                break;
        }

        final ObjectCreator.Checked<T, ReflectiveOperationException> accCtor;
        //JDK11: if (!noArgsCtor.canAccess(null)) {
        if (!noArgsCtor.isAccessible()) {
            noArgsCtor.setAccessible(true);
        }

        accCtor = () -> noArgsCtor.newInstance((Object[]) null);

        return accCtor.unchecked();
    }
}
