package org.funcj.json;

import org.funcj.document.*;
import org.funcj.util.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class JSArray extends AbstractJSValue
        implements Iterable<JSValue> {

    protected final List<JSValue> values;

    public JSArray(List<JSValue> values) {
        this.values = values;
    }

    public int size() {
        return values.size();
    }

    public JSValue get(int index) {
        return values.get(index);
    }

    @Override
    public Iterator<JSValue> iterator() {
        return values.iterator();
    }

    public Stream<JSValue> stream() {
        return values.stream();
    }

    public void forEach(Consumer<? super JSValue> action) {
        values.forEach(action);
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    @Override
    public Document toDocument() {
        return API.enclose(
                API.text('['),
                API.text(", "),
                API.text(']'),
                Functors.map(JSValue::toDocument, values)
        );
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append('[');
        boolean first = true;
        for (JSValue n : values) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            n.toString(sb);
        }
        return sb.append(']');
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        }
        final JSArray that = (JSArray) rhs;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public <T> T match(
            Functions.F<JSNull, T> nl,
            Functions.F<JSBool, T> bl,
            Functions.F<JSNumber, T> nm,
            Functions.F<JSString, T> st,
            Functions.F<JSArray, T> ar,
            Functions.F<JSObject, T> ob) {
        return ar.apply(this);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public JSArray asArray() {
        return this;
    }
}
