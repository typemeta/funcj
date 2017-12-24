package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

import java.util.Objects;

/**
 * Models a JSON number value.
 */
public final class JsNumber implements JsValue {

    private final double value;

    protected JsNumber(double value) {
        this.value = Objects.requireNonNull(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Utils.format(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final JsNumber rhs = (JsNumber) obj;
        return value == rhs.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public <T> T match(
            Functions.F<JsNull, T> fNull,
            Functions.F<JsBool, T> fBool,
            Functions.F<JsNumber, T> fNum,
            Functions.F<JsString, T> fStr,
            Functions.F<JsArray, T> fArr,
            Functions.F<JsObject, T> fObj) {
        return fNum.apply(this);
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public JsNumber asNumber() {
        return this;
    }
}
