package org.typemeta.funcj.control;

import org.typemeta.funcj.functions.Functions.F0;

public interface Trampoline<T> {

    static <T> Done<T> done(T result) {
        return new Done<T>(result);
    }

    static <T> More<T> more(F0<Trampoline<T>> next) {
        return new More<T>(next);
    }

    final class Done<T> implements Trampoline<T> {
        final T result;

        public Done(T result) {
            this.result = result;
        }

        @Override
        public T compute() {
            return result;
        }
    }

    final class More<T> implements Trampoline<T> {
        final F0<Trampoline<T>> next;

        public More(F0<Trampoline<T>> next) {
            this.next = next;
        }

        @Override
        public T compute() {
            Trampoline<T> t = this;
            while(true) {
                if (t instanceof More) {
                    t = ((More<T>)t).next.apply();
                } else {
                    return ((Done<T>)t).result;
                }
            }
        }
    }

    T compute();
}
