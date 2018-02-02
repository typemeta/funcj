package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

/**
 * Models a JSON null value.
 */
public enum JsNull implements JsValue {
    NULL;

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Type type() {
        return Type.NULL;
    }

    @Override
    public <T> T match(
            Functions.F<JsNull, T> fNull,
            Functions.F<JsBool, T> fBool,
            Functions.F<JsNumber, T> fNum,
            Functions.F<JsString, T> fStr,
            Functions.F<JsArray, T> fArr,
            Functions.F<JsObject, T> fObj) {
        return fNull.apply(this);
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
    public JsNull asNull() {
        return this;
    }

    @Override
    public JsBool asBool() {
        throw Utils.boolTypeError(getClass());
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
