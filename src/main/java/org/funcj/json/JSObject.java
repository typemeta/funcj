package org.funcj.json;

import org.funcj.document.*;
import org.funcj.util.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public final class JSObject extends AbstractJSValue
        implements Iterable<Map.Entry<String, JSValue>> {

    public final LinkedHashMap<String, JSValue> fields;

    public JSObject(LinkedHashMap<String, JSValue> fields) {
        this.fields = fields;
    }

    public int size() {
        return fields.size();
    }

    public JSValue get(String name) {
        return fields.get(name);
    }

    @Override
    public Iterator<Map.Entry<String, JSValue>> iterator() {
        return fields.entrySet().iterator();
    }

    public Stream<Map.Entry<String, JSValue>> stream() {
        return fields.entrySet().stream();
    }

    public void forEach(BiConsumer<? super String, ? super JSValue> action) {
        fields.forEach(action);
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    @Override
    public Document toDocument() {
        return API.enclose(
                API.text('{'),
                API.text(", "),
                API.text('}'),
                Functors.map(JsonUtils::toDoc, fields.entrySet())
        );
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, JSValue> en : fields.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            JsonUtils.format(en.getKey(), sb).append(':');
            en.getValue().toString(sb);
        }
        return sb.append('}');
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        }
        final JSObject that = (JSObject) rhs;
        return fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    @Override
    public <T> T match(
            Functions.F<JSNull, T> nl,
            Functions.F<JSBool, T> bl,
            Functions.F<JSNumber, T> nm,
            Functions.F<JSString, T> st,
            Functions.F<JSArray, T> ar,
            Functions.F<JSObject, T> ob) {
        return ob.apply(this);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isBool() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isObject() {
        return true ;
    }

    @Override
    public JSNull asNull() {
        throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
    }

    @Override
    public JSBool asBool() {
        throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
    }

    @Override
    public JSNumber asNumber() {
        throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
    }

    @Override
    public JSString asString() {
        throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
    }

    @Override
    public JSArray asArray() {
        throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
    }

    @Override
    public JSObject asObject() {
        return this;
    }
}
