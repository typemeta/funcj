package org.funcj.json;

import org.funcj.document.*;
import org.funcj.util.Functions;

public enum JSNull implements JSValue {
    INSTANCE;

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Document toDocument() {
        return API.text(toString());
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(toString());
    }

    @Override
    public <T> T match(
            Functions.F<JSNull, T> nl,
            Functions.F<JSBool, T> bl,
            Functions.F<JSNumber, T> nm,
            Functions.F<JSString, T> st,
            Functions.F<JSArray, T> ar,
            Functions.F<JSObject, T> ob) {
        return nl.apply(this);
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isBool() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public JSNull asNull() {
        return this;
    }

    @Override
    public JSBool asBool() {
        throw JsonUtils.asBool(getClass());
    }

    @Override
    public JSNumber asNumber() {
        throw JsonUtils.asNumber(getClass());
    }

    @Override
    public JSString asString() {
        throw JsonUtils.asString(getClass());
    }

    @Override
    public JSArray asArray() {
        throw JsonUtils.asArray(getClass());
    }

    @Override
    public JSObject asObject() {
        throw JsonUtils.asObject(getClass());
    }
}
