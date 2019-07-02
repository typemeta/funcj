package org.typemeta.funcj.json.parser;

import org.typemeta.funcj.functions.FunctionsEx;
import org.typemeta.funcj.functions.SideEffectEx;

/**
 * Exception class for JSON exceptions.
 */
public class JsonException extends RuntimeException {
    public static <T> T wrap(FunctionsEx.F0<T> f) {
        try {
            return f.apply();
        } catch (Exception ex) {
            throw new JsonException(ex);
        }
    }

    public static void wrap(SideEffectEx.F0 f) {
        try {
            f.apply();
        } catch (Exception ex) {
            throw new JsonException(ex);
        }
    }

    public JsonException() {
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
