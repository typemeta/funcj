package io.typemeta.funcj.codec.utils;

import io.typemeta.funcj.codec.CodecException;

public final class OperationNotImplementedException extends CodecException {
    public OperationNotImplementedException() {
        super("Operation not implemented");
    }
}
