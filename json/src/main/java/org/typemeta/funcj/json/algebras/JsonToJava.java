package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.json.comb.JsonCombParser;
import org.typemeta.funcj.json.model.JsonAlg;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * An algebra for converting JsValue objects to raw java data.
 */
class JsonToJava implements JsonAlg<Object> {
    public static final Object NULL = new Object() {
        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == null || obj.equals(this);
        }

        @Override
        public String toString() {
            return "null";
        }
    };

    /**
     * Transform a JSON string into a raw Java equivalent.
     * @param json      the JSON string
     * @return          the raw Java value
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object> toJava(String json) {
        return JsonCombParser.parse(json)
                .map(jsv -> (LinkedHashMap<String, Object>)jsv.apply(JsonToJava.INSTANCE))
                .getOrThrow();
    }

    public static final JsonToJava INSTANCE = new JsonToJava();

    @Override
    public Object nul() {
        // We need to map a JsNull value to a NULL object (and not to a null value),
        // as the algebra engine won't allow nulls.
        return NULL;
    }

    @Override
    public Object bool(boolean b) {
        return b;
    }

    @Override
    public Object num(double v) {
        return v;
    }

    @Override
    public Object str(String s) {
        return s;
    }

    @Override
    public Object arr(List<Object> list) {
        return list;
    }

    @Override
    public Object obj(LinkedHashMap<String, Object> map) {
        return map;
    }
}
