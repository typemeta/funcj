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
        throw JsonUtils.asNull(getClass());
    }

    public JSBool asBool() {
        throw JsonUtils.asBool(getClass());
    }

    public JSNumber asNumber() {
        throw JsonUtils.asNumber(getClass());
    }

    public JSString asString() {
        throw JsonUtils.asString(getClass());
    }

    public JSArray asArray() {
        throw JsonUtils.asArray(getClass());
    }

    public JSObject asObject() {
        throw JsonUtils.asObject(getClass());
    }
}
