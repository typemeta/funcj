package org.typemeta.funcj.injectors;

import org.typemeta.funcj.functions.Functions;

@FunctionalInterface
public interface Injector<ENV, T> {

    ENV inject(ENV env, T value);

    default <U> Injector<ENV, U> map(Functions.F<U, T> f) {
        return (env, value) ->inject(env, f.apply(value));
    }
}
