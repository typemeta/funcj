package org.typemeta.funcj.json;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.toMap;

public final class JSObject extends AbstractJSValue
        implements Iterable<JSObject.Field> {

    public static class Field {
        public final String name;
        public final JSValue value;

        public Field(String name, JSValue value) {
            this.name = Objects.requireNonNull(name);
            this.value = Objects.requireNonNull(value);
        }

        public String getName() {
            return name;
        }

        public JSValue getValue() {
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

    public static Field field(String name, JSValue node) {
        return new Field(name, node);
    }

    public static JSObject of(Field... fields) {
        final LinkedHashMap<String, Field> fieldMap =
                Arrays.stream(fields)
                        .collect(toMap(
                                Field::getName,
                                Function.identity(),
                                Utils::duplicateKeyError,
                                LinkedHashMap::new
                        ));
        return of(fieldMap);
    }

    public static JSObject of(Iterable<Field> iter) {
        final LinkedHashMap<String, Field> fieldMap =
                StreamSupport.stream(iter.spliterator(), false)
                        .collect(toMap(
                                Field::getName,
                                Function.identity(),
                                Utils::duplicateKeyError,
                                LinkedHashMap::new
                        ));
        return of(fieldMap);
    }

    public static JSObject of(LinkedHashMap<String, Field> values) {
        return new JSObject(values);
    }

    private final LinkedHashMap<String, Field> fields;

    protected JSObject(LinkedHashMap<String, Field> fields) {
        this.fields = fields;
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }

    public int size() {
        return fields.size();
    }

    public boolean containsName(String name) {
        return fields.containsKey(name);
    }

    public JSValue get(String name) {
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
    public Document toDocument() {
        return API.enclose(
                API.text('{'),
                API.text(", "),
                API.text('}'),
                Functors.map(Utils::toDoc, fields.values())
        );
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append('{');
        boolean first = true;
        for (Field field : fields.values()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            Utils.format(field.name, sb).append(':');
            field.value.toString(sb);
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
    public boolean isObject() {
        return true ;
    }

    @Override
    public JSObject asObject() {
        return this;
    }
}
