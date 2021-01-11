package org.typemeta.funcj.codec2.core;

import org.typemeta.funcj.codec2.core.utils.CodecException;

public interface ObjectCreator<T> {
    interface Checked<T, EX extends Exception> {
        T create() throws EX;

        default ObjectCreator<T> unchecked() {
            return () -> {
                try {
                    return create();
                } catch (Exception ex) {
                    throw new CodecException("Failed to create object", ex);
                }
            };
        }
    }

    T create();
}
