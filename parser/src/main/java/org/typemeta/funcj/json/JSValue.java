package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.functions.Functions.F;

/**
 * Common interface for types represent JSON values.
 */
public interface JSValue {

    /**
     * Pretty-print this value as a JSON string.
     * @param width     maximum line length
     * @return          string representation of formatted JSON
     */
    default String toString(int width)  {
        return DocFormat.format(width, toDocument());
    }

    /**
     * Write this value into the supplied {@code StringBuilder}.
     * @param sb        the {@code StringBuilder}
     * @return          the {@code StringBuilder}
     */
    /* private */ StringBuilder toString(StringBuilder sb);

    <T> T match(
        F<JSNull, T> nl,
        F<JSBool, T> bl,
        F<JSNumber, T> nm,
        F<JSString, T> st,
        F<JSArray, T> ar,
        F<JSObject, T> ob
    );

    boolean isNull();
    boolean isBool();
    boolean isNumber();
    boolean isString();
    boolean isArray();
    boolean isObject();

    JSNull asNull();
    JSBool asBool();
    JSNumber asNumber();
    JSString asString();
    JSArray asArray();
    JSObject asObject();

    Document toDocument();
}
