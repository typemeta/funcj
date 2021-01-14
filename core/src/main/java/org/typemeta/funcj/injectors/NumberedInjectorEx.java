package org.typemeta.funcj.injectors;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

/**
 * A variation of {@link NumberedInjector} that may throw an exception.
 * @param <ENV>     the context type
 * @param <T>       the value type
 * @param <EX>      the exception type
 */
@FunctionalInterface
public interface NumberedInjectorEx<ENV, T, EX extends Exception> {
    /**
     * Inject a value into a context.
     * @param env       the context
     * @param n         the index
     * @param value     the value
     * @return          the new context
     * @throws EX       if the injection fails
     */
    ENV inject(ENV env, int n, T value) throws EX;

    /**
     * Bind this injector to an index.
     * @param n         the index
     * @return          the new injector
     */
    default InjectorEx<ENV, T, EX> bind(int n) {
        return (env, value) -> inject(env, n, value);
    }

    /**
     * Return an injector which first applies the given function to the value.
     * @param f         the function
     * @param <U>       the function return type
     * @return          the new injector
     */
    default <U> NumberedInjectorEx<ENV, U, EX> premap(Functions.F<U, T> f) {
        return (env, n, value) -> inject(env, n, f.apply(value));
    }

    /**
     * Return an unchecked equivalent of this injector.
     * @return an unchecked equivalent of this injector
     */
    default NumberedInjector<ENV, T> unchecked() {
        return (env, n, value) -> {
            try {
                return inject(env, n, value);
            } catch (Exception ex) {
                return Exceptions.throwUnchecked(ex);
            }
        };
    }
}
