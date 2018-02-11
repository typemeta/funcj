package org.typemeta.funcj.jsonp.algebras;

import javax.json.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * An identity object algebra,
 * i.e. one that creates an identical copy of the original object.
 */
public class JsonId implements JsonAlg<JsonValue> {
    @Override
    public JsonValue nul() {
        return JsonValue.NULL;
    }

    @Override
    public JsonValue bool(boolean b) {
        return b ? JsonValue.TRUE : JsonValue.FALSE;
    }

    @Override
    public JsonValue num(int value) {
        return Json.createValue(value);
    }

    @Override
    public JsonValue num(double value) {
        return Json.createValue(value);
    }

    @Override
    public JsonValue num(BigDecimal value) {
        return Json.createValue(value);
    }

    @Override
    public JsonValue str(String s) {
        return Json.createValue(s);
    }

    @Override
    public JsonValue arr(List<JsonValue> elems) {
        final JsonArrayBuilder jab = Json.createArrayBuilder();
        elems.forEach(jab::add);
        return jab.build();
    }

    @Override
    public JsonValue obj(Map<String, JsonValue> fields) {
        final JsonObjectBuilder jab = Json.createObjectBuilder();
        fields.forEach(jab::add);
        return jab.build();
    }
}
