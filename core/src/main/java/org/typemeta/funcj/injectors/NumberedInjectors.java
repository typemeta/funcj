package org.typemeta.funcj.injectors;

import java.util.Optional;

public abstract class NumberedInjectors {

    public static <ENV, T> NumberedInjector<ENV, Optional<T>> optional(NumberedInjector<ENV, T> injr) {
        return (env, n, optVal) ->
                optVal.isPresent() ? injr.inject(env, n, optVal.get()) : env;
    }
}
