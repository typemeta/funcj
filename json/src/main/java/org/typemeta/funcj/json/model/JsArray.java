package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Models a JSON array.
 */
public final class JsArray
        implements Iterable<JsValue>, JsValue {

    private final List<JsValue> values;

    protected JsArray(List<JsValue> values) {
        this.values = values;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

    public JsValue get(int index) {
        return values.get(index);
    }

    @Override
    public Iterator<JsValue> iterator() {
        final Iterator<JsValue> iter = values.iterator();

        return new Iterator<JsValue>() {

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public JsValue next() {
                return iter.next();
            }
        };
    }

    public Stream<JsValue> stream() {
        return values.stream();
    }

    public void forEach(Consumer<? super JsValue> action) {
        values.forEach(action);
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        }
        final JsArray that = (JsArray) rhs;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public <T> T match(
            Functions.F<JsNull, T> fNull,
            Functions.F<JsBool, T> fBool,
            Functions.F<JsNumber, T> fNum,
            Functions.F<JsString, T> fStr,
            Functions.F<JsArray, T> fArr,
            Functions.F<JsObject, T> fObj) {
        return fArr.apply(this);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public JsArray asArray() {
        return this;
    }
}
