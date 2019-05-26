package org.typemeta.funcj.codec.utils;

import org.typemeta.funcj.functions.*;

/**
 * Exception class for encoding/decoding exceptions.
 */
public class CodecException extends RuntimeException {
    public static <T> T wrap(FunctionsEx.F0<T> f) {
        try {
            return f.apply();
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }

    public static void wrap(SideEffectEx.F0 f) {
        try {
            f.apply();
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }

    public CodecException() {
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }

    public CodecException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
