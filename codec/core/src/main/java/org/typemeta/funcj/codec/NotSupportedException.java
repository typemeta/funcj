package org.typemeta.funcj.codec;

public class NotSupportedException extends CodecException {
    public NotSupportedException() {
        super("Operation not supported");
    }
}
