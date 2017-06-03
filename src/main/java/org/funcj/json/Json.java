package org.funcj.json;

import org.funcj.data.*;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public abstract class Json {
    public static JSNull nul() {
        return JSNull.INSTANCE;
    }

    public static JSBool bool(boolean value) {
        return value ? JSBool.TRUE : JSBool.FALSE;
    }

    public static JSNumber number(double value) {
        return new JSNumber(value);
    }

    public static JSString string(String value) {
        return new JSString(value);
    }

    public static JSArray array(JSValue... values) {
        return new JSArray(Arrays.asList(values));
    }

    public static JSArray array(List<JSValue> values) {
        return new JSArray(values);
    }

    public static Tuple2<String, JSValue> entry(String name, JSValue node) {
        return Tuple2.of(name, node);
    }

    public static JSObject object(Tuple2<String, JSValue>... fields) {
        return object(IList.ofArray(fields));
    }

    public static JSObject object(IList<Tuple2<String, JSValue>> fields) {
        final LinkedHashMap<String, JSValue> m =
                fields.stream().collect(
                        toMap(
                                Tuple2::get1,
                                Tuple2::get2,
                                JsonUtils::duplicateKeyError,
                                LinkedHashMap::new
                        )
                );
        return new JSObject(m);
    }

    public static JSObject object(LinkedHashMap<String, JSValue> values) {
        return new JSObject(values);
    }
}
