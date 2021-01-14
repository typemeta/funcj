package org.typemeta.funcj.injectors;

import org.typemeta.funcj.functions.Functions;

/**
 * An {@link Injector} injects values into a context.
 * @param <ENV>     the context type
 * @param <T>       the value type
 */
@FunctionalInterface
public interface Injector<ENV, T> {
    /**
     * Inject a value into a context.
     * @param env       the context
     * @param value     the value
     * @return          the new context
     */
    ENV inject(ENV env, T value);

    default <U> Injector<ENV, U> premap(Functions.F<U, T> f) {
        return (env, value) -> inject(env, f.apply(value));
    }
}
