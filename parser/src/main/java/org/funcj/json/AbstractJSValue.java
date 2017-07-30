package org.funcj.json;

import org.funcj.util.Functions;

public abstract class AbstractJSValue implements JSValue {

    public abstract StringBuilder toString(StringBuilder sb);

    public abstract <T> T match(
            Functions.F<JSNull, T> nl,
            Functions.F<JSBool, T> bl,
            Functions.F<JSNumber, T> nm,
            Functions.F<JSString, T> st,
            Functions.F<JSArray, T> ar,
            Functions.F<JSObject, T> ob
    );

    public boolean isNull() {
        return false;
    }

    public boolean isBool() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public JSNull asNull() {
        throw JsonUtils.nullTypeError(getClass());
    }

    public JSBool asBool() {
        throw JsonUtils.boolTypeError(getClass());
    }

    public JSNumber asNumber() {
        throw JsonUtils.numberTypeError(getClass());
    }

    public JSString asString() {
        throw JsonUtils.stringTypeError(getClass());
    }

    public JSArray asArray() {
        throw JsonUtils.arrayTypeError(getClass());
    }

    public JSObject asObject() {
        throw JsonUtils.objectTypeError(getClass());
    }
}
