package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.functions.Functions;

import java.util.Objects;

public final class JSNumber extends AbstractJSValue {

    private final Object value;

    protected JSNumber(Number value) {
        this.value = Objects.requireNonNull(value);
    }

    protected JSNumber(String value) {
        this.value = value;
    }

    public byte toByte() {
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else {
            return Byte.parseByte(value.toString());
        }
    }

    public short toShort() {
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else {
            return Short.parseShort(value.toString());
        }
    }

    public int toInt() {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return Integer.parseInt(value.toString());
        }
    }

    public long toLong() {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            return Long.parseLong(value.toString());
        }
    }

    public float toFloat() {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else {
            return Float.parseFloat(value.toString());
        }
    }

    public double oDouble() {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            return Double.parseDouble(value.toString());
        }
    }

    @Override
    public String toString() {
        return Utils.formatAsNumber(value);
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final JSNumber rhs = (JSNumber) obj;
        return value.equals(rhs.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public <T> T match(
            Functions.F<JSNull, T> nl,
            Functions.F<JSBool, T> bl,
            Functions.F<JSNumber, T> nm,
            Functions.F<JSString, T> st,
            Functions.F<JSArray, T> ar,
            Functions.F<JSObject, T> ob) {
        return nm.apply(this);
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public JSNumber asNumber() {
        return this;
    }
}
