package org.funcj.json;

import org.funcj.document.*;
import org.funcj.util.Functions.F;

public interface JSValue {

    default String toJson(int width)  {
        return DocWriter.format(width, toDocument());
    }

    StringBuilder toString(StringBuilder sb);

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
