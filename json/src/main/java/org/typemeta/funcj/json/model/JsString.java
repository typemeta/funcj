package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

import java.util.Objects;

/**
 * Models a JSON string.
 */
public final class JsString implements JsValue {

    private final String value;

    protected JsString(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String value() {
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
        } else if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        } else {
            final JsString rhsT = (JsString) rhs;
            return value.equals(rhsT.value);
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public Type type() {
        return Type.STRING;
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
