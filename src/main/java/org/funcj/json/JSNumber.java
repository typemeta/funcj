package org.funcj.json;

import org.funcj.document.*;
import org.funcj.util.Functions;

import java.util.Objects;

public final class JSNumber extends AbstractJSValue {

    public static JSNumber of(double value) {
        return new JSNumber(value);
    }

    private final double value;

    protected JSNumber(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return JsonUtils.format(value);
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
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        }
        final JSNumber that = (JSNumber) rhs;
        return Double.compare(that.value, value) == 0;
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
