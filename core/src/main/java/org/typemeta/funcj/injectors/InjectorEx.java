package org.typemeta.funcj.injectors;

@FunctionalInterface
public interface InjectorEx<ENV, T, EX extends Exception> {
    ENV inject(ENV env, T value) throws EX;
}
