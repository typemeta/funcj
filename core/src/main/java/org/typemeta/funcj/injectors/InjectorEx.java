package org.typemeta.funcj.injectors;

import org.typemeta.funcj.util.Exceptions;

@FunctionalInterface
public interface InjectorEx<ENV, T, EX extends Exception> {
    ENV inject(ENV env, T value) throws EX;

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
