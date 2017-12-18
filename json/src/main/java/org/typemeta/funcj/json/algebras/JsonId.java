package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.json.model.*;

import java.util.*;

/**
 * An identity object algebra,
 * i.e. one that creates an identical copy of the original object.
 */
public class JsonId implements JsonAlg<JsValue> {
    @Override
    public JsValue nul() {
        return JSAPI.nul();
    }

    @Override
    public JsValue bool(boolean b) {
        return JSAPI.bool(b);
    }

    @Override
    public JsValue num(Object value) {
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
