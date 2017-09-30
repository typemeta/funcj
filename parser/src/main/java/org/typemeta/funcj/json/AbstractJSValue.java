package org.typemeta.funcj.json;

import org.typemeta.funcj.functions.Functions;

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
        throw Utils.nullTypeError(getClass());
    }

    public JSBool asBool() {
        throw Utils.boolTypeError(getClass());
    }

    public JSNumber asNumber() {
        throw Utils.numberTypeError(getClass());
    }

    public JSString asString() {
        throw Utils.stringTypeError(getClass());
    }

    public JSArray asArray() {
        throw Utils.arrayTypeError(getClass());
    }

    public JSObject asObject() {
        throw Utils.objectTypeError(getClass());
    }
}
