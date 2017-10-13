package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.functions.Functions;

/**
 * Models JSON true and false values.
 */
public enum JSBool implements JSValue {
    TRUE(true),
    FALSE(false);

    private final boolean value;

    JSBool(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
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
            Functions.F<JSNull, T> fNull,
            Functions.F<JSBool, T> fBool,
            Functions.F<JSNumber, T> fNum,
            Functions.F<JSString, T> fStr,
            Functions.F<JSArray, T> fArr,
            Functions.F<JSObject, T> fObj) {
        return fBool.apply(this);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isBool() {
        return true;
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
        throw Utils.nullTypeError(getClass());
    }

    @Override
    public JSBool asBool() {
        return this;
    }

    @Override
    public JSNumber asNumber() {
        throw Utils.numberTypeError(getClass());
    }

    @Override
    public JSString asString() {
        throw Utils.stringTypeError(getClass());
    }

    @Override
    public JSArray asArray() {
        throw Utils.arrayTypeError(getClass());
    }

    @Override
    public JSObject asObject() {
        throw Utils.objectTypeError(getClass());
    }
}
