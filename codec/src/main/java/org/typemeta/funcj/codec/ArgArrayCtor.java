package org.typemeta.funcj.codec;

public interface ArgArrayCtor<T> {
    T construct(Object[] args);
}
