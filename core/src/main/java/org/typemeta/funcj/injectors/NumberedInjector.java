package org.typemeta.funcj.injectors;

import org.typemeta.funcj.functions.Functions;

@FunctionalInterface
public interface NumberedInjector<ENV, T> {
    ENV inject(ENV env, int n, T value);

    default Injector<ENV, T> bind(int n) {
        return (env, value) -> inject(env, n, value);
    }

    default <U> NumberedInjector<ENV, U> premap(Functions.F<U, T> f) {
        return (env, n, value) -> inject(env, n, f.apply(value));
    }
}
