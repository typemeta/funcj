package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.json.model.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * An identity object algebra,
 * i.e. one that creates an identical copy of the original object.
 * Since JsValue objects are immutable, this algebra isn't that useful in itself,
 * however it can be used as a base class for algebras that only modify one or two
 * JsValue types.
 */
public class JsonId implements JsonAlg<JsValue> {
    public static final JsonId INSTANCE = new JsonId();

    @Override
    public JsValue nul() {
        return JSAPI.nul();
    }

    @Override
    public JsValue bool(boolean b) {
        return JSAPI.bool(b);
    }

    @Override
    public JsValue num(double value) {
        return JSAPI.num(value);
    }

    @Override
    public JsValue str(String s) {
        return JSAPI.str(s);
    }

    @Override
    public JsValue arr(List<JsValue> elems) {
        return JSAPI.arr(elems);
    }

    @Override
    public JsValue obj(LinkedHashMap<String, JsValue> fields) {
        return JSAPI.obj(fields);
    }
}
