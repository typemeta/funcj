package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

/**
 * Models a JSON string.
 */
public final class JsString implements JsValue {

    protected final String value;

    protected JsString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Utils.format(value);
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        }
        final JsString that = (JsString) rhs;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public <T> T match(
            Functions.F<JsNull, T> fNull,
            Functions.F<JsBool, T> fBool,
            Functions.F<JsNumber, T> fNum,
            Functions.F<JsString, T> fStr,
            Functions.F<JsArray, T> fArr,
            Functions.F<JsObject, T> fObj) {
        return fStr.apply(this);
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public JsString asString() {
        return this;
    }
}
