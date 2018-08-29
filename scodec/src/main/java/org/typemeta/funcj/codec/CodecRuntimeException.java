package org.typemeta.funcj.codec;

/**
 * Exception class for encoding/decoding exceptions.
 */
public class CodecRuntimeException extends RuntimeException {
    public CodecRuntimeException() {
    }

    public CodecRuntimeException(String message) {
        super(message);
    }

    public CodecRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodecRuntimeException(Throwable cause) {
        super(cause);
    }

    public CodecRuntimeException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
