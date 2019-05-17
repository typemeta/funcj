package org.typemeta.funcj.json.model;

import org.typemeta.funcj.functions.Functions;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Models a JSON object.
 */
public final class JsObject
        implements Iterable<JsObject.Field>, JsValue {

    public static class Field {
        private final String name;
        private final JsValue value;

        public Field(String name, JsValue value) {
            this.name = Objects.requireNonNull(name);
            this.value = Objects.requireNonNull(value);
        }

        public String name() {
            return name;
        }

        public JsValue value() {
            return value;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Field field = (Field) o;
            return Objects.equals(name, field.name) &&
                    Objects.equals(value, field.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }
    }

    private final LinkedHashMap<String, Field> fields;

    protected JsObject(Map<String, Field> fields) {
        this.fields = new LinkedHashMap<String, Field>(fields);
    }

    protected JsObject(LinkedHashMap<String, Field> fields) {
        this.fields = fields;
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }

    public int size() {
        return fields.size();
    }

    public Set<String> keySet() {
        return fields.keySet();
    }

    public boolean containsName(String name) {
        return fields.containsKey(name);
    }

    public JsValue get(String name) {
        return fields.get(name).value;
    }

    @Override
    public Iterator<Field> iterator() {
        final Iterator<Map.Entry<String, Field>> iter = fields.entrySet().iterator();
        return new Iterator<Field>() {

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Field next() {
                return iter.next().getValue();
            }
        };
    }

    public Stream<Field> stream() {
        return fields.values().stream();
    }

    public void forEach(Consumer<? super Field> action) {
        fields.values().forEach(action);
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
        final JsObject that = (JsObject) rhs;
        return fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    @Override
    public Type type() {
        return Type.OBJECT;
    }

    @Override
    public <T> T match(
            Functions.F<JsNull, T> fNull,
            Functions.F<JsBool, T> fBool,
            Functions.F<JsNumber, T> fNum,
            Functions.F<JsString, T> fStr,
            Functions.F<JsArray, T> fArr,
            Functions.F<JsObject, T> fObj) {
        return fObj.apply(this);
    }

    @Override
    public boolean isObject() {
        return true ;
    }

    @Override
    public JsObject asObject() {
        return this;
    }
}
