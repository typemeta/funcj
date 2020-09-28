package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

import java.util.Objects;

/**
 * Models a JSON number value.
 */
public final class JsNumber implements JsValue {

    private final double value;

    protected JsNumber(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }

    public byte byteValue() {
        return (byte)value;
    }

    public short shortValue() {
        return (short)value;
    }

    public int intValue() {
        return (int)value;
    }

    public long longValue() {
        return (long)value;
    }

    public float floatValue() {
        return (float)value;
    }

    public double doubleValue() {
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
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            final JsNumber rhsT = (JsNumber) obj;
            return value == rhsT.value;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public Type type() {
        return Type.NUMBER;
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
