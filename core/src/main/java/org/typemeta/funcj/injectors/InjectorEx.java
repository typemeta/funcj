package org.typemeta.funcj.injectors;

import org.typemeta.funcj.util.Exceptions;

/**
 * A variation of {@link Injector} that may throw an exception.
 * @param <ENV>     the context type
 * @param <T>       the value type
 * @param <EX>      the exception type
 */
@FunctionalInterface
public interface InjectorEx<ENV, T, EX extends Exception> {
    /**
     * Inject a value into a context.
     * @param env       the context
     * @param value     the value
     * @return          the new context
     * @throws EX       if the injection fails
     */
    ENV inject(ENV env, T value) throws EX;

    /**
     * Return an unchecked equivalent of this injector.
     * @return an unchecked equivalent of this injector
     */
    default Injector<ENV, T> unchecked() {
        return (env, value) -> {
            try {
                return inject(env, value);
            } catch (Exception ex) {
                return Exceptions.throwUnchecked(ex);
            }
        };
    }
}
