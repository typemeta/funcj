package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.functions.Functions;

/**
 * Models a JSON string.
 */
public final class JSString extends AbstractJSValue {

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
            Functions.F<JSNull, T> fNull,
            Functions.F<JSBool, T> fBool,
            Functions.F<JSNumber, T> fNum,
            Functions.F<JSString, T> fStr,
            Functions.F<JSArray, T> fArr,
            Functions.F<JSObject, T> fObj) {
        return fStr.apply(this);
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
