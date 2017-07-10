package org.funcj.codec.utils;

import org.funcj.codec.CodecException;

public final class OperationNotImplementedException extends CodecException {
    public OperationNotImplementedException() {
        super("Operation not implemented");
    }
}
