package org.funcj.codec.utils;

import org.funcj.codec.CodecException;

public abstract class Utils {
    public static RuntimeException opnNotImplError() {
        return new CodecException("Operation not implemented");
    }
}
