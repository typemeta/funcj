package org.typemeta.funcj.injectors;

import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

@FunctionalInterface
public interface NumberedInjectorEx<ENV, T, EX extends Exception> {
    ENV inject(ENV env, int n, T value) throws EX;

    default InjectorEx<ENV, T, EX> bind(int n) throws EX {
        return (env, value) -> inject(env, n, value);
    }

    default <U> NumberedInjectorEx<ENV, U, EX> premap(Functions.F<U, T> f) {
        return (env, n, value) -> inject(env, n, f.apply(value));
    }

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
