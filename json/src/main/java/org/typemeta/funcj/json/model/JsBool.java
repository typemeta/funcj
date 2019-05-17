package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

/**
 * Models JSON true and false values.
 */
public enum JsBool implements JsValue {
    TRUE(true),
    FALSE(false);

    private final boolean value;

    JsBool(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Type type() {
        return Type.BOOL;
    }

    @Override
    public <T> T match(
            Functions.F<JsNull, T> fNull,
            Functions.F<JsBool, T> fBool,
            Functions.F<JsNumber, T> fNum,
            Functions.F<JsString, T> fStr,
            Functions.F<JsArray, T> fArr,
            Functions.F<JsObject, T> fObj) {
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
    public JsNull asNull() {
        throw Utils.nullTypeError(getClass());
    }

    @Override
    public JsBool asBool() {
        return this;
    }

    @Override
    public JsNumber asNumber() {
        throw Utils.numberTypeError(getClass());
    }

    @Override
    public JsString asString() {
        throw Utils.stringTypeError(getClass());
    }

    @Override
    public JsArray asArray() {
        throw Utils.arrayTypeError(getClass());
    }

    @Override
    public JsObject asObject() {
        throw Utils.objectTypeError(getClass());
    }
}
