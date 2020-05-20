package org.typemeta.funcj.injectors;

@FunctionalInterface
public interface NumberedInjector<ENV, T> {
    ENV inject(ENV env, int n, T value);

    default Injector<ENV, T> bind(int n) {
        return (env, value) -> inject(env, n, value);
    }
}
