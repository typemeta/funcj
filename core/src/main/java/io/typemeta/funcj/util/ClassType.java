package io.typemeta.funcj.util;

import io.typemeta.funcj.util.Functions.F;

import java.util.Optional;

/**
 * A wrapper for {@link Class} that adds utility methods.
 * @param <T>       the class type
 */
public class ClassType<T> {

    /**
     * Construct a {@code ClassType} instance from a {@link Class}.
     * @param clazz     the class
     * @param <T>       the class type
     * @return          the new {@code ClassType} instance
     */
    public static <T> ClassType<T> of(Class<T> clazz) {
        return new ClassType<T>(clazz);
    }

    private final Class<T> clazz;

    private ClassType(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * If it has the same type then cast an object to this type and wrap in {@link Optional},
     * otherwise return an empty {@code Optional}.
     * @param value     the object to cast
     * @return          an {@code Optional} that wraps the cast value
     */
    public Optional<T> cast(Object value) {
        if (clazz.isInstance(value)) {
            return Optional.of((T)value);
        } else {
            return Optional.empty();
        }
    }

    /**
     * If the supplied object value has the same type as {@link ClassType#clazz},
     * then cast to {@code T} and apply function {@code success},
     * otherwise apply function {@code fail}.
     * @param value     the object value to test
     * @param success   function to be applied if the object value matches
     * @param fail      function to be applied if the object value does not match
     * @param <S>       the object value type
     * @param <R>       the return type of both functions
     * @return          the result of appllying either function
     */
    public <S, R> R match(S value, F<T, R> success, F<S, R> fail) {
        if (clazz.isInstance(value)) {
            return success.apply((T)value);
        } else {
            return fail.apply(value);
        }
    }
}
