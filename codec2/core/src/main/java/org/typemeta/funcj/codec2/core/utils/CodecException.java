package org.typemeta.funcj.codec2.core.utils;

/**
 * Exception class for encoding/decoding exceptions.
 */
public class CodecException extends RuntimeException {

    public interface Throws0 {
        void apply() throws Exception;
    }

    public static void wrap(Throws0 f) {
        try {
            f.apply();
        } catch (Exception ex) {
            throw new CodecException(ex);
        }
    }

    public interface Throws1<R> {
        R apply() throws Exception;
    }

    public static <T> T wrap(Throws1<T> f) {
        try {
            return f.apply();
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
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
