package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.util.Functions;

public final class JSString extends AbstractJSValue {

    public static JSString of(String value) {
        return new JSString(value);
    }

    protected final String value;

    protected JSString(String value) {
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
    public Document toDocument() {
        return API.text(toString());
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return Utils.format(value, sb);
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        }
        final JSString that = (JSString) rhs;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public <T> T match(
            Functions.F<JSNull, T> nl,
            Functions.F<JSBool, T> bl,
            Functions.F<JSNumber, T> nm,
            Functions.F<JSString, T> st,
            Functions.F<JSArray, T> ar,
            Functions.F<JSObject, T> ob) {
        return st.apply(this);
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public JSString asString() {
        return this;
    }
}
