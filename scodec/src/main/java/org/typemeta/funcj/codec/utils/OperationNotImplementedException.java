package org.typemeta.funcj.codec.utils;

import org.typemeta.funcj.codec.CodecRuntimeException;

public final class OperationNotImplementedException extends CodecRuntimeException {
    public OperationNotImplementedException() {
        super("Operation not implemented");
    }
}
