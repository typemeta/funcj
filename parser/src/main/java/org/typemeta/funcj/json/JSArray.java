package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Functors;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Models a JSON array.
 */
public final class JSArray extends AbstractJSValue
        implements Iterable<JSValue> {

    private final List<JSValue> values;

    protected JSArray(List<JSValue> values) {
        this.values = values;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

    public JSValue get(int index) {
        return values.get(index);
    }

    @Override
    public Iterator<JSValue> iterator() {
        final Iterator<JSValue> iter = values.iterator();

        return new Iterator<JSValue>() {

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public JSValue next() {
                return iter.next();
            }
        };
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
            Functions.F<JSNull, T> fNull,
            Functions.F<JSBool, T> fBool,
            Functions.F<JSNumber, T> fNum,
            Functions.F<JSString, T> fStr,
            Functions.F<JSArray, T> fArr,
            Functions.F<JSObject, T> fObj) {
        return fArr.apply(this);
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
