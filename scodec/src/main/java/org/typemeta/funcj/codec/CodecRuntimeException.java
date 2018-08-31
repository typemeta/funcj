package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.*;

/**
 * Exception class for encoding/decoding exceptions.
 */
public class CodecRuntimeException extends RuntimeException {
    public static <T> T wrap(FunctionsEx.F0<T> f) {
        try {
            return f.apply();
        } catch (Exception ex) {
            throw new CodecRuntimeException(ex);
        }
    }

    public static void wrap(SideEffectEx.F0 f) {
        try {
            f.apply();
        } catch (Exception ex) {
            throw new CodecRuntimeException(ex);
        }
    }

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
