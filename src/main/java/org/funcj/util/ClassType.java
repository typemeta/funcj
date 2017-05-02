package org.funcj.util;

import org.funcj.util.Functions.F;

import java.util.Optional;

public class ClassType<T> {

    public static <T> ClassType<T> of(Class<T> clazz) {
        return new ClassType<T>(clazz);
    }

    private final Class<T> clazz;

    private ClassType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Optional<T> cast(Object value) {
        if (clazz.isInstance(value)) {
            return Optional.of((T)value);
        } else {
            return Optional.empty();
        }
    }

    public <S, R> R match(S value, F<T, R> success, F<S, R> fail) {
        if (clazz.isInstance(value)) {
            return success.apply((T)value);
        } else {
            return fail.apply(value);
        }
    }
}
