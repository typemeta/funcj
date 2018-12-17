package org.typemeta.funcj.codec.utils;

public class NotSupportedException extends CodecException {
    public NotSupportedException() {
        super("Operation not supported");
    }

    public NotSupportedException(String message) {
        super(message);
    }
}
