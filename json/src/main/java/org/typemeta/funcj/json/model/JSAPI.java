package org.typemeta.funcj.json.model;

import org.typemeta.funcj.data.IList;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

public class JSAPI {

    public static JsNull nul() {
        return JsNull.NULL;
    }

    public static JsBool bool(boolean value) {
        return value ? JsBool.TRUE : JsBool.FALSE;
    }

    public static JsArray arr(JsValue... values) {
        return new JsArray(Arrays.asList(values));
    }

    public static JsArray arr(List<JsValue> values) {
        return new JsArray(values);
    }

    public static JsArray arr(IList<JsValue> values) {
        return new JsArray(values.toList());
    }

    public static JsNumber num(byte value) {
        return new JsNumber(value);
    }

    public static JsNumber num(short value) {
        return new JsNumber(value);
    }

    public static JsNumber num(int value) {
        return new JsNumber(value);
    }

    public static JsNumber num(long value) {
        return new JsNumber(value);
    }

    public static JsNumber num(float value) {
        return new JsNumber(value);
    }

    public static JsNumber num(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("JsNumber does not allow " + value);
        } else {
            return new JsNumber(value);
        }
    }

    public static JsString str(String value) {
        return new JsString(value);
    }

    public static JsObject.Field field(String name, JsValue node) {
        return new JsObject.Field(name, node);
    }

    public static JsObject obj(JsObject.Field... fields) {
        final LinkedHashMap<String, JsObject.Field> fieldMap =
                Arrays.stream(fields)
                        .collect(toFieldMap());
        return obj(fieldMap);
    }

    public static JsObject obj(Iterable<JsObject.Field> iter) {
        final LinkedHashMap<String, JsObject.Field> fieldMap =
                StreamSupport.stream(iter.spliterator(), false)
                        .collect(toFieldMap());
        return obj(fieldMap);
    }

    public static JsObject obj(LinkedHashMap<String, JsValue> fields) {
        final LinkedHashMap<String, JsObject.Field> fieldMap =
                fields.entrySet()
                        .stream()
                        .collect(toMap(
                                Map.Entry::getKey,
                                en -> JSAPI.field(en.getKey(), en.getValue()),
                                JSAPI::duplicateKeyError,
                                LinkedHashMap::new
                        ));
        return obj(fieldMap);
    }

    public static JsObject obj(Map<String, JsObject.Field> values) {
        return new JsObject(values);
    }

    private static Collector<JsObject.Field, ?, LinkedHashMap<String, JsObject.Field>> toFieldMap() {
        return toMap(
                JsObject.Field::getName,
                Function.identity(),
                JSAPI::duplicateKeyError,
                LinkedHashMap::new
        );
    }

    private static <T> T duplicateKeyError(T u, T v) {
        throw new IllegalStateException("Duplicate keys - " + u + " & " + v);
    }
}
