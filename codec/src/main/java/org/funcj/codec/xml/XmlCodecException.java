package org.funcj.codec.xml;

import org.funcj.codec.CodecException;

public class XmlCodecException extends CodecException {
    public XmlCodecException() {
    }

    public XmlCodecException(String message) {
        super(message);
    }

    public XmlCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlCodecException(Throwable cause) {
        super(cause);
    }

    public XmlCodecException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
