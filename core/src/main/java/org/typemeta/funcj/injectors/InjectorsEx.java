package org.typemeta.funcj.injectors;

import java.util.Optional;

public abstract class InjectorsEx {
    public static <ENV, T, EX extends Exception> InjectorEx<ENV, Optional<T>, EX> optional(InjectorEx<ENV, T, EX> injr) {
        return (env, optVal) ->
                optVal.isPresent() ? injr.inject(env, optVal.get()) : env;
    }
}
