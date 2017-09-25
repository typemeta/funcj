package org.typemeta.funcj.codec.utils;

import org.typemeta.funcj.codec.CodecException;

public final class OperationNotImplementedException extends CodecException {
    public OperationNotImplementedException() {
        super("Operation not implemented");
    }
}
